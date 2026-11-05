import pika
import json
import os

class DocumentEventPublisher:

    def __init__(self):
        self.exchange = "document.exchange.response"
        self.routing_key = "document.event.scan-result"

        USUARIO = os.getenv('RABBITMQ_USERNAME', 'rabbit')
        PASSWORD = os.getenv('RABBITMQ_PASSWORD', 'rabbit')
        HOST = os.getenv('RABBITMQ_HOST', 'localhost')

        credentials = pika.PlainCredentials(USUARIO, PASSWORD)
        parameters = pika.ConnectionParameters(
            host=HOST,
            virtual_host="/",
            credentials=credentials
        )

        # La conexión NO se crea en __init__: quedaría inactiva durante
        # todo el escaneo y el broker la cerraría por missed heartbeats
        # (timeout 60s), provocando una excepción en basic_publish al
        # final del flujo. Ver _publish.
        self._parameters = parameters

    def publish_document_result(
        self,
        document_id: str,
        accept: bool,
        findings: list | None = None
    ):
        payload = {
            "documentId": document_id,
            "accept": accept,
            "findings": findings or []
        }

        self._publish(payload)

    def _publish(self, payload: dict):
        body = json.dumps(payload, ensure_ascii=False)
        connection = None

        try:
            # Conexión corta: se abre, se publica y se cierra. Así nunca
            # queda una conexión inactiva que el broker cierre por missed
            # heartbeats mientras el escáner trabaja.
            connection = pika.BlockingConnection(self._parameters)
            channel = connection.channel()

            channel.exchange_declare(
                exchange=self.exchange,
                exchange_type="topic",
                durable=True
            )

            channel.basic_publish(
                exchange=self.exchange,
                routing_key=self.routing_key,
                body=body,
                properties=pika.BasicProperties(
                    delivery_mode=2,
                    content_type="application/json"
                )
            )

            print(
                f"[Publisher] Resultado publicado para el documento "
                f"{payload['documentId']}: accept={payload['accept']}."
            )
        finally:
            if connection is not None and connection.is_open:
                connection.close()

    def close(self):
        """No-op: la conexión se abre y cierra en cada _publish."""
        return None

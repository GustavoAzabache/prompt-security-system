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

        self.connection = pika.BlockingConnection(parameters)
        self.channel = self.connection.channel()

        self.channel.exchange_declare(
            exchange=self.exchange,
            exchange_type="topic",
            durable=True
        )

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
        self.channel.basic_publish(
            exchange=self.exchange,
            routing_key=self.routing_key,
            body=json.dumps(payload, ensure_ascii=False),
            properties=pika.BasicProperties(
                delivery_mode=2,
                content_type="application/json"
            )
        )

        print(
            f"[Publisher] Resultado publicado para el documento "
            f"{payload['documentId']}: accept={payload['accept']}."
        )

    def close(self):
        if self.connection and self.connection.is_open:
            self.connection.close()

import pika
import json

class DocumentEventPublisher:

    def __init__(self):
        self.exchange = "document.events.exchange"
        self.queue = "document.scan.result.queue"
        self.routing_key = "DOCUMENT_SCAN_RESULT"

        credentials = pika.PlainCredentials("rabbit", "rabbit")

        parameters = pika.ConnectionParameters(
            host="localhost",
            virtual_host="/",
            credentials=credentials
        )

        self.connection = pika.BlockingConnection(parameters)
        self.channel = self.connection.channel()

        self.channel.exchange_declare(
            exchange=self.exchange,
            exchange_type="direct",
            durable=True
        )

        self.channel.queue_declare(
            queue=self.queue,
            durable=True
        )

        self.channel.queue_bind(
            exchange=self.exchange,
            queue=self.queue,
            routing_key=self.routing_key
        )

    def publish_document_result(
        self,
        document_id: str,
        filename: str,
        accept: bool,
        findings: list | None = None
    ):
        payload = {
            "documentId": document_id,
            "filename": filename,
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
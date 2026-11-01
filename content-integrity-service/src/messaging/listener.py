import pika, json

def start_listener(callback_funcion):
    USUARIO = 'rabbit'
    PASSWORD = 'rabbit'
    HOST = 'localhost'
    VIRTUAL_HOST = '/'

    credentials = pika.PlainCredentials(USUARIO, PASSWORD)
    parameters = pika.ConnectionParameters(
        host=HOST,
        virtual_host=VIRTUAL_HOST,
        credentials=credentials
    )

    conn = pika.BlockingConnection(parameters)
    ch = conn.channel()

    EXCHANGE_PUBLISHER = 'document.exchange.request'
    QUEUE_TARGET = 'document.queue.scan'
    ROUTING_KEY = 'document.event.upload'

    ch.exchange_declare(exchange=EXCHANGE_PUBLISHER, exchange_type='topic', durable=True)
    ch.queue_declare(queue=QUEUE_TARGET, durable=True) 
    ch.queue_bind(exchange=EXCHANGE_PUBLISHER, queue=QUEUE_TARGET, routing_key=ROUTING_KEY)

    def on_message(ch, method, props, body):
        try:
            payload = json.loads(body)
            doc_id = payload.get('documentId')
            filename = payload.get('filename', 'Unknown_File')

            if doc_id: 
                callback_funcion(doc_id, filename)
            ch.basic_ack(method.delivery_tag)
        except Exception as e:
            print(f" [!] Error al procesar mensaje: {e}")
            # Si falla, no lo volvemos a encolar (requeue=False) para evitar bucles infinitos
            ch.basic_nack(method.delivery_tag, requeue=False)

    # Escuchamos directamente de la cola gestionada por document-service
    ch.basic_consume(QUEUE_TARGET, on_message)
    print(f" [*] Conectado con éxito. Escuchando la cola '{QUEUE_TARGET}' de RabbitMQ...")
    ch.start_consuming()
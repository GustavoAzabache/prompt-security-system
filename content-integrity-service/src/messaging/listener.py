import pika, json
import os

EXCHANGE_PUBLISHER = 'document.exchange.request'
QUEUE_TARGET = 'document.queue.scan'
ROUTING_KEY = 'document.event.upload'


def _connection_parameters():
    USUARIO = os.getenv('RABBITMQ_USERNAME', 'rabbit')
    PASSWORD = os.getenv('RABBITMQ_PASSWORD', 'rabbit')
    HOST = os.getenv('RABBITMQ_HOST', 'localhost')
    VIRTUAL_HOST = '/'

    credentials = pika.PlainCredentials(USUARIO, PASSWORD)
    return pika.ConnectionParameters(
        host=HOST,
        virtual_host=VIRTUAL_HOST,
        credentials=credentials
    )


def _declare_topology(channel):
    """Crea exchange, cola y binding (idempotente)."""
    channel.exchange_declare(exchange=EXCHANGE_PUBLISHER, exchange_type='topic', durable=True)
    channel.queue_declare(queue=QUEUE_TARGET, durable=True)
    channel.queue_bind(exchange=EXCHANGE_PUBLISHER, queue=QUEUE_TARGET, routing_key=ROUTING_KEY)


def declare_topology():
    """Declara la cola y su binding, y cierra la conexión.

    Se llama ANTES de cargar el modelo LLM-Guard (tarda minutos): así la
    cola existe desde el primer segundo del arranque y los mensajes que
    se publiquen mientras el modelo carga esperan en la cola en vez de
    descartarse por no tener binding.

    La conexión se cierra enseguida a propósito: si quedara abierta e
    inactiva durante la carga del modelo, el broker la cerraría por
    missed heartbeats (timeout 60s).
    """
    conn = pika.BlockingConnection(_connection_parameters())
    try:
        _declare_topology(conn.channel())
        print(f" [*] Topología declarada. Cola '{QUEUE_TARGET}' lista para recibir.")
    finally:
        if conn.is_open:
            conn.close()


def start_listener(callback_funcion):
    conn = pika.BlockingConnection(_connection_parameters())
    ch = conn.channel()

    # Idempotente: ya se declaró en declare_topology(), se repite por si
    # el broker se reinició mientras el modelo cargaba.
    _declare_topology(ch)

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

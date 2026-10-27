import pika, json

def start_listener(callback_funcion):
    USUARIO = 'rabbit'
    PASSWORD = 'rabbit'
    HOST = 'localhost'
    VIRTUAL_HOST = '/'

    credenciales = pika.PlainCredentials(USUARIO, PASSWORD)
    parametros = pika.ConnectionParameters(
        host=HOST,
        virtual_host=VIRTUAL_HOST,
        credentials=credenciales
    )

    # Conectar usando los parámetros con credenciales
    conn = pika.BlockingConnection(parametros)
    ch = conn.channel()
    
    COLA_OBJETIVO = 'document.scan.queue'
    ch.queue_declare(queue=COLA_OBJETIVO, durable=True) 

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
    ch.basic_consume(COLA_OBJETIVO, on_message)
    print(f" [*] Conectado con éxito. Escuchando la cola '{COLA_OBJETIVO}' de RabbitMQ...")
    ch.start_consuming()
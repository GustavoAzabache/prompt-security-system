from clients.get_token_client import GetTokenClient
from clients.document_client import DocumentClient
from services.pdf_analyzer_service import PdfAnalyzerService
from messaging.listener import start_listener
from messaging.publisher import DocumentEventPublisher
import requests

# Inicialización modular de dependencias
token_client = GetTokenClient()
document_client = DocumentClient()
analyzer_service = PdfAnalyzerService()
event_publisher = DocumentEventPublisher()

def document_orchestrator_controller(document_id: str, filename: str):
    """Controlador central: Dirige el flujo de datos y gestiona los veredictos de seguridad."""
    print(f"\n[Content_Integrity_Service] === Iniciando Auditoría para: '{filename}' ===")
    
    try:
        # Paso 1: Petición síncrona del token de acceso con scope "documents:scan"
        print("[Content_Integrity_Service] -> Solicitando token OAuth2 M2M...")
        token = token_client.get_access_token()
        
        # Paso 2: Descarga del PDF en formato Stream de bytes
        print("[Content_Integrity_Service] -> Descargando stream binario desde Spring Boot...")
        pdf_bytes = document_client.download_pdf_stream(document_id, token)
        
        # Paso 3: Delegación del análisis (PyMuPDF -> Markdown -> LLM-Guard)
        print("[Content_Integrity_Service] -> Enviando datos al motor de análisis de contenido...")
        veredicto = analyzer_service.analyze_content(pdf_bytes)
        
        # Paso 4: Tomar decisiones con la respuesta estructurada recibida
        if not veredicto["success"]:
            print(
                "[Content_Integrity_Service] El análisis no pudo completarse."
            )
            raise RuntimeError(
                f"No se pudo analizar el documento {document_id}"
            )

        print(f"[Content_Integrity_Service] Veredicto final recibido:")
        print(f"               ¿Documento Seguro?: {veredicto['is_valid']}")
        print(f"               Nivel de Riesgo (LLM-Guard): {veredicto['risk_score']}")
        
        event_publisher.publish_document_result(
            document_id=document_id,
            filename=filename,
            accept=veredicto["is_valid"],
            findings=veredicto["findings"]
        )

    except PermissionError as e:
        print(f"[Content_Integrity_Service] Error de Autenticación/Autorización con Spring Boot: {e}")
    except requests.exceptions.RequestException as e:
        print(f"[Content_Integrity_Service] Fallo de conexión de red con los microservicios: {e}")
        raise e  # Relanzamos para provocar el basic_nack de RabbitMQ y reintentar el mensaje luego
    except Exception as e:
        print(f"[Content_Integrity_Service] Excepción inesperada en el hilo de ejecución: {e}")
        raise e

if __name__ == "__main__":
    print(" [▲] Guardián de Contenido M2M inicializado.")
    # El controlador se acopla como el callback definitivo del listener asíncrono
    start_listener(document_orchestrator_controller)

import requests, os

class DocumentClient:
    def __init__(self):
        self.base_url = os.getenv(
            "DOCUMENT_SERVICE_URL",
            "http://localhost:8082/api/v1/documents/internal"
        )

    def download_pdf_stream(self, document_id: str, token: str) -> bytes:
        """Establece conexión por stream con el Resource Server y extrae los bytes."""
        url = f"{self.base_url}/{document_id}/content"
        headers = {"Authorization": f"Bearer {token}"}
        
        # Uso de context manager para asegurar el cierre de la conexión de red
        with requests.get(url, headers=headers, stream=True) as response:
            if response.status_code in (401, 403):
                raise PermissionError(f"Error de Seguridad ({response.status_code}): Token inválido o scope insuficiente.")
            
            if response.status_code != 200:
                raise RuntimeError(f"Error en Resource Server de Spring Boot. Status: {response.status_code}")
            
            return response.content

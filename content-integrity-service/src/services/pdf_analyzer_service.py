import fitz
from llm_guard.input_scanners import PromptInjection


class PdfAnalyzerService:

    def __init__(self):
        # Inicializamos el escáner una sola vez al levantar el servicio
        # para evitar recargarlo por cada mensaje de RabbitMQ.
        print("[Analyzer] Inicializando escáneres de LLM-Guard...")
        self.injection_scanner = PromptInjection()

    def analyze_content(self, pdf_bytes: bytes) -> dict:
        """
        Extrae el texto nativo del PDF mediante PyMuPDF
        y lo analiza con LLM-Guard.
        Retorna un veredicto estructurado.
        """
        resultado = {
            "success": False,
            "is_valid": True,
            "risk_score": 0.0,
            "findings": []
        }

        try:
            # 1. Extraer el texto nativo del PDF
            with fitz.open(stream=pdf_bytes, filetype="pdf") as doc:
                text = "\n".join(
                    page.get_text("text", sort=False)
                    for page in doc
                )

            if not text.strip():
                resultado["findings"].append(
                    "El documento no contiene texto nativo legible."
                )
                resultado["success"] = True
                return resultado

            print(
                f"[Analyzer] Texto extraído del PDF con éxito. "
                f"Longitud: {len(text)} caracteres."
            )

            # 2. Analizar el texto extraído con LLM-Guard
            _, is_valid, risk_score = self.injection_scanner.scan(text)

            resultado["is_valid"] = is_valid
            resultado["risk_score"] = risk_score
            resultado["success"] = True

            if not is_valid:
                resultado["findings"].append(
                    "Alerta de seguridad: Posible Prompt Injection "
                    f"detectado. Riesgo: {risk_score}"
                )
                print(
                    "[⚠️ ALERTA] Amenaza detectada por LLM-Guard. "
                    f"Score: {risk_score}"
                )
            else:
                print(
                    "[✓] LLM-Guard no detectó amenazas de inyección "
                    "en el texto."
                )

        except Exception as e:
            print(f"[Analyzer X] Error procesando PDF/LLM-Guard: {e}")
            resultado["findings"].append(
                f"Error interno de análisis: {str(e)}"
            )

        return resultado
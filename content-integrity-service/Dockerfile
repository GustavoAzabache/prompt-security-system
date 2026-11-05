FROM python:3.12-slim

WORKDIR /app

# Install system dependencies required by some Python packages
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    g++ \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements first for better caching
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# 1b) Modelo de LLM-Guard bakeado en la imagen.
#
# PromptInjection() corre con use_onnx=False (optimum no esta instalado),
# asi que solo se necesitan los pesos en safetensors + tokenizer.
# NO se descarga onnx/model.onnx (~704 MB) porque no se usa.
#
# Al quedar dentro de la imagen, el arranque del worker no depende de la
# red: sin esto, cada contenedor nuevo tarda decenas de minutos bajando
# el modelo y mientras tanto no declara la cola de RabbitMQ.
ARG HF_MODEL_REPO=protectai/deberta-v3-base-prompt-injection-v2
ARG HF_MODEL_REVISION=89b085cd330414d3e7d9dd787870f315957e1e9f
RUN python -c "from huggingface_hub import snapshot_download; snapshot_download('${HF_MODEL_REPO}', revision='${HF_MODEL_REVISION}', allow_patterns=['config.json', 'model.safetensors', 'tokenizer.json', 'tokenizer_config.json', 'special_tokens_map.json', 'added_tokens.json', 'spm.model'])"

# Copy source code
COPY src/ ./src/

# 2) Sin buffer: si no, los print() se acumulan en el buffer de stdout y
# `docker logs` no muestra nada hasta que se llena o el proceso termina.
ENV PYTHONUNBUFFERED=1

# Run the worker
CMD ["python", "src/main.py"]

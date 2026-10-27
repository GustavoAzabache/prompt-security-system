import os
from pathlib import Path

import requests

def load_env_file() -> None:
    env_path = Path(__file__).resolve().parents[2] / ".env"

    if not env_path.exists():
        return

    for line in env_path.read_text(encoding="utf-8").splitlines():
        line = line.strip()

        if not line or line.startswith("#") or "=" not in line:
            continue

        key, value = line.split("=", 1)
        os.environ.setdefault(key.strip(), value.strip().strip('"').strip("'"))


class GetTokenClient:

    def __init__(self):
        load_env_file()

        self.base_url = os.getenv(
            "AUTH_SERVICE_URL",
            "http://localhost:8081",
        ).rstrip("/")
        self.name = os.getenv("CONTENT_INTEGRITY_NAME")
        self.password = os.getenv("CONTENT_INTEGRITY_PASSWORD")

    def get_access_token(self) -> str:
        if not self.name or not self.password:
            raise RuntimeError(
                "Faltan CONTENT_INTEGRITY_NAME y/o CONTENT_INTEGRITY_PASSWORD"
            )

        response = requests.post(
            f"{self.base_url}/api/v1/auth/clients/login",
            json={
                "name": self.name,
                "password": self.password,
            },
            timeout=10,
        )

        if response.status_code != 200:
            raise RuntimeError(
                f"Auth-service respondió {response.status_code}: {response.text}"
            )

        return response.json()["accessToken"]

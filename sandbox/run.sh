
# Obtém o diretório onde o script está localizado
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Criar as pastas com permissões
mkdir -m 777 "$SCRIPT_DIR"/.docker
mkdir -m 777 "$SCRIPT_DIR"/.docker/keycloak
mkdir -m 777 "$SCRIPT_DIR"/.docker/es01
mkdir -m 777 "$SCRIPT_DIR"/.docker/filebeat
sudo chown root "$SCRIPT_DIR"/app/filebeat/filebeat.docker.yml

# Cria as networks docker
docker network create adm_videos_services
docker network create elastic

# Sobe os container dos composes
if ! docker compose  -f "$SCRIPT_DIR"/services/docker-compose.yml up -d; then
  echo "Falha ao inicializar os containers services"
  exit 1
fi

if ! docker compose  -f "$SCRIPT_DIR"/elk/docker-compose.yml up -d; then
  echo "Falha ao inicializar os containers elk"
  exit 1
fi

echo "inicializando os containers"
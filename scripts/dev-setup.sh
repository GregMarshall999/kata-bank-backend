set -e

echo "🚀 Setting up Kata Bank API development environment..."

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker daemon is not running. Please start Docker."
        exit 1
    fi
    
    print_status "Docker is installed and running"
}

check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    print_status "Docker Compose is installed"
}

check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_warning "Maven is not installed. You can still use Docker for development."
    else
        print_status "Maven is installed"
    fi
}

check_java() {
    if ! command -v java &> /dev/null; then
        print_warning "Java is not installed. You can still use Docker for development."
    else
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge 17 ]; then
            print_status "Java $JAVA_VERSION is installed"
        else
            print_warning "Java $JAVA_VERSION is installed, but Java 17+ is recommended"
        fi
    fi
}

create_directories() {
    print_status "Creating necessary directories..."
    mkdir -p logs
    mkdir -p nginx/ssl
    print_status "Directories created"
}

create_nginx_config() {
    if [ ! -f "nginx/nginx.conf" ]; then
        print_status "Creating nginx configuration..."
        mkdir -p nginx
        cat > nginx/nginx.conf << 'EOF'
events {
    worker_connections 1024;
}

http {
    upstream kata-bank-api {
        server kata-bank-api:8080;
    }

    server {
        listen 80;
        server_name localhost;

        location / {
            proxy_pass http://kata-bank-api;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
EOF
        print_status "Nginx configuration created"
    else
        print_status "Nginx configuration already exists"
    fi
}

create_db_init() {
    if [ ! -f "init.sql" ]; then
        print_status "Creating database initialization script..."
        cat > init.sql << 'EOF'
-- Database initialization script for Kata Bank API
-- This script will be executed when the MySQL container starts

USE kata_bank;

-- Create initial tables if they don't exist
-- (Spring Boot will handle table creation via JPA)

-- Insert any initial data here if needed
-- INSERT INTO bank_users (id, username, email, created_at) VALUES (1, 'admin', 'admin@example.com', NOW());

EOF
        print_status "Database initialization script created"
    else
        print_status "Database initialization script already exists"
    fi
}

start_services() {
    print_status "Building and starting services..."
    docker-compose up --build -d
    
    print_status "Waiting for services to be ready..."
    sleep 10
    
    if docker-compose ps | grep -q "Up"; then
        print_status "Services are running successfully!"
    else
        print_error "Some services failed to start. Check logs with: docker-compose logs"
        exit 1
    fi
}

show_service_info() {
    echo ""
    print_status "Service Information:"
    echo "  🌐 Application: http://localhost:8080"
    echo "  📚 Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "  🗄️  MySQL: localhost:3306"
    echo "  🔄 Redis: localhost:6379"
    echo "  🌍 Nginx: http://localhost:80"
    echo ""
    print_status "Useful commands:"
    echo "  📋 View logs: docker-compose logs -f kata-bank-api"
    echo "  🛑 Stop services: docker-compose down"
    echo "  🔄 Restart services: docker-compose restart"
    echo "  🧹 Clean up: docker-compose down -v"
    echo ""
}

main() {
    print_status "Starting development environment setup..."
    
    check_docker
    check_docker_compose
    check_maven
    check_java
    create_directories
    create_nginx_config
    create_db_init
    
    if [ "$1" = "--skip-start" ]; then
        print_status "Setup completed. Run 'docker-compose up -d' to start services."
    else
        start_services
        show_service_info
    fi
}

main "$@"

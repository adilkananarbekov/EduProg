# EdupageProject Management Script
# Usage: .\manage.ps1 <command>
# Commands: start, stop, restart, rebuild, logs, status, db, clean

param(
    [Parameter(Position=0)]
    [string]$Command = "help"
)

$ProjectRoot = $PSScriptRoot

function Show-Help {
    Write-Host @"
EdupageProject Management Script
================================
Usage: .\manage.ps1 <command>

Commands:
  start       Start all services (detached)
  stop        Stop all services
  restart     Restart all services
  rebuild     Rebuild and start all services
  logs        Show logs (follow mode)
  logs-backend    Show backend logs only
  logs-frontend   Show frontend logs only
  status      Show running containers
  db          Connect to PostgreSQL database
  clean       Stop services and remove volumes
  help        Show this help message

Examples:
  .\manage.ps1 start
  .\manage.ps1 logs-backend
  .\manage.ps1 db
"@
}

function Start-Services {
    Write-Host "Starting all services..." -ForegroundColor Green
    docker compose up -d --build
    Write-Host "`nServices started!" -ForegroundColor Green
    Write-Host "Frontend: http://localhost:5173" -ForegroundColor Cyan
    Write-Host "Backend:  http://localhost:8080" -ForegroundColor Cyan
}

function Stop-Services {
    Write-Host "Stopping all services..." -ForegroundColor Yellow
    docker compose down
    Write-Host "Services stopped." -ForegroundColor Green
}

function Restart-Services {
    Stop-Services
    Start-Services
}

function Rebuild-Services {
    Write-Host "Rebuilding and starting all services..." -ForegroundColor Green
    docker compose down
    docker compose up -d --build --force-recreate
    Write-Host "`nServices rebuilt and started!" -ForegroundColor Green
    Write-Host "Frontend: http://localhost:5173" -ForegroundColor Cyan
    Write-Host "Backend:  http://localhost:8080" -ForegroundColor Cyan
}

function Show-Logs {
    param([string]$Service = "")
    if ($Service) {
        docker compose logs -f $Service
    } else {
        docker compose logs -f
    }
}

function Show-Status {
    Write-Host "Container Status:" -ForegroundColor Cyan
    docker compose ps
}

function Connect-Database {
    Write-Host "Connecting to PostgreSQL..." -ForegroundColor Cyan
    docker exec -it edupage-postgres psql -U edupage -d edupage
}

function Clean-All {
    Write-Host "Stopping services and removing volumes..." -ForegroundColor Red
    docker compose down -v
    Write-Host "Cleaned up." -ForegroundColor Green
}

# Main command router
switch ($Command.ToLower()) {
    "start"         { Start-Services }
    "stop"          { Stop-Services }
    "restart"       { Restart-Services }
    "rebuild"       { Rebuild-Services }
    "logs"          { Show-Logs }
    "logs-backend"  { Show-Logs -Service "backend" }
    "logs-frontend" { Show-Logs -Service "frontend" }
    "status"        { Show-Status }
    "db"            { Connect-Database }
    "clean"         { Clean-All }
    "help"          { Show-Help }
    default         { 
        Write-Host "Unknown command: $Command" -ForegroundColor Red
        Show-Help 
    }
}

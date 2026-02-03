# EduOps Project Management Script
# Usage: .\manage.ps1 <command>

param(
    [Parameter(Position = 0)]
    [string]$Command = "help"
)

$ProjectRoot = $PSScriptRoot
$BackendDir = Join-Path $ProjectRoot "backend"
$MobileDir = Join-Path $ProjectRoot "eduprog_application_mobile"

function Show-Help {
    Write-Host @"
EduOps Project Management Script
================================
Usage: .\manage.ps1 <command>

Quick Start:
  dev               Start backend + mobile app (waits for backend)
  
Backend Commands:
  backend-start     Start Spring Boot backend
  backend-build     Build backend JAR
  backend-test      Run backend tests
  
Mobile Commands:
  mobile-run        Run Flutter app (connected device)
  mobile-build      Build Flutter APK
  mobile-analyze    Run Flutter analyze
  mobile-clean      Clean Flutter build
  mobile-deps       Get Flutter dependencies
  
Docker Commands:
  docker-start      Start all Docker services
  docker-stop       Stop Docker services
  docker-rebuild    Rebuild Docker containers
  docker-logs       Show Docker logs
  docker-status     Show container status
  db                Connect to PostgreSQL
  
Utilities:
  clean             Clean all build artifacts
  status            Show project status
  help              Show this help

Examples:
  .\manage.ps1 dev              # Start everything
  .\manage.ps1 backend-start
  .\manage.ps1 mobile-run
"@
}

# Dev Mode - Start both backend and mobile
function Start-Dev {
    Write-Host "`n=== Starting EduOps Development Environment ===" -ForegroundColor Cyan
    
    # Start backend in background
    Write-Host "`n[1/3] Starting backend..." -ForegroundColor Yellow
    $backendJob = Start-Job -ScriptBlock {
        param($dir)
        Set-Location $dir
        if (Test-Path ".\mvnw.cmd") {
            & .\mvnw.cmd spring-boot:run 2>&1
        }
        else {
            & mvn spring-boot:run 2>&1
        }
    } -ArgumentList $BackendDir
    
    # Wait for backend to be ready
    Write-Host "[2/3] Waiting for backend to start..." -ForegroundColor Yellow
    $maxWait = 60
    $waited = 0
    $ready = $false
    
    while ($waited -lt $maxWait -and -not $ready) {
        Start-Sleep -Seconds 2
        $waited += 2
        
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                $ready = $true
            }
        }
        catch {
            Write-Host "  Backend starting... ($waited/$maxWait seconds)" -ForegroundColor Gray
        }
    }
    
    if ($ready) {
        Write-Host "  Backend is ready!" -ForegroundColor Green
    }
    else {
        Write-Host "  Backend may still be starting, proceeding anyway..." -ForegroundColor Yellow
    }
    
    # Start Flutter app
    Write-Host "[3/3] Launching Flutter app..." -ForegroundColor Yellow
    Write-Host "`n=== Backend running in background (Job ID: $($backendJob.Id)) ===" -ForegroundColor Cyan
    Write-Host "To stop backend later: Stop-Job $($backendJob.Id); Remove-Job $($backendJob.Id)" -ForegroundColor Gray
    Write-Host ""
    
    Push-Location $MobileDir
    flutter run
    Pop-Location
}

# Backend Functions
function Start-Backend {
    Write-Host "Starting Spring Boot backend..." -ForegroundColor Green
    Push-Location $BackendDir
    if (Test-Path ".\mvnw.cmd") {
        .\mvnw.cmd spring-boot:run
    }
    else {
        mvn spring-boot:run
    }
    Pop-Location
}

function Build-Backend {
    Write-Host "Building backend..." -ForegroundColor Green
    Push-Location $BackendDir
    if (Test-Path ".\mvnw.cmd") {
        .\mvnw.cmd clean package -DskipTests
    }
    else {
        mvn clean package -DskipTests
    }
    Pop-Location
    Write-Host "Backend built successfully!" -ForegroundColor Green
}

function Test-Backend {
    Write-Host "Running backend tests..." -ForegroundColor Cyan
    Push-Location $BackendDir
    if (Test-Path ".\mvnw.cmd") {
        .\mvnw.cmd test
    }
    else {
        mvn test
    }
    Pop-Location
}

# Mobile Functions
function Run-Mobile {
    Write-Host "Running Flutter app..." -ForegroundColor Green
    Push-Location $MobileDir
    flutter run
    Pop-Location
}

function Reset-Mobile-Project {
    flutter clean
    flutter pub get
}

function Build-Mobile {
    Write-Host "Building Flutter APK..." -ForegroundColor Green
    Push-Location $MobileDir
    flutter build apk --release
    Pop-Location
    Write-Host "APK built: $MobileDir\build\app\outputs\flutter-apk\app-release.apk" -ForegroundColor Cyan
}

function Analyze-Mobile {
    Write-Host "Running Flutter analyze..." -ForegroundColor Cyan
    Push-Location $MobileDir
    flutter analyze
    Pop-Location
}

function Clean-Mobile {
    Write-Host "Cleaning Flutter build..." -ForegroundColor Yellow
    Push-Location $MobileDir
    flutter clean
    Pop-Location
    Write-Host "Flutter cleaned." -ForegroundColor Green
}

function Get-MobileDeps {
    Write-Host "Getting Flutter dependencies..." -ForegroundColor Green
    Push-Location $MobileDir
    flutter pub get
    Pop-Location
    Write-Host "Dependencies installed." -ForegroundColor Green
}

# Docker Functions
function Start-Docker {
    Write-Host "Starting Docker services..." -ForegroundColor Green
    docker compose up -d --build
    Write-Host "`nServices started!" -ForegroundColor Green
    Write-Host "Backend API: http://localhost:8080" -ForegroundColor Cyan
    Write-Host "PostgreSQL:  localhost:5432" -ForegroundColor Cyan
}

function Stop-Docker {
    Write-Host "Stopping Docker services..." -ForegroundColor Yellow
    docker compose down
    Write-Host "Services stopped." -ForegroundColor Green
}

function Rebuild-Docker {
    Write-Host "Rebuilding Docker services..." -ForegroundColor Green
    docker compose down
    docker compose up -d --build --force-recreate
    Write-Host "Services rebuilt!" -ForegroundColor Green
}

function Show-DockerLogs {
    docker compose logs -f
}

function Show-DockerStatus {
    Write-Host "Container Status:" -ForegroundColor Cyan
    docker compose ps
}

function Connect-Database {
    Write-Host "Connecting to PostgreSQL..." -ForegroundColor Cyan
    docker exec -it edupage-postgres psql -U edupage -d edupage
}

# Utility Functions
function Clean-All {
    Write-Host "Cleaning all build artifacts..." -ForegroundColor Yellow
    
    # Clean backend
    if (Test-Path $BackendDir) {
        Push-Location $BackendDir
        if (Test-Path ".\mvnw.cmd") {
            .\mvnw.cmd clean
        }
        Pop-Location
    }
    
    # Clean mobile
    if (Test-Path $MobileDir) {
        Push-Location $MobileDir
        flutter clean
        Pop-Location
    }
    
    Write-Host "All cleaned!" -ForegroundColor Green
}

function Show-Status {
    Write-Host "`n=== EduOps Project Status ===" -ForegroundColor Cyan
    
    # Check backend
    Write-Host "`n[Backend]" -ForegroundColor Yellow
    if (Test-Path (Join-Path $BackendDir "pom.xml")) {
        Write-Host "  Location: $BackendDir" -ForegroundColor Green
        Write-Host "  Status: Ready" -ForegroundColor Green
    }
    else {
        Write-Host "  Status: Not found" -ForegroundColor Red
    }
    
    # Check mobile
    Write-Host "`n[Mobile App]" -ForegroundColor Yellow
    if (Test-Path (Join-Path $MobileDir "pubspec.yaml")) {
        Write-Host "  Location: $MobileDir" -ForegroundColor Green
        Write-Host "  Status: Ready" -ForegroundColor Green
    }
    else {
        Write-Host "  Status: Not found" -ForegroundColor Red
    }
    
    # Check Docker
    Write-Host "`n[Docker]" -ForegroundColor Yellow
    $dockerRunning = docker compose ps --quiet 2>$null
    if ($dockerRunning) {
        Write-Host "  Status: Running" -ForegroundColor Green
        docker compose ps --format "table {{.Name}}\t{{.Status}}"
    }
    else {
        Write-Host "  Status: Not running" -ForegroundColor Yellow
    }
    
    Write-Host ""
}

# Main command router
switch ($Command.ToLower()) {
    # Quick Start
    "dev" { Start-Dev }
    
    # Backend
    "backend-start" { Start-Backend }
    "backend-build" { Build-Backend }
    "backend-test" { Test-Backend }
    
    # Mobile
    "mobile-run" { Run-Mobile }
    "mobile-build" { Build-Mobile }
    "mobile-analyze" { Analyze-Mobile }
    "mobile-clean" { Clean-Mobile }
    "mobile-deps" { Get-MobileDeps }
    
    # Docker
    "docker-start" { Start-Docker }
    "docker-stop" { Stop-Docker }
    "docker-rebuild" { Rebuild-Docker }
    "docker-logs" { Show-DockerLogs }
    "docker-status" { Show-DockerStatus }
    "db" { Connect-Database }
    
    # Legacy aliases
    "start" { Start-Docker }
    "stop" { Stop-Docker }
    "rebuild" { Rebuild-Docker }
    "logs" { Show-DockerLogs }
    
    # Utilities
    "clean" { Clean-All }
    "status" { Show-Status }
    "help" { Show-Help }
    
    default { 
        Write-Host "Unknown command: $Command" -ForegroundColor Red
        Show-Help 
    }
}

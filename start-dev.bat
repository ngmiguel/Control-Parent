@echo off
echo Démarrage de l'environnement de développement...

echo.
echo 1. Vérification du service MySQL...
sc query MySQL80 | find "RUNNING" >nul
if %ERRORLEVEL% NEQ 0 (
    echo ✗ MySQL n'est pas démarré. Démarrage en cours...
    net start MySQL80
    if %ERRORLEVEL% NEQ 0 (
        echo ✗ Impossible de démarrer MySQL. Vérifiez l'installation.
        pause
        exit /b 1
    )
) else (
    echo ✓ MySQL est déjà démarré
)

echo.
echo 2. Compilation du backend...
cd Control-Parent(back)
call mvnw clean compile
if %ERRORLEVEL% NEQ 0 (
    echo ✗ Erreur de compilation du backend
    pause
    exit /b 1
)
echo ✓ Backend compilé avec succès

echo.
echo 3. Démarrage du backend Spring Boot...
start "Backend" cmd /k "mvnw spring-boot:run"

echo.
echo 4. Attente de 15 secondes pour que le backend démarre...
timeout /t 15 /nobreak

echo.
echo 5. Installation des dépendances du frontend...
cd ../Control-Parent(front)
call npm install
if %ERRORLEVEL% NEQ 0 (
    echo ✗ Erreur d'installation des dépendances npm
    pause
    exit /b 1
)

echo.
echo 6. Démarrage du frontend Angular...
start "Frontend" cmd /k "npm start"

echo.
echo ✓ Environnement de développement démarré !
echo Backend: http://localhost:8080
echo Frontend: http://localhost:4200
echo.
echo Appuyez sur une touche pour fermer cette fenêtre...
pause
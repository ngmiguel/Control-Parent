@echo off
echo Configuration de MySQL pour le projet...

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
echo 2. Configuration de la base de données...
echo Veuillez entrer le mot de passe root de MySQL (ou appuyez sur Entrée si aucun):
set /p mysql_password=Mot de passe MySQL root: 

if "%mysql_password%"=="" (
    echo Tentative de connexion sans mot de passe...
    mysql -u root -e "CREATE DATABASE IF NOT EXISTS parent_portal; SHOW DATABASES LIKE 'parent_portal';"
) else (
    echo Tentative de connexion avec mot de passe...
    mysql -u root -p%mysql_password% -e "CREATE DATABASE IF NOT EXISTS parent_portal; SHOW DATABASES LIKE 'parent_portal';"
)

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Base de données 'parent_portal' créée/vérifiée avec succès !
    
    echo.
    echo 3. Mise à jour du fichier de configuration...
    if "%mysql_password%"=="" (
        echo Configuration sans mot de passe maintenue
    ) else (
        echo Mise à jour avec le mot de passe fourni...
        powershell -Command "(Get-Content 'Control-Parent(back)\src\main\resources\application.properties') -replace 'spring.datasource.password=', 'spring.datasource.password=%mysql_password%' | Set-Content 'Control-Parent(back)\src\main\resources\application.properties'"
    )
    
    echo.
    echo ✓ Configuration MySQL terminée !
) else (
    echo.
    echo ✗ Erreur de connexion MySQL
    echo Vérifiez le mot de passe et réessayez
)

echo.
pause
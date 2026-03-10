@echo off
echo Test de connexion MySQL...

echo.
echo 1. Vérification du service MySQL...
sc query MySQL80

echo.
echo 2. Test de connexion à la base de données...
echo Tentative de connexion à MySQL (appuyez sur Entrée si aucun mot de passe)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS parent_portal; SHOW DATABASES LIKE 'parent_portal';"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Connexion MySQL réussie !
    echo ✓ Base de données 'parent_portal' créée/vérifiée
) else (
    echo.
    echo ✗ Erreur de connexion MySQL
    echo Vérifiez que MySQL est démarré et que les identifiants sont corrects
)

echo.
pause
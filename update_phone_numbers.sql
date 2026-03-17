-- Script SQL pour normaliser les numéros de téléphone dans la base de données
-- À exécuter une seule fois

-- Mettre à jour les numéros du père (format camerounais sans +237)
UPDATE etudiant 
SET telephone_pere = CONCAT('+237', telephone_pere)
WHERE telephone_pere REGEXP '^6[0-9]{8}$';

-- Mettre à jour les numéros de la mère (format camerounais sans +237)
UPDATE etudiant 
SET telephone_mere = CONCAT('+237', telephone_mere)
WHERE telephone_mere REGEXP '^6[0-9]{8}$';

-- Mettre à jour les numéros qui commencent par 237 sans +
UPDATE etudiant 
SET telephone_pere = CONCAT('+', telephone_pere)
WHERE telephone_pere REGEXP '^237[0-9]{9}$';

UPDATE etudiant 
SET telephone_mere = CONCAT('+', telephone_mere)
WHERE telephone_mere REGEXP '^237[0-9]{9}$';

-- Vérifier les résultats
SELECT 
    nom,
    prenom,
    telephone_pere,
    telephone_mere
FROM etudiant
WHERE telephone_pere IS NOT NULL OR telephone_mere IS NOT NULL;

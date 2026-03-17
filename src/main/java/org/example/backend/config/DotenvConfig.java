package org.example.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration pour charger les variables d'environnement depuis le fichier .env
 */
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            // Charger le fichier .env
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // Ne pas échouer si .env n'existe pas
                    .load();

            // Créer une map avec toutes les variables
            Map<String, Object> envMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                envMap.put(entry.getKey(), entry.getValue());
            });

            // Ajouter les variables au contexte Spring
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envMap));

            System.out.println("✅ Fichier .env chargé avec succès");
        } catch (Exception e) {
            System.out.println("⚠️ Fichier .env non trouvé, utilisation des variables d'environnement système");
        }
    }
}

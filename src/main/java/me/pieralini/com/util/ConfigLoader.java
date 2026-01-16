package me.pieralini.com.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ConfigLoader {

    private static final String CONFIG_FILE = "config.yml";
    private static final String DATABASE_KEY = "database";
    private static final String EMAIL_KEY = "email";
    private static final String TIMEZONE_KEY = "timezone";

    public static Map<String, String> loadConfig() {
        Map<String, String> config = new HashMap<>();
        File file = new File(CONFIG_FILE);

        if (!file.exists()) {
            return config;
        }

        try (FileInputStream input = new FileInputStream(file)) {
            Map<String, Object> yamlData = loadYaml(input);
            if (yamlData == null) return config;

            loadDatabaseConfig(yamlData, config);
            loadEmailConfig(yamlData, config);
            loadTimezone(yamlData, config);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }

    private static Map<String, Object> loadYaml(FileInputStream input) {
        return new Yaml().load(input);
    }

    private static void loadDatabaseConfig(Map<String, Object> yamlData, Map<String, String> config) {
        Object database = yamlData.get(DATABASE_KEY);

        if (database instanceof Map<?, ?> dbMap) {
            dbMap.forEach((key, value) ->
                    config.put(String.valueOf(key), String.valueOf(value))
            );
        }
    }

    private static void loadEmailConfig(Map<String, Object> yamlData, Map<String, String> config) {
        Object email = yamlData.get(EMAIL_KEY);

        if (email instanceof Map<?, ?> emailMap) {
            emailMap.forEach((key, value) ->
                    config.put("email." + key, String.valueOf(value))
            );
        }
    }

    private static void loadTimezone(Map<String, Object> yamlData, Map<String, String> config) {
        Object timezone = yamlData.get(TIMEZONE_KEY);

        if (timezone != null) {
            config.put(TIMEZONE_KEY, timezone.toString());
        }
    }
}
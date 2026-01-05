package me.pieralini.com.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.TimeZone;

public class LoadConfig {

    private static Map<String, Object> config;
    private static LoadConfig instance;

    private LoadConfig() {
        loadConfiguration();
    }

    public static LoadConfig getInstance() {
        if (instance == null) {
            instance = new LoadConfig();
        }
        return instance;
    }

    private void loadConfiguration() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("config.yml");

            if (inputStream == null) {
                throw new RuntimeException("config.yml file not found in resources!");
            }

            config = yaml.load(inputStream);
            inputStream.close();

            setTimezone();

            System.out.println("[LoadConfig] Configuration loaded successfully!");

        } catch (Exception e) {
            System.err.println("[LoadConfig] Error loading configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setTimezone() {
        try {
            Map<String, Object> timezoneConfig = getSection("timezone");
            if (timezoneConfig != null) {
                boolean useSystemTimezone = (boolean) timezoneConfig.getOrDefault("use-system-timezone", false);

                if (!useSystemTimezone) {
                    String timezone = (String) timezoneConfig.get("default");
                    if (timezone != null && !timezone.isEmpty()) {
                        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
                        System.out.println("[LoadConfig] Timezone set to: " + timezone);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[LoadConfig] Error setting timezone: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getSection(String path) {
        if (config == null) {
            getInstance();
        }

        String[] keys = path.split("\\.");
        Map<String, Object> current = config;

        for (int i = 0; i < keys.length - 1; i++) {
            Object next = current.get(keys[i]);
            if (next instanceof Map) {
                current = (Map<String, Object>) next;
            } else {
                return null;
            }
        }

        Object result = current.get(keys[keys.length - 1]);
        return result instanceof Map ? (Map<String, Object>) result : null;
    }

    public static String getString(String path) {
        Object value = getValue(path);
        return value != null ? value.toString() : null;
    }

    public static Integer getInt(String path) {
        Object value = getValue(path);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static Boolean getBoolean(String path) {
        Object value = getValue(path);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Object getValue(String path) {
        if (config == null) {
            getInstance();
        }

        String[] keys = path.split("\\.");
        Map<String, Object> current = config;

        for (int i = 0; i < keys.length - 1; i++) {
            Object next = current.get(keys[i]);
            if (next instanceof Map) {
                current = (Map<String, Object>) next;
            } else {
                return null;
            }
        }

        return current.get(keys[keys.length - 1]);
    }

    public static void reload() {
        getInstance().loadConfiguration();
    }
}

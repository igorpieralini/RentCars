package me.pieralini.com.util.email;

import me.pieralini.com.util.ConfigLoader;

import java.util.Map;
import java.util.Properties;

public final class EmailConfig {

    private static final Map<String, String> CONFIG = ConfigLoader.loadConfig();

    private EmailConfig() {}

    public static Properties smtpProperties() {
        validar();

        Properties props = new Properties();
        props.put("mail.smtp.auth", get("email.smtpAuth", "true"));
        props.put("mail.smtp.starttls.enable", get("email.startTls", "true"));
        props.put("mail.smtp.host", get("email.host", null));
        props.put("mail.smtp.port", get("email.port", "587"));
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        return props;
    }

    public static String username() {
        return get("email.username", null);
    }

    public static String password() {
        return get("email.password", null);
    }

    private static String get(String key, String defaultValue) {
        return CONFIG.getOrDefault(key, defaultValue);
    }

    private static void validar() {
        String user = username();
        String pass = password();
        String host = get("email.host", null);

        if (user == null || user.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Configuração 'email.username' não encontrada ou vazia no config.yml"
            );
        }

        if (pass == null || pass.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Configuração 'email.password' não encontrada ou vazia no config.yml"
            );
        }

        if (host == null || host.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Configuração 'email.host' não encontrada ou vazia no config.yml"
            );
        }
    }
}
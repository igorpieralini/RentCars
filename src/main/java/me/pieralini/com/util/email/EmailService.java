package me.pieralini.com.util.email;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailService {

    public static void sendEmail(
            String destinatario,
            String assunto,
            String conteudoHtml
    ) {

        Properties props = EmailConfig.smtpProperties();

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        EmailConfig.username(),
                        EmailConfig.password()
                );
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.username()));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destinatario)
            );
            message.setSubject(assunto);

            String corpoFinal = EmailTemplate.montarEmail(conteudoHtml);
            message.setContent(corpoFinal, "text/html; charset=UTF-8");

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar email", e);
        }
    }
}

package me.pieralini.com.util.email;

public class EmailTemplate {

    private static final String HEADER = """
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
            <meta charset="UTF-8">
            <title>AlugaCar</title>
        </head>
        <body style="margin:0; padding:0; background-color:#f4f6f8;">
            <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f8; padding:30px 0;">
                <tr>
                    <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background-color:#ffffff; border-radius:8px;
                                      font-family: Arial, Helvetica, sans-serif;
                                      box-shadow:0 2px 8px rgba(0,0,0,0.05);">
                            <tr>
                                <td style="padding:24px; text-align:center; background-color:#0d6efd; border-radius:8px 8px 0 0;">
                                    <h1 style="margin:0; color:#ffffff; font-size:26px;">
                                        🚗 AlugaCar
                                    </h1>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding:30px; color:#333333; font-size:15px; line-height:1.6;">
    """;

    private static final String FOOTER = """
                                </td>
                            </tr>
                            <tr>
                                <td style="padding:20px; background-color:#f8f9fa; text-align:center;
                                           border-top:1px solid #e0e0e0;">
                                    <p style="margin:0; font-size:12px; color:#6c757d;">
                                        Este é um e-mail automático da <strong>AlugaCar</strong>.<br>
                                        Não responda este e-mail.
                                    </p>
                                </td>
                            </tr>
                        </table>
                        <p style="margin-top:15px; font-size:11px; color:#999999;">
                            © 2026 AlugaCar • Todos os direitos reservados
                        </p>
                    </td>
                </tr>
            </table>
        </body>
        </html>
    """;

    public static String montarEmail(String conteudo) {
        return HEADER + conteudo + FOOTER;
    }
}

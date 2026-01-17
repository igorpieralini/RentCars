package me.pieralini.com.ui.systems;

import me.pieralini.com.ui.components.*;
import me.pieralini.com.util.Database;
import me.pieralini.com.util.UIHelper;
import me.pieralini.com.util.email.EmailService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PasswordRecoveryFrame extends JFrame {

    private static final Color BG_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    private static final Color TEXT_SECONDARY = new Color(96, 96, 96);
    private static final Color BRAND_COLOR = new Color(10, 102, 194);

    private final BufferedImage appIcon;
    private double scale;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Step 1: Request
    private HintTextField emailField;
    private JLabel requestStatusLabel;
    private RoundedButton sendTokenButton;

    // Step 2: Verify & Reset
    private HintTextField tokenField;
    private HintPasswordField newPasswordField;
    private HintPasswordField confirmPasswordField;
    private JLabel resetStatusLabel;
    private RoundedButton resetButton;

    private String currentEmail;
    private String generatedToken;

    public PasswordRecoveryFrame(BufferedImage icon) {
        super("AlugaCar — Recuperar Senha");

        this.appIcon = icon;
        setupFrame();
        buildUI();
        setupResponsiveness();

        setVisible(true);
    }

    private void setupFrame() {
        if (appIcon != null) {
            setIconImage(appIcon);
            if (Taskbar.isTaskbarSupported()) {
                try {
                    Taskbar.getTaskbar().setIconImage(appIcon);
                } catch (Exception ignored) {}
            }
        }

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.scale = Math.max(0.75, Math.min(1.25, screen.width / 1366.0));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(s(500), s(580));
        setResizable(false); // Impede redimensionamento
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_WHITE);
        setLayout(new BorderLayout());
    }

    private void buildUI() {
        add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BG_WHITE);

        cardPanel.add(createRequestPanel(), "REQUEST");
        cardPanel.add(createResetPanel(), "RESET");

        add(cardPanel, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private void setupResponsiveness() {
        // Removido - janela agora tem tamanho fixo
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(s(10), s(20), s(10), s(20))
        ));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, s(8), 0));
        logoPanel.setOpaque(false);

        LogoLabel logo = new LogoLabel(s(24), BRAND_COLOR, scale);
        logoPanel.add(logo);

        JLabel brandName = new JLabel("AlugaCar");
        brandName.setFont(new Font("SansSerif", Font.BOLD, s(18)));
        brandName.setForeground(BRAND_COLOR);
        logoPanel.add(brandName);

        header.add(logoPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createRequestPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(s(20), s(35), s(20), s(35)));

        RoundedPanel formContainer = new RoundedPanel(s(12), BG_WHITE);
        formContainer.setLayout(new GridBagLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(s(16), s(20), s(16), s(20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;

        // Icon
        JLabel iconLabel = new JLabel("🔒");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, s(40)));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, s(12), 0);
        formContainer.add(iconLabel, gbc);

        // Title
        JLabel title = new JLabel("Recuperar Senha");
        title.setFont(new Font("SansSerif", Font.BOLD, s(22)));
        title.setForeground(TEXT_PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, s(6), 0);
        formContainer.add(title, gbc);

        // Subtitle
        JLabel subtitle = new JLabel("<html><center>Informe seu e-mail cadastrado.<br>Enviaremos um código de verificação.</center></html>");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, s(11)));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, s(18), 0);
        formContainer.add(subtitle, gbc);

        // Email Label
        JLabel emailLabel = new JLabel("E-mail");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, s(11)));
        emailLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, s(4), 0);
        formContainer.add(emailLabel, gbc);

        // Email Field
        emailField = new HintTextField("Digite seu e-mail cadastrado");
        emailField.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        emailField.setPreferredSize(new Dimension(0, s(36)));
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(TEXT_PRIMARY);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(s(8), s(10), s(8), s(10))
        ));

        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BRAND_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(s(7), s(9), s(7), s(9))
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(s(8), s(10), s(8), s(10))
                ));
            }
        });

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, s(10), 0);
        formContainer.add(emailField, gbc);

        // Status Label
        requestStatusLabel = new JLabel(" ", SwingConstants.LEFT);
        requestStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, s(10)));
        requestStatusLabel.setForeground(new Color(220, 53, 69));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, s(10), 0);
        formContainer.add(requestStatusLabel, gbc);

        // Send Button
        sendTokenButton = new RoundedButton("Enviar Código");
        sendTokenButton.setPreferredSize(new Dimension(0, s(38)));
        sendTokenButton.setFont(new Font("SansSerif", Font.BOLD, s(13)));
        sendTokenButton.setBackground(BRAND_COLOR);
        sendTokenButton.setForeground(Color.WHITE);
        sendTokenButton.addActionListener(e -> requestPasswordReset());
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        formContainer.add(sendTokenButton, gbc);

        emailField.addActionListener(e -> requestPasswordReset());

        panel.add(formContainer);
        return panel;
    }

    private JPanel createResetPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(s(15), s(35), s(15), s(35)));

        RoundedPanel formContainer = new RoundedPanel(s(12), BG_WHITE);
        formContainer.setLayout(new GridBagLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(s(14), s(20), s(14), s(20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;

        // Icon
        JLabel iconLabel = new JLabel("✉️");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, s(40)));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, s(10), 0);
        formContainer.add(iconLabel, gbc);

        // Title
        JLabel title = new JLabel("Verificar Código");
        title.setFont(new Font("SansSerif", Font.BOLD, s(22)));
        title.setForeground(TEXT_PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, s(6), 0);
        formContainer.add(title, gbc);

        // Subtitle
        JLabel subtitle = new JLabel("<html><center>Digite o código enviado para seu e-mail<br>e escolha uma nova senha.</center></html>");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, s(11)));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, s(16), 0);
        formContainer.add(subtitle, gbc);

        // Token Field
        JLabel tokenLabel = new JLabel("Código de Verificação");
        tokenLabel.setFont(new Font("SansSerif", Font.PLAIN, s(11)));
        tokenLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, s(4), 0);
        formContainer.add(tokenLabel, gbc);

        tokenField = new HintTextField("Digite o código de 6 dígitos");
        tokenField.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        tokenField.setPreferredSize(new Dimension(0, s(36)));
        tokenField.setBackground(Color.WHITE);
        tokenField.setForeground(TEXT_PRIMARY);
        tokenField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(s(8), s(10), s(8), s(10))
        ));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, s(10), 0);
        formContainer.add(tokenField, gbc);

        // New Password Field
        JLabel newPassLabel = new JLabel("Nova Senha");
        newPassLabel.setFont(new Font("SansSerif", Font.PLAIN, s(11)));
        newPassLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, s(4), 0);
        formContainer.add(newPassLabel, gbc);

        newPasswordField = new HintPasswordField("Digite sua nova senha");
        newPasswordField.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        newPasswordField.setPreferredSize(new Dimension(0, s(36)));
        newPasswordField.setBackground(Color.WHITE);
        newPasswordField.setForeground(TEXT_PRIMARY);
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(s(8), s(10), s(8), s(10))
        ));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, s(10), 0);
        formContainer.add(newPasswordField, gbc);

        // Confirm Password Field
        JLabel confirmPassLabel = new JLabel("Confirmar Senha");
        confirmPassLabel.setFont(new Font("SansSerif", Font.PLAIN, s(11)));
        confirmPassLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, s(4), 0);
        formContainer.add(confirmPassLabel, gbc);

        confirmPasswordField = new HintPasswordField("Confirme sua nova senha");
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        confirmPasswordField.setPreferredSize(new Dimension(0, s(36)));
        confirmPasswordField.setBackground(Color.WHITE);
        confirmPasswordField.setForeground(TEXT_PRIMARY);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(s(8), s(10), s(8), s(10))
        ));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, s(10), 0);
        formContainer.add(confirmPasswordField, gbc);

        // Status Label
        resetStatusLabel = new JLabel(" ", SwingConstants.LEFT);
        resetStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, s(10)));
        resetStatusLabel.setForeground(new Color(220, 53, 69));
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, s(8), 0);
        formContainer.add(resetStatusLabel, gbc);

        // Reset Button
        resetButton = new RoundedButton("Redefinir Senha");
        resetButton.setPreferredSize(new Dimension(0, s(38)));
        resetButton.setFont(new Font("SansSerif", Font.BOLD, s(13)));
        resetButton.setBackground(BRAND_COLOR);
        resetButton.setForeground(Color.WHITE);
        resetButton.addActionListener(e -> performPasswordReset());
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        formContainer.add(resetButton, gbc);

        confirmPasswordField.addActionListener(e -> performPasswordReset());

        panel.add(formContainer);
        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(s(8), 0, s(12), 0));

        JLabel backLabel = new JLabel("Voltar para login");
        backLabel.setFont(new Font("SansSerif", Font.PLAIN, s(11)));
        backLabel.setForeground(BRAND_COLOR);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backLabel.setText("<html><u>Voltar para login</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                backLabel.setText("Voltar para login");
            }
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        footer.add(backLabel);
        return footer;
    }

    private void requestPasswordReset() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            UIHelper.statusMessage(requestStatusLabel, "Digite seu e-mail", UIHelper.MessageType.ERROR);
            return;
        }

        if (!isValidEmail(email)) {
            UIHelper.statusMessage(requestStatusLabel, "E-mail inválido", UIHelper.MessageType.ERROR);
            return;
        }

        sendTokenButton.setEnabled(false);
        requestStatusLabel.setText("Verificando...");
        requestStatusLabel.setForeground(TEXT_SECONDARY);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return checkEmailExists(email);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        generateAndSendToken(email);
                    } else {
                        UIHelper.statusMessage(requestStatusLabel, "E-mail não encontrado", UIHelper.MessageType.ERROR);
                        sendTokenButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    UIHelper.statusMessage(requestStatusLabel, "Erro ao verificar e-mail", UIHelper.MessageType.ERROR);
                    sendTokenButton.setEnabled(true);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private void generateAndSendToken(String email) {
        generatedToken = generateToken();
        currentEmail = email;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    saveTokenToDatabase(email, generatedToken);
                    sendTokenEmail(email, generatedToken);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIHelper.statusMessage(requestStatusLabel, "Código enviado com sucesso!", UIHelper.MessageType.INFO);
                        Timer timer = new Timer(1500, e -> {
                            cardLayout.show(cardPanel, "RESET");
                            sendTokenButton.setEnabled(true);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        UIHelper.statusMessage(requestStatusLabel, "Erro ao enviar código", UIHelper.MessageType.ERROR);
                        sendTokenButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    UIHelper.statusMessage(requestStatusLabel, "Erro ao enviar código", UIHelper.MessageType.ERROR);
                    sendTokenButton.setEnabled(true);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private void performPasswordReset() {
        String token = tokenField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (token.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            UIHelper.statusMessage(resetStatusLabel, "Preencha todos os campos", UIHelper.MessageType.ERROR);
            return;
        }

        if (newPassword.length() < 6) {
            UIHelper.statusMessage(resetStatusLabel, "A senha deve ter pelo menos 6 caracteres", UIHelper.MessageType.ERROR);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            UIHelper.statusMessage(resetStatusLabel, "As senhas não coincidem", UIHelper.MessageType.ERROR);
            return;
        }

        resetButton.setEnabled(false);
        resetStatusLabel.setText("Atualizando senha...");
        resetStatusLabel.setForeground(TEXT_SECONDARY);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return verifyTokenAndUpdatePassword(currentEmail, token, newPassword);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIHelper.statusMessage(resetStatusLabel, "Senha atualizada com sucesso!", UIHelper.MessageType.INFO);
                        Timer timer = new Timer(2000, e -> {
                            JOptionPane.showMessageDialog(
                                    PasswordRecoveryFrame.this,
                                    "Senha alterada com sucesso!\nFaça login com sua nova senha.",
                                    "Sucesso",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            dispose();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        UIHelper.statusMessage(resetStatusLabel, "Código inválido ou expirado", UIHelper.MessageType.ERROR);
                        resetButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    UIHelper.statusMessage(resetStatusLabel, "Erro ao atualizar senha", UIHelper.MessageType.ERROR);
                    resetButton.setEnabled(true);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        int token = 100000 + random.nextInt(900000);
        return String.valueOf(token);
    }

    private boolean checkEmailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (ResultSet rs = Database.query(sql, email)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveTokenToDatabase(String email, String token) throws SQLException {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        String expiresAtStr = expiresAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String sql = "INSERT INTO password_reset_tokens (email, token, expires_at, used) VALUES (?, ?, ?, false) " +
                "ON DUPLICATE KEY UPDATE token = ?, expires_at = ?, used = false";

        Database.execute(sql, email, token, expiresAtStr, token, expiresAtStr);
    }

    private void sendTokenEmail(String email, String token) {
        String conteudo = String.format("""
            <h2 style="color:#0d6efd; margin-bottom:16px;">Recuperação de Senha</h2>
            <p>Você solicitou a recuperação de senha da sua conta AlugaCar.</p>
            <p>Use o código abaixo para redefinir sua senha:</p>
            <div style="background-color:#f8f9fa; padding:20px; border-radius:8px; text-align:center; margin:20px 0;">
                <h1 style="color:#0d6efd; font-size:36px; letter-spacing:8px; margin:0;">%s</h1>
            </div>
            <p style="color:#dc3545; font-weight:bold;">⏰ Este código expira em 15 minutos.</p>
            <p>Se você não solicitou esta recuperação, ignore este e-mail.</p>
            <hr style="border:none; border-top:1px solid #e0e0e0; margin:20px 0;">
            <p style="font-size:13px; color:#6c757d;">
                Por segurança, nunca compartilhe este código com ninguém.
            </p>
            """, token);

        EmailService.sendEmail(email, "Recuperação de Senha - AlugaCar", conteudo);
    }

    private boolean verifyTokenAndUpdatePassword(String email, String token, String newPassword) {
        String sql = "SELECT expires_at, used FROM password_reset_tokens WHERE email = ? AND token = ?";

        try (ResultSet rs = Database.query(sql, email, token)) {
            if (rs.next()) {
                String expiresAtStr = rs.getString("expires_at");
                boolean used = rs.getBoolean("used");

                if (used) {
                    return false;
                }

                LocalDateTime expiresAt = LocalDateTime.parse(expiresAtStr,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                if (LocalDateTime.now().isAfter(expiresAt)) {
                    return false;
                }

                String updateSql = "UPDATE users SET password = ? WHERE email = ?";
                Database.execute(updateSql, newPassword, email);

                String markUsedSql = "UPDATE password_reset_tokens SET used = true WHERE email = ? AND token = ?";
                Database.execute(markUsedSql, email, token);

                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private int s(int v) {
        return (int) Math.round(v * scale);
    }
}
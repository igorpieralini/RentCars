package me.pieralini.com.ui.view;

import me.pieralini.com.ui.components.*;
import me.pieralini.com.util.Database;
import me.pieralini.com.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

public class Cadaster extends JFrame {

    private static final Color BG_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    private static final Color TEXT_SECONDARY = new Color(96, 96, 96);
    private static final Color BRAND_COLOR = new Color(10, 102, 194);
    private static final Color ACCENT = new Color(0, 115, 177);
    private static final Color ACCENT_LIGHT = new Color(230, 245, 255);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);

    private final Map<String, String> config;
    private double scale;
    private final BufferedImage appIcon;

    private HintTextField usernameField;
    private HintTextField fullNameField;
    private HintTextField emailField;
    private HintPasswordField passField;
    private HintPasswordField confirmPassField;
    private JLabel statusLabel;
    private RoundedButton registerButton;
    private JPanel mainContent;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Cadaster(Map<String, String> config, BufferedImage icon) {
        super("AlugaCar — Cadastro");

        this.config = config;
        this.appIcon = icon;

        initializeDatabase();
        setupFrame();
        buildUI();
        setupResponsiveness();

        setVisible(true);
    }

    private void initializeDatabase() {
        Database.setup(config);
        if (!Database.isConnected()) {
            if (!Database.connect()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível conectar ao banco de dados.\nVerifique as configurações em config.yml",
                        "Erro de Conexão",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
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

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(s(1200), s(700));
        setMinimumSize(new Dimension(s(900), s(650)));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_WHITE);
        setLayout(new BorderLayout());
    }

    private void buildUI() {
        add(createHeader(), BorderLayout.NORTH);
        mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }

    private void setupResponsiveness() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mainContent.revalidate();
                mainContent.repaint();
            }
        });
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(s(12), s(24), s(12), s(24))
        ));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, s(10), 0));
        logoPanel.setOpaque(false);

        LogoLabel logo = new LogoLabel(s(28), BRAND_COLOR, scale);
        logoPanel.add(logo);

        JLabel brandName = new JLabel("AlugaCar");
        brandName.setFont(new Font("SansSerif", Font.BOLD, s(20)));
        brandName.setForeground(BRAND_COLOR);
        logoPanel.add(brandName);

        header.add(logoPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_WHITE);

        // Painel central com o formulário em card único
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(s(30), s(50), s(30), s(50)));

        JPanel formCard = createFormCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;

        centerPanel.add(formCard, gbc);
        main.add(centerPanel, BorderLayout.CENTER);

        return main;
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(s(35), s(40), s(35), s(40))
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        // Seção de título
        addTitleSection(card, gbc);

        // Container dos campos em 2 colunas
        gbc.gridy++;
        gbc.insets = new Insets(s(25), 0, 0, 0);
        card.add(createFieldsContainer(), gbc);

        // Status e botão
        gbc.gridy++;
        gbc.insets = new Insets(s(20), 0, 0, 0);
        addStatusLabel(card, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(s(8), 0, 0, 0);
        addRegisterButton(card, gbc);

        // Divider e link de login
        gbc.gridy++;
        gbc.insets = new Insets(s(20), 0, 0, 0);
        addDivider(card, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(s(16), 0, 0, 0);
        addLoginLink(card, gbc);

        setupRegisterActions();

        return card;
    }

    private void addTitleSection(JPanel container, GridBagConstraints gbc) {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Crie sua conta");
        title.setFont(new Font("SansSerif", Font.BOLD, s(32)));
        title.setForeground(new Color(191, 90, 45));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Preencha as informações abaixo para começar");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, s(14)));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(s(8)));
        titlePanel.add(subtitle);

        gbc.insets = new Insets(0, 0, 0, 0);
        container.add(titlePanel, gbc);
    }

    private JPanel createFieldsContainer() {
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridy = 0;

        // Primeira linha: Nome de usuário | Nome completo
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, s(12));
        fieldsPanel.add(createFieldPanel("Nome de usuário", usernameField = new HintTextField("Digite seu nome de usuário")), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, s(12), 0, 0);
        fieldsPanel.add(createFieldPanel("Nome completo", fullNameField = new HintTextField("Digite seu nome completo")), gbc);

        // Segunda linha: E-mail (campo largo)
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(s(16), 0, 0, 0);
        fieldsPanel.add(createFieldPanel("E-mail", emailField = new HintTextField("Digite seu e-mail")), gbc);

        // Terceira linha: Senha | Confirmar senha
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(s(16), 0, 0, s(12));
        fieldsPanel.add(createFieldPanel("Senha", passField = new HintPasswordField("Digite sua senha")), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(s(16), s(12), 0, 0);
        fieldsPanel.add(createFieldPanel("Confirmar senha", confirmPassField = new HintPasswordField("Confirme sua senha")), gbc);

        return fieldsPanel;
    }

    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, s(6)));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, s(13)));
        label.setForeground(TEXT_PRIMARY);

        if (field instanceof HintTextField) {
            setupTextField((HintTextField) field);
        } else if (field instanceof HintPasswordField) {
            setupPasswordField((HintPasswordField) field);
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void setupTextField(HintTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, s(14)));
        field.setPreferredSize(new Dimension(0, s(40)));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(s(10), s(12), s(10), s(12))
        ));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BRAND_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(s(9), s(11), s(9), s(11))
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(s(10), s(12), s(10), s(12))
                ));
            }
        });
    }

    private void setupPasswordField(HintPasswordField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, s(14)));
        field.setPreferredSize(new Dimension(0, s(40)));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(s(10), s(12), s(10), s(12))
        ));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BRAND_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(s(9), s(11), s(9), s(11))
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(s(10), s(12), s(10), s(12))
                ));
            }
        });
    }

    private void addStatusLabel(JPanel container, GridBagConstraints gbc) {
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        statusLabel.setForeground(ERROR_COLOR);
        container.add(statusLabel, gbc);
    }

    private void addRegisterButton(JPanel container, GridBagConstraints gbc) {
        registerButton = new RoundedButton("Criar conta");
        registerButton.setPreferredSize(new Dimension(0, s(46)));
        registerButton.setFont(new Font("SansSerif", Font.BOLD, s(15)));
        registerButton.setBackground(BRAND_COLOR);
        registerButton.setForeground(Color.WHITE);
        container.add(registerButton, gbc);
    }

    private void addDivider(JPanel container, GridBagConstraints gbc) {
        JPanel divider = createDividerWithText("ou");
        container.add(divider, gbc);
    }

    private void addLoginLink(JPanel container, GridBagConstraints gbc) {
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, s(5), 0));
        loginPanel.setOpaque(false);

        JLabel hasAccount = new JLabel("Já possui uma conta?");
        hasAccount.setFont(new Font("SansSerif", Font.PLAIN, s(13)));
        hasAccount.setForeground(TEXT_PRIMARY);

        JLabel loginLink = new JLabel("Faça login");
        loginLink.setFont(new Font("SansSerif", Font.BOLD, s(13)));
        loginLink.setForeground(BRAND_COLOR);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginLink.setText("<html><u>Faça login</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                loginLink.setText("Faça login");
            }
            public void mouseClicked(MouseEvent e) {
                openLoginWindow();
            }
        });

        loginPanel.add(hasAccount);
        loginPanel.add(loginLink);

        container.add(loginPanel, gbc);
    }

    private JPanel createDividerWithText(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridy = 0;

        JSeparator left = new JSeparator();
        left.setForeground(new Color(200, 200, 200));
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, s(12));
        panel.add(left, gbc);

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, s(13)));
        label.setForeground(TEXT_SECONDARY);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(label, gbc);

        JSeparator right = new JSeparator();
        right.setForeground(new Color(200, 200, 200));
        gbc.gridx = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, s(12), 0, 0);
        panel.add(right, gbc);

        return panel;
    }

    private void setupRegisterActions() {
        Runnable register = this::performRegister;

        registerButton.addActionListener(e -> register.run());
        confirmPassField.addActionListener(e -> register.run());

        getRootPane().setDefaultButton(registerButton);
    }

    private void performRegister() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String confirmPassword = new String(confirmPassField.getPassword()).trim();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            UIHelper.statusMessage(statusLabel, "Preencha todos os campos", UIHelper.MessageType.ERROR);
            return;
        }

        if (username.length() < 3) {
            UIHelper.statusMessage(statusLabel, "Nome de usuário deve ter no mínimo 3 caracteres", UIHelper.MessageType.ERROR);
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            UIHelper.statusMessage(statusLabel, "E-mail inválido", UIHelper.MessageType.ERROR);
            return;
        }

        if (password.length() < 6) {
            UIHelper.statusMessage(statusLabel, "A senha deve ter no mínimo 6 caracteres", UIHelper.MessageType.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            UIHelper.statusMessage(statusLabel, "As senhas não coincidem", UIHelper.MessageType.ERROR);
            return;
        }

        if (!Database.isConnected() && !Database.connect()) {
            UIHelper.statusMessage(
                    statusLabel,
                    "Erro de conexão com o banco de dados",
                    UIHelper.MessageType.ERROR
            );
            return;
        }

        registerButton.setEnabled(false);
        statusLabel.setText("Criando conta...");
        statusLabel.setForeground(TEXT_SECONDARY);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            private String errorMessage = "";

            @Override
            protected Boolean doInBackground() {
                return createUser(username, fullName, email, password);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIHelper.statusMessage(statusLabel, "Conta criada com sucesso!", UIHelper.MessageType.INFO);
                        Timer timer = new Timer(1500, e -> openLoginWindow());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        UIHelper.statusMessage(statusLabel, errorMessage.isEmpty() ? "Erro ao criar conta" : errorMessage, UIHelper.MessageType.ERROR);
                        registerButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    UIHelper.statusMessage(statusLabel, "Erro durante o cadastro", UIHelper.MessageType.ERROR);
                    registerButton.setEnabled(true);
                    e.printStackTrace();
                }
            }

            private boolean createUser(String username, String fullName, String email, String password) {

                String checkEmailSql = "SELECT id FROM users WHERE email = ? AND active = 1";
                try (ResultSet rs = Database.query(checkEmailSql, email)) {
                    if (rs.next()) {
                        errorMessage = "E-mail já cadastrado";
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    errorMessage = "Erro ao verificar e-mail";
                    return false;
                }

                String checkUsernameSql = "SELECT id FROM users WHERE username = ? AND active = 1";
                try (ResultSet rs = Database.query(checkUsernameSql, username)) {
                    if (rs.next()) {
                        errorMessage = "Nome de usuário já cadastrado";
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    errorMessage = "Erro ao verificar nome de usuário";
                    return false;
                }

                String now = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                String insertSql =
                        "INSERT INTO users " +
                                "(username, password, email, full_name, role, active, created_at, updated_at, last_login) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try {
                    int rows = Database.execute(
                            insertSql,
                            username,
                            password,
                            email,
                            fullName,
                            "USER",
                            1,
                            now,
                            now,
                            null
                    );
                    return rows > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    errorMessage = "Erro ao criar conta";
                    return false;
                }
            }


        };

        worker.execute();
    }

    private void openLoginWindow() {
        java.awt.EventQueue.invokeLater(() -> {
            new me.pieralini.com.ui.LoginFrame(config, appIcon).setVisible(true);
            dispose();
        });
    }

    private int s(int v) {
        return (int) Math.round(v * scale);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
package me.pieralini.com.ui;

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
import java.util.Map;

public class LoginFrame extends JFrame {

    private static final Color BG_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    private static final Color TEXT_SECONDARY = new Color(96, 96, 96);
    private static final Color BRAND_COLOR = new Color(10, 102, 194);
    private static final Color ACCENT = new Color(0, 115, 177);
    private static final Color ILLUSTRATION_BG = new Color(245, 248, 250);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);

    private final Map<String, String> config;
    private double scale;
    private final BufferedImage appIcon;

    private HintTextField emailField;
    private HintPasswordField passField;
    private JLabel statusLabel;
    private RoundedButton loginButton;
    private JPanel mainContent;
    private JPanel leftPanel;
    private JPanel rightPanel;

    public LoginFrame(Map<String, String> config, BufferedImage icon) {
        super("AlugaCar — Login");

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
        if (!Database.connect()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível conectar ao banco de dados.\nVerifique as configurações em config.yml",
                    "Erro de Conexão",
                    JOptionPane.ERROR_MESSAGE
            );
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
        setSize(s(1000), s(600));
        setMinimumSize(new Dimension(s(700), s(500)));
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
                updateLayout();
            }
        });
    }

    private void updateLayout() {
        int width = getWidth();

        if (width < s(900)) {
            if (mainContent.getLayout() instanceof GridLayout) {
                mainContent.removeAll();
                mainContent.setLayout(new BorderLayout());

                JPanel leftContainer = new JPanel(new GridBagLayout());
                leftContainer.setBackground(BG_WHITE);
                leftContainer.add(leftPanel);

                mainContent.add(leftContainer, BorderLayout.CENTER);
                rightPanel.setVisible(false);
            }
        } else {
            if (mainContent.getLayout() instanceof BorderLayout) {
                mainContent.removeAll();
                mainContent.setLayout(new GridLayout(1, 2));
                mainContent.add(leftPanel);
                mainContent.add(rightPanel);
                rightPanel.setVisible(true);
            }
        }

        mainContent.revalidate();
        mainContent.repaint();
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
        JPanel main = new JPanel(new GridLayout(1, 2));
        main.setBackground(BG_WHITE);

        leftPanel = createLeftPanel();
        rightPanel = createRightPanel();

        main.add(leftPanel);
        main.add(rightPanel);

        return main;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(BG_WHITE);

        int hPadding = Math.max(s(20), Math.min(s(80), getWidth() / 20));
        int vPadding = s(15);
        left.setBorder(BorderFactory.createEmptyBorder(vPadding, hPadding, vPadding, hPadding));

        RoundedPanel formContainer = new RoundedPanel(s(16), BG_WHITE);
        formContainer.setLayout(new GridBagLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(s(14), s(16), s(14), s(16)));

        int maxWidth = Math.min(s(400), (int)(getWidth() * 0.4));
        formContainer.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;

        addTitle(formContainer, gbc);
        addEmailField(formContainer, gbc);
        addPasswordField(formContainer, gbc);
        addForgotPassword(formContainer, gbc);
        addStatusLabel(formContainer, gbc);
        addLoginButton(formContainer, gbc);
        addDivider(formContainer, gbc);
        addAlternativeButton(formContainer, gbc);
        addSignupLink(formContainer, gbc);

        setupLoginActions();

        left.add(formContainer);
        return left;
    }

    private void addTitle(JPanel container, GridBagConstraints gbc) {
        JLabel title = new JLabel("Acelere sua jornada");
        title.setFont(new Font("SansSerif", Font.BOLD, s(26)));
        title.setForeground(new Color(191, 90, 45));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, s(1), 0);
        container.add(title, gbc);

        JLabel subtitle = new JLabel("Faça login para continuar");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        subtitle.setForeground(TEXT_SECONDARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, s(16), 0);
        container.add(subtitle, gbc);
    }

    private void addEmailField(JPanel container, GridBagConstraints gbc) {
        JLabel emailLabel = new JLabel("E-mail ou telefone");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        emailLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, s(4), 0);
        container.add(emailLabel, gbc);

        emailField = new HintTextField("Digite seu e-mail ou telefone");
        emailField.setFont(new Font("SansSerif", Font.PLAIN, s(13)));
        emailField.setPreferredSize(new Dimension(0, s(38)));
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

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, s(10), 0);
        container.add(emailField, gbc);
    }

    private void addPasswordField(JPanel container, GridBagConstraints gbc) {
        JLabel passLabel = new JLabel("Senha");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        passLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, s(4), 0);
        container.add(passLabel, gbc);

        passField = new HintPasswordField("Digite sua senha");
        passField.setFont(new Font("SansSerif", Font.PLAIN, s(13)));
        passField.setPreferredSize(new Dimension(0, s(38)));
        passField.setBackground(Color.WHITE);
        passField.setForeground(TEXT_PRIMARY);
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(s(8), s(10), s(8), s(10))
        ));

        passField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                passField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BRAND_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(s(7), s(9), s(7), s(9))
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                passField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(s(8), s(10), s(8), s(10))
                ));
            }
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, s(4), 0);
        container.add(passField, gbc);
    }

    private void addForgotPassword(JPanel container, GridBagConstraints gbc) {
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPanel.setOpaque(false);

        JLabel forgotLink = new JLabel("Esqueceu a senha?");
        forgotLink.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        forgotLink.setForeground(BRAND_COLOR);
        forgotLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLink.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                forgotLink.setText("<html><u>Esqueceu a senha?</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                forgotLink.setText("Esqueceu a senha?");
            }
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Funcionalidade de recuperação de senha em desenvolvimento",
                        "Recuperar Senha",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        forgotPanel.add(forgotLink);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, s(10), 0);
        container.add(forgotPanel, gbc);
    }

    private void addStatusLabel(JPanel container, GridBagConstraints gbc) {
        statusLabel = new JLabel(" ", SwingConstants.LEFT);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, s(11)));
        statusLabel.setForeground(ERROR_COLOR);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, s(5), 0);
        container.add(statusLabel, gbc);
    }

    private void addLoginButton(JPanel container, GridBagConstraints gbc) {
        loginButton = new RoundedButton("Entrar");
        loginButton.setPreferredSize(new Dimension(0, s(40)));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, s(14)));
        loginButton.setBackground(BRAND_COLOR);
        loginButton.setForeground(Color.WHITE);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, s(10), 0);
        container.add(loginButton, gbc);
    }

    private void addDivider(JPanel container, GridBagConstraints gbc) {
        JPanel divider = createDividerWithText("ou");
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, s(10), 0);
        container.add(divider, gbc);
    }

    private void addAlternativeButton(JPanel container, GridBagConstraints gbc) {
        RoundedButton altBtn = new RoundedButton("Entrar com navegador");
        altBtn.setPreferredSize(new Dimension(0, s(40)));
        altBtn.setFont(new Font("SansSerif", Font.PLAIN, s(13)));
        altBtn.setBackground(Color.WHITE);
        altBtn.setForeground(TEXT_SECONDARY);
        altBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(s(8), s(12), s(8), s(12))
        ));
        altBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Funcionalidade de login via navegador em desenvolvimento",
                    "Login Alternativo",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, s(12), 0);
        container.add(altBtn, gbc);
    }

    private void addSignupLink(JPanel container, GridBagConstraints gbc) {
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, s(4), 0));
        signupPanel.setOpaque(false);

        JLabel newHere = new JLabel("Ainda não faz parte do AlugaCar?");
        newHere.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        newHere.setForeground(TEXT_PRIMARY);

        JLabel signupLink = new JLabel("Cadastre-se agora");
        signupLink.setFont(new Font("SansSerif", Font.BOLD, s(12)));
        signupLink.setForeground(BRAND_COLOR);
        signupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLink.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                signupLink.setText("<html><u>Cadastre-se agora</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                signupLink.setText("Cadastre-se agora");
            }
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Funcionalidade de cadastro em desenvolvimento",
                        "Cadastrar",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        signupPanel.add(newHere);
        signupPanel.add(signupLink);

        gbc.gridy = 11;
        gbc.insets = new Insets(0, 0, 0, 0);
        container.add(signupPanel, gbc);
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, ILLUSTRATION_BG,
                        getWidth(), getHeight(), new Color(230, 240, 245)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 40));
                int circleSize = s(350);
                g2.fillOval(getWidth() - circleSize/2, getHeight()/2 - circleSize/2, circleSize, circleSize);

                drawCarIllustration(g2);
                g2.dispose();
            }
        };
        right.setBackground(ILLUSTRATION_BG);
        right.setBorder(BorderFactory.createEmptyBorder(s(30), s(30), s(30), s(30)));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel illustText = new JLabel("Seu próximo carro");
        illustText.setFont(new Font("SansSerif", Font.BOLD, s(28)));
        illustText.setForeground(new Color(40, 40, 40));
        illustText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel illustSubtext = new JLabel("está a um clique de distância");
        illustSubtext.setFont(new Font("SansSerif", Font.PLAIN, s(18)));
        illustSubtext.setForeground(TEXT_SECONDARY);
        illustSubtext.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(illustText);
        textPanel.add(Box.createVerticalStrut(s(8)));
        textPanel.add(illustSubtext);

        right.add(textPanel);
        return right;
    }

    private void drawCarIllustration(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2 + s(40);

        g2.setColor(new Color(10, 102, 194, 180));

        int carW = s(200);
        int carH = s(70);
        int carX = cx - carW/2;
        int carY = cy - carH/2;

        g2.fillRoundRect(carX, carY, carW, carH, s(14), s(14));

        int cabinW = s(110);
        int cabinH = s(45);
        int cabinX = carX + s(35);
        int cabinY = carY - s(32);
        g2.fillRoundRect(cabinX, cabinY, cabinW, cabinH, s(11), s(11));

        g2.setColor(new Color(200, 220, 240, 200));
        g2.fillRoundRect(cabinX + s(7), cabinY + s(7), s(45), s(31), s(7), s(7));
        g2.fillRoundRect(cabinX + s(57), cabinY + s(7), s(45), s(31), s(7), s(7));

        g2.setColor(new Color(40, 40, 40, 200));
        int wheelD = s(30);
        g2.fillOval(carX + s(18), carY + carH - s(7), wheelD, wheelD);
        g2.fillOval(carX + carW - s(48), carY + carH - s(7), wheelD, wheelD);

        g2.setColor(new Color(180, 180, 180, 200));
        int rimD = s(16);
        g2.fillOval(carX + s(18) + (wheelD-rimD)/2, carY + carH - s(7) + (wheelD-rimD)/2, rimD, rimD);
        g2.fillOval(carX + carW - s(48) + (wheelD-rimD)/2, carY + carH - s(7) + (wheelD-rimD)/2, rimD, rimD);

        g2.setColor(new Color(180, 180, 180, 100));
        g2.setStroke(new BasicStroke(s(3), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{s(18), s(13)}, 0));
        g2.drawLine(s(35), cy + s(70), w - s(35), cy + s(70));
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
        gbc.insets = new Insets(0, 0, 0, s(10));
        panel.add(left, gbc);

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        label.setForeground(TEXT_SECONDARY);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(label, gbc);

        JSeparator right = new JSeparator();
        right.setForeground(new Color(200, 200, 200));
        gbc.gridx = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, s(10), 0, 0);
        panel.add(right, gbc);

        return panel;
    }

    private void setupLoginActions() {
        Runnable login = this::performLogin;

        loginButton.addActionListener(e -> login.run());
        emailField.addActionListener(e -> login.run());
        passField.addActionListener(e -> login.run());

        getRootPane().setDefaultButton(loginButton);
    }

    private void performLogin() {
        String username = emailField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            UIHelper.statusMessage(statusLabel, "Preencha todos os campos", UIHelper.MessageType.ERROR);
            return;
        }

        if (!Database.isConnected()) {
            UIHelper.statusMessage(statusLabel, "Erro de conexão com o banco de dados", UIHelper.MessageType.ERROR);
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setText("Autenticando...");
        statusLabel.setForeground(TEXT_SECONDARY);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return authenticateUser(username, password);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        UIHelper.statusMessage(statusLabel, "Login realizado com sucesso!", UIHelper.MessageType.INFO);
                        Timer timer = new Timer(1500, e -> openMainWindow());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        UIHelper.statusMessage(statusLabel, "Credenciais inválidas", UIHelper.MessageType.ERROR);
                        loginButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    UIHelper.statusMessage(statusLabel, "Erro durante autenticação", UIHelper.MessageType.ERROR);
                    loginButton.setEnabled(true);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private boolean authenticateUser(String email, String password) {
        String sql = "SELECT id FROM users WHERE email = ? AND password = ?";

        try (ResultSet rs = Database.query(sql, email, password)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openMainWindow() {
        java.awt.EventQueue.invokeLater(() -> {
            new me.pieralini.com.ui.view.MainPageFrame(appIcon).setVisible(true);
            dispose();
        });
    }

    private int s(int v) {
        return (int) Math.round(v * scale);
    }

    @Override
    public void dispose() {
        Database.disconnect();
        super.dispose();
    }
}
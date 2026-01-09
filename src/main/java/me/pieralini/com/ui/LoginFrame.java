package me.pieralini.com.ui;

import me.pieralini.com.ui.components.*;
import me.pieralini.com.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

public class LoginFrame extends JFrame {

    private static final Color BG_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    private static final Color TEXT_SECONDARY = new Color(96, 96, 96);
    private static final Color BRAND_COLOR = new Color(10, 102, 194); // LinkedIn-like blue
    private static final Color ACCENT = new Color(0, 115, 177);
    private static final Color ILLUSTRATION_BG = new Color(245, 248, 250);

    private final Map<String, String> config;
    private final double scale;
    private final BufferedImage appIcon;

    public LoginFrame(Map<String, String> config, BufferedImage icon) {
        super("AlugaCar — Login");

        this.config = config;
        this.appIcon = icon;

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
        setMinimumSize(new Dimension(s(800), s(500)));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_WHITE);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        setVisible(true);
    }

    /* ===================== HEADER ===================== */

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(s(16), s(32), s(16), s(32))
        ));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, s(10), 0));
        logoPanel.setOpaque(false);

        int logoSize = s(32);
        if (appIcon != null) {
            double ratio = Math.min(
                    (double) logoSize / appIcon.getWidth(),
                    (double) logoSize / appIcon.getHeight()
            );
            int w = Math.max(1, (int) (appIcon.getWidth() * ratio));
            int h = Math.max(1, (int) (appIcon.getHeight() * ratio));
            JLabel logo = new JLabel(new ImageIcon(
                    appIcon.getScaledInstance(w, h, Image.SCALE_SMOOTH)
            ));
            logoPanel.add(logo);
        }

        JLabel brandName = new JLabel("AlugaCar");
        brandName.setFont(new Font("SansSerif", Font.BOLD, s(22)));
        brandName.setForeground(BRAND_COLOR);
        logoPanel.add(brandName);

        header.add(logoPanel, BorderLayout.WEST);
        return header;
    }

    /* ===================== MAIN CONTENT ===================== */

    private JPanel createMainContent() {
        JPanel main = new JPanel(new GridLayout(1, 2));
        main.setBackground(BG_WHITE);

        // Left side - Login form
        main.add(createLeftPanel());

        // Right side - Illustration
        main.add(createRightPanel());

        return main;
    }

    /* ===================== LEFT PANEL - FORM ===================== */

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(BG_WHITE);
        left.setBorder(BorderFactory.createEmptyBorder(s(40), s(80), s(40), s(60)));

        // Container with max width
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        formContainer.setMaximumSize(new Dimension(s(400), Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;

        // Title
        JLabel title = new JLabel("Acelere sua jornada");
        title.setFont(new Font("SansSerif", Font.BOLD, s(36)));
        title.setForeground(new Color(191, 90, 45)); // Warm orange-red
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, s(6), 0);
        formContainer.add(title, gbc);

        // Subtitle
        JLabel subtitle = new JLabel("Faça login para continuar");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, s(16)));
        subtitle.setForeground(TEXT_SECONDARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, s(32), 0);
        formContainer.add(subtitle, gbc);

        // Email label
        JLabel emailLabel = new JLabel("E-mail ou telefone");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, s(14)));
        emailLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, s(6), 0);
        left.add(emailLabel, gbc);

        // Email field
        HintTextField emailField = createModernField("");
        emailField.setMaximumSize(new Dimension(s(400), s(48)));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, s(16), 0);
        formContainer.add(emailField, gbc);

        // Password label
        JLabel passLabel = new JLabel("Senha");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, s(14)));
        passLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, s(6), 0);
        formContainer.add(passLabel, gbc);

        // Password field
        HintPasswordField passField = createModernPassField("");
        passField.setMaximumSize(new Dimension(s(400), s(48)));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, s(8), 0);
        formContainer.add(passField, gbc);

        // Forgot password link (aligned right)
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPanel.setOpaque(false);
        JLabel forgotLink = new JLabel("Esqueceu a senha?");
        forgotLink.setFont(new Font("SansSerif", Font.PLAIN, s(14)));
        forgotLink.setForeground(BRAND_COLOR);
        forgotLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLink.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                forgotLink.setText("<html><u>Esqueceu a senha?</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                forgotLink.setText("Esqueceu a senha?");
            }
        });
        forgotPanel.add(forgotLink);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, s(20), 0);
        formContainer.add(forgotPanel, gbc);

        // Status message
        JLabel status = new JLabel(" ", SwingConstants.LEFT);
        status.setFont(new Font("SansSerif", Font.PLAIN, s(12)));
        status.setForeground(new Color(220, 53, 69));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, s(8), 0);
        formContainer.add(status, gbc);

        // Login button
        RoundedButton loginBtn = new RoundedButton("Entrar");
        loginBtn.setPreferredSize(new Dimension(s(400), s(48)));
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, s(16)));
        loginBtn.setBackground(BRAND_COLOR);
        loginBtn.setForeground(Color.WHITE);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, s(16), 0);
        formContainer.add(loginBtn, gbc);

        // Divider with "ou"
        JPanel divider = createDividerWithText("ou");
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, s(16), 0);
        formContainer.add(divider, gbc);

        // Alternative login button
        RoundedButton altBtn = new RoundedButton("Entrar com navegador");
        altBtn.setPreferredSize(new Dimension(s(400), s(48)));
        altBtn.setFont(new Font("SansSerif", Font.PLAIN, s(14)));
        altBtn.setBackground(Color.WHITE);
        altBtn.setForeground(TEXT_SECONDARY);
        altBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(s(12), s(16), s(12), s(16))
        ));
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, s(20), 0);
        formContainer.add(altBtn, gbc);

        // Sign up text
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, s(4), 0));
        signupPanel.setOpaque(false);
        JLabel newHere = new JLabel("Ainda não faz parte do AlugaCar?");
        newHere.setFont(new Font("SansSerif", Font.PLAIN, s(14)));
        newHere.setForeground(TEXT_PRIMARY);
        JLabel signupLink = new JLabel("Cadastre-se agora");
        signupLink.setFont(new Font("SansSerif", Font.BOLD, s(14)));
        signupLink.setForeground(BRAND_COLOR);
        signupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLink.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                signupLink.setText("<html><u>Cadastre-se agora</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                signupLink.setText("Cadastre-se agora");
            }
        });
        signupPanel.add(newHere);
        signupPanel.add(signupLink);

        gbc.gridy = 11;
        gbc.insets = new Insets(0, 0, 0, 0);
        formContainer.add(signupPanel, gbc);

        setupLogin(emailField, passField, loginBtn, status);

        left.add(formContainer);
        return left;
    }

    /* ===================== RIGHT PANEL - ILLUSTRATION ===================== */

    private JPanel createRightPanel() {
        JPanel right = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw illustration background with gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, ILLUSTRATION_BG,
                        getWidth(), getHeight(), new Color(230, 240, 245)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw large decorative circle
                g2.setColor(new Color(255, 255, 255, 40));
                int circleSize = s(400);
                g2.fillOval(getWidth() - circleSize/2, getHeight()/2 - circleSize/2, circleSize, circleSize);

                // Draw car-related illustration (simplified)
                drawCarIllustration(g2);
            }
        };
        right.setBackground(ILLUSTRATION_BG);
        right.setBorder(BorderFactory.createEmptyBorder(s(40), s(40), s(40), s(40)));

        // Add text overlay
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel illustText = new JLabel("Seu próximo carro");
        illustText.setFont(new Font("SansSerif", Font.BOLD, s(32)));
        illustText.setForeground(new Color(40, 40, 40));
        illustText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel illustSubtext = new JLabel("está a um clique de distância");
        illustSubtext.setFont(new Font("SansSerif", Font.PLAIN, s(20)));
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

        // Draw simple car silhouette
        g2.setColor(new Color(10, 102, 194, 180));

        // Car body
        int carW = s(220);
        int carH = s(80);
        int carX = cx - carW/2;
        int carY = cy - carH/2;

        // Main body (rounded rectangle)
        g2.fillRoundRect(carX, carY, carW, carH, s(15), s(15));

        // Car top/cabin
        int cabinW = s(120);
        int cabinH = s(50);
        int cabinX = carX + s(40);
        int cabinY = carY - s(35);
        g2.fillRoundRect(cabinX, cabinY, cabinW, cabinH, s(12), s(12));

        // Windows
        g2.setColor(new Color(200, 220, 240, 200));
        g2.fillRoundRect(cabinX + s(8), cabinY + s(8), s(50), s(34), s(8), s(8));
        g2.fillRoundRect(cabinX + s(62), cabinY + s(8), s(50), s(34), s(8), s(8));

        // Wheels
        g2.setColor(new Color(40, 40, 40, 200));
        int wheelD = s(32);
        g2.fillOval(carX + s(20), carY + carH - s(8), wheelD, wheelD);
        g2.fillOval(carX + carW - s(52), carY + carH - s(8), wheelD, wheelD);

        // Wheel rims
        g2.setColor(new Color(180, 180, 180, 200));
        int rimD = s(18);
        g2.fillOval(carX + s(20) + (wheelD-rimD)/2, carY + carH - s(8) + (wheelD-rimD)/2, rimD, rimD);
        g2.fillOval(carX + carW - s(52) + (wheelD-rimD)/2, carY + carH - s(8) + (wheelD-rimD)/2, rimD, rimD);

        // Road lines
        g2.setColor(new Color(180, 180, 180, 100));
        g2.setStroke(new BasicStroke(s(3), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{s(20), s(15)}, 0));
        g2.drawLine(s(40), cy + s(80), w - s(40), cy + s(80));
    }

    /* ===================== HELPERS ===================== */

    private HintTextField createModernField(String hint) {
        HintTextField f = new HintTextField(hint);
        f.setFont(new Font("SansSerif", Font.PLAIN, s(15)));
        f.setPreferredSize(new Dimension(0, s(48)));
        f.setBackground(Color.WHITE);
        f.setForeground(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(s(12), s(14), s(12), s(14))
        ));

        // Focus effect
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BRAND_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(s(11), s(13), s(11), s(13))
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(s(12), s(14), s(12), s(14))
                ));
            }
        });

        return f;
    }

    private HintPasswordField createModernPassField(String hint) {
        HintPasswordField f = new HintPasswordField(hint);
        f.setFont(new Font("SansSerif", Font.PLAIN, s(15)));
        f.setPreferredSize(new Dimension(0, s(48)));
        f.setBackground(Color.WHITE);
        f.setForeground(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(s(12), s(14), s(12), s(14))
        ));

        // Focus effect
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BRAND_COLOR, 2, true),
                        BorderFactory.createEmptyBorder(s(11), s(13), s(11), s(13))
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(s(12), s(14), s(12), s(14))
                ));
            }
        });

        return f;
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

    /* ===================== LOGIN ===================== */

    private void setupLogin(HintTextField user, HintPasswordField pass,
                            RoundedButton btn, JLabel status) {

        Runnable login = () -> UIHelper.statusMessage(
                status,
                "Autenticação simulada - Login realizado com sucesso!",
                UIHelper.MessageType.INFO
        );

        btn.addActionListener(e -> login.run());
        user.addActionListener(e -> login.run());
        pass.addActionListener(e -> login.run());

        getRootPane().setDefaultButton(btn);
    }

    private int s(int v) {
        return (int) Math.round(v * scale);
    }
}
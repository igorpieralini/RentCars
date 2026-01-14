package me.pieralini.com.ui.view;

import me.pieralini.com.util.Database;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Taskbar;

public class MainPageFrame extends JFrame {

    private static final Color BG_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    private static final Color TEXT_SECONDARY = new Color(96, 96, 96);
    private static final Color BRAND_COLOR = new Color(10, 102, 194);
    private static final Color ACCENT = new Color(0, 115, 177);
    private static final Color ILLUSTRATION_BG = new Color(245, 248, 250);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);

    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(220, 220, 220);

    private final BufferedImage appIcon;
    private JPanel cardsPanel;

    public MainPageFrame() {
        this(null);
    }

    public MainPageFrame(BufferedImage icon) {
        super("AlugaCar — Encontre seu carro ideal");

        this.appIcon = icon;

        setupFrame();
        buildUI();
        setupResponsiveness();
    }

    private void setupFrame() {
        if (appIcon != null) {
            setIconImage(appIcon);
            if (Taskbar.isTaskbarSupported()) {
                try {
                    Taskbar taskbar = Taskbar.getTaskbar();
                    if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                        taskbar.setIconImage(appIcon);
                    }
                } catch (Exception ignored) {}
            }
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(900, 600));
        getContentPane().setBackground(ILLUSTRATION_BG);
    }

    private void buildUI() {
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ILLUSTRATION_BG);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(ILLUSTRATION_BG);
        JLabel titleLabel = new JLabel("Carros Disponíveis");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel cardsContainer = new JPanel(new BorderLayout());
        cardsContainer.setBackground(ILLUSTRATION_BG);

        cardsPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        cardsPanel.setBackground(ILLUSTRATION_BG);

        cardsContainer.add(cardsPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        loadCars(cardsPanel);
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

        int columns;
        if (width < 800) {
            columns = 1;
        } else if (width < 1100) {
            columns = 1;
        } else {
            columns = 2;
        }

        GridLayout layout = (GridLayout) cardsPanel.getLayout();
        if (layout.getColumns() != columns) {
            cardsPanel.setLayout(new GridLayout(0, columns, 20, 20));
            cardsPanel.revalidate();
            cardsPanel.repaint();
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BRAND_COLOR);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(new EmptyBorder(15, 40, 15, 40));

        JLabel logo = new JLabel("AlugaCar");
        logo.setFont(new Font("SansSerif", Font.BOLD, 32));
        logo.setForeground(Color.WHITE);
        header.add(logo, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(250, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JButton searchBtn = new JButton("Buscar");
        searchBtn.setBackground(BG_WHITE);
        searchBtn.setForeground(BRAND_COLOR);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(90, 40));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private void loadCars(JPanel cardsPanel) {
        try {
            ResultSet rs = Database.query(
                    "SELECT c.id, cm.name as model_name, c.year, c.price, c.license_plate, " +
                            "c.mileage, c.available " +
                            "FROM cars c " +
                            "JOIN car_models cm ON c.car_model_id = cm.id " +
                            "ORDER BY c.available DESC, c.year DESC"
            );

            int count = 0;
            while (rs.next()) {
                String fullName = rs.getString("model_name");
                String brand = "";
                String model = fullName;

                if (fullName != null && fullName.contains(" ")) {
                    String[] parts = fullName.split(" ", 2);
                    brand = parts[0];
                    model = parts[1];
                }

                JPanel card = createCarCard(
                        rs.getInt("id"),
                        brand,
                        model,
                        rs.getInt("year"),
                        rs.getDouble("price"),
                        rs.getString("license_plate"),
                        rs.getInt("mileage"),
                        rs.getInt("available")
                );
                cardsPanel.add(card);
                count++;
            }

            if (count == 0) {
                JLabel emptyLabel = new JLabel("Nenhum carro disponível no momento");
                emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
                emptyLabel.setForeground(TEXT_SECONDARY);
                emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                cardsPanel.add(emptyLabel);
            }

            cardsPanel.revalidate();
            cardsPanel.repaint();

        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Erro ao carregar carros: " + e.getMessage());
            errorLabel.setForeground(ERROR_COLOR);
            errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            cardsPanel.add(errorLabel);
            e.printStackTrace();
        }
    }

    private JPanel createCarCard(
            int id,
            String brand,
            String model,
            int year,
            double price,
            String licensePlate,
            int mileage,
            int available
    ) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(0, 0, 0, 0)
        ));

        JPanel mainContainer = new JPanel(new BorderLayout(15, 0));
        mainContainer.setBackground(CARD_BG);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(180, 150));
        imagePanel.setBackground(ILLUSTRATION_BG);
        imagePanel.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        JLabel imagePlaceholder = new JLabel("🚗", SwingConstants.CENTER);
        imagePlaceholder.setFont(new Font("SansSerif", Font.PLAIN, 60));
        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(imagePlaceholder, BorderLayout.CENTER);

        if (available == 1) {
            JLabel badge = new JLabel("DISPONÍVEL");
            badge.setFont(new Font("SansSerif", Font.BOLD, 9));
            badge.setForeground(Color.WHITE);
            badge.setBackground(SUCCESS_COLOR);
            badge.setOpaque(true);
            badge.setHorizontalAlignment(SwingConstants.CENTER);
            badge.setBorder(new EmptyBorder(3, 6, 3, 6));
            JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            badgePanel.setOpaque(false);
            badgePanel.add(badge);
            imagePanel.add(badgePanel, BorderLayout.NORTH);
        }

        mainContainer.add(imagePanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_BG);

        String displayName = brand.isEmpty() ? model : brand + " " + model;
        JLabel titleLabel = new JLabel(displayName);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel yearLabel = new JLabel(String.valueOf(year));
        yearLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        yearLabel.setForeground(TEXT_SECONDARY);
        yearLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(yearLabel);
        infoPanel.add(Box.createVerticalStrut(12));

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        detailsPanel.setBackground(CARD_BG);
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel kmLabel = new JLabel("🛣️ " + String.format("%,d", mileage) + " km");
        kmLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        kmLabel.setForeground(TEXT_SECONDARY);

        JLabel plateLabel = new JLabel("🔖 " + licensePlate);
        plateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        plateLabel.setForeground(TEXT_SECONDARY);

        detailsPanel.add(kmLabel);
        detailsPanel.add(plateLabel);
        infoPanel.add(detailsPanel);
        infoPanel.add(Box.createVerticalGlue());

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(CARD_BG);
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pricePanel.setBackground(CARD_BG);

        JLabel priceLabel = new JLabel("R$ " + String.format("%,.2f", price));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        priceLabel.setForeground(BRAND_COLOR);

        JLabel perDayLabel = new JLabel(" /dia");
        perDayLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        perDayLabel.setForeground(TEXT_SECONDARY);

        pricePanel.add(priceLabel);
        pricePanel.add(perDayLabel);

        JButton rentButton = new JButton(available == 1 ? "Alugar agora" : "Indisponível");
        rentButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        rentButton.setBackground(available == 1 ? BRAND_COLOR : TEXT_SECONDARY);
        rentButton.setForeground(Color.WHITE);
        rentButton.setFocusPainted(false);
        rentButton.setBorder(new EmptyBorder(8, 20, 8, 20));
        rentButton.setCursor(available == 1 ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        rentButton.setEnabled(available == 1);

        if (available == 1) {
            rentButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    rentButton.setBackground(ACCENT);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    rentButton.setBackground(BRAND_COLOR);
                }
            });
        }

        bottomPanel.add(pricePanel, BorderLayout.WEST);
        bottomPanel.add(rentButton, BorderLayout.EAST);

        infoPanel.add(bottomPanel);

        mainContainer.add(infoPanel, BorderLayout.CENTER);
        card.add(mainContainer, BorderLayout.CENTER);

        return card;
    }
}
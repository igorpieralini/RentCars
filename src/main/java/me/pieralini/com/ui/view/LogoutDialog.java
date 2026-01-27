package me.pieralini.com.ui.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LogoutDialog extends JDialog {

    private static final Color BG_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    private static final Color TEXT_SECONDARY = new Color(96, 96, 96);
    private static final Color BRAND_COLOR = new Color(10, 102, 194);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color DANGER_HOVER = new Color(200, 35, 51);
    private static final Color CANCEL_COLOR = new Color(108, 117, 125);
    private static final Color CANCEL_HOVER = new Color(90, 98, 104);

    private boolean confirmed = false;

    public LogoutDialog(Frame parent) {
        super(parent, "Confirmar Saída", true);
        setupDialog();
        buildUI();
    }

    private void setupDialog() {
        setSize(450, 250);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_WHITE);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Painel superior com ícone e mensagem
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_WHITE);
        contentPanel.setBorder(new EmptyBorder(30, 40, 20, 40));

        // Ícone
        JLabel iconLabel = new JLabel("👋");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Título
        JLabel titleLabel = new JLabel("Deseja sair?");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        // Mensagem
        JLabel messageLabel = new JLabel("Você será desconectado do sistema");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_SECONDARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(messageLabel);

        add(contentPanel, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BG_WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 40, 30, 40));

        JButton cancelButton = createButton("Cancelar", CANCEL_COLOR, CANCEL_HOVER);
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JButton confirmButton = createButton("Sair", DANGER_COLOR, DANGER_HOVER);
        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ESC para cancelar
        getRootPane().registerKeyboardAction(
                e -> {
                    confirmed = false;
                    dispose();
                },
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private JButton createButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 25, 10, 25));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
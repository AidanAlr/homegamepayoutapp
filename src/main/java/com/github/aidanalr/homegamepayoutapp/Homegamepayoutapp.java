package com.github.aidanalr.homegamepayoutapp;

import com.github.aidanalr.homegamepayoutapp.backend.HomeGame;
import com.github.aidanalr.homegamepayoutapp.backend.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Homegamepayoutapp extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color FOREGROUND_COLOR = Color.BLACK;
    private static final Color ALT_FOREGROUND_COLOR = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color SECONDARY_BACKGROUND = new Color(45, 45, 45);
    private static final Font MAIN_FONT = new Font("Roboto", Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font("Roboto", Font.BOLD, 18);

    private HomeGame game;
    private JTextField nameField, buyInField, cashOutField;
    private JTable playerTable;
    private DefaultTableModel tableModel;
    private JButton addPlayerButton, calculatePayoutsButton;
    private JLabel statusLabel;
    private List<Player> players;

    public Homegamepayoutapp() {
        game = new HomeGame();
        players = new ArrayList<>();

        setTitle("Poker Payout Calculator");
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        setupHeader(mainPanel);
        setupInputAndTablePanel(mainPanel);
        setupStatusBar(mainPanel);

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void setupHeader(JPanel mainPanel) {
        JLabel headerLabel = new JLabel("Poker Payout Calculator", SwingConstants.CENTER);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(ALT_FOREGROUND_COLOR);
        headerLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
    }

    private void setupInputAndTablePanel(JPanel mainPanel) {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(BACKGROUND_COLOR);

        nameField = createStyledTextField(20);
        buyInField = createStyledTextField(10);
        cashOutField = createStyledTextField(10);

        inputPanel.add(createStyledLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(createStyledLabel("Buy-in:"));
        inputPanel.add(buyInField);
        inputPanel.add(createStyledLabel("Cash-out:"));
        inputPanel.add(cashOutField);

        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(getButtonPanel(mainPanel), BorderLayout.SOUTH);
        setupPlayerTable(centerPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void setupPlayerTable(JPanel centerPanel) {
        tableModel = new DefaultTableModel(new String[]{"Name", "Buy-in", "Cash-out", "Net"}, 0);
        playerTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? BACKGROUND_COLOR : SECONDARY_BACKGROUND);
                }
                return c;
            }
        };
        playerTable.setBackground(BACKGROUND_COLOR);
        playerTable.setForeground(ALT_FOREGROUND_COLOR);
        playerTable.setFont(MAIN_FONT);
        playerTable.setGridColor(ACCENT_COLOR);
        playerTable.getTableHeader().setBackground(ACCENT_COLOR);
        playerTable.getTableHeader().setForeground(FOREGROUND_COLOR);
        playerTable.getTableHeader().setFont(MAIN_FONT.deriveFont(Font.BOLD));
        playerTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(playerTable);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel getButtonPanel(JPanel mainPanel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        addPlayerButton = createStyledButton("Add Player");
        addPlayerButton.addActionListener(new AddPlayerListener());
        calculatePayoutsButton = createStyledButton("Calculate Payouts");
        calculatePayoutsButton.addActionListener(new CalculatePayoutsListener());
        buttonPanel.add(addPlayerButton);
        buttonPanel.add(calculatePayoutsButton);
        return buttonPanel;
    }

    private void setupStatusBar(JPanel mainPanel) {
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(FOREGROUND_COLOR);
        statusLabel.setFont(MAIN_FONT);
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ALT_FOREGROUND_COLOR);
        label.setFont(MAIN_FONT);
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setBackground(SECONDARY_BACKGROUND);
        textField.setForeground(FOREGROUND_COLOR);
        textField.setFont(MAIN_FONT);
        textField.setCaretColor(FOREGROUND_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(FOREGROUND_COLOR);
        button.setFont(MAIN_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private class AddPlayerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showStatus("Please enter a name.", true);
                return;
            }

            int buyIn, cashOut;
            try {
                buyIn = Integer.parseInt(buyInField.getText());
                cashOut = Integer.parseInt(cashOutField.getText());
            } catch (NumberFormatException ex) {
                showStatus("Invalid buy-in or cash-out amount.", true);
                return;
            }

            Player player = new Player(name, buyIn, cashOut);
            players.add(player);
            game.addPlayer(player);

            updatePlayerTable();
            showStatus("Player added successfully.", false);

            // Clear input fields
            nameField.setText("");
            buyInField.setText("");
            cashOutField.setText("");
        }
    }

    private class CalculatePayoutsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (players.isEmpty()) {
                showStatus("Please add players before calculating payouts.", true);
                return;
            }

            ArrayList<String> payouts = game.calculatePayouts();
            updatePlayerTable();

            StringBuilder payoutMessage = new StringBuilder("Payouts:\n");
            for(String payoutString: payouts){
                payoutMessage.append(payoutString).append("\n");
            }
            JTextArea payoutArea = new JTextArea(payoutMessage.toString());
            payoutArea.setEditable(false);
            payoutArea.setBackground(BACKGROUND_COLOR);
            payoutArea.setForeground(ALT_FOREGROUND_COLOR);
            payoutArea.setFont(MAIN_FONT);

            JScrollPane scrollPane = new JScrollPane(payoutArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(Homegamepayoutapp.this, scrollPane, "Payout Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updatePlayerTable() {
        tableModel.setRowCount(0);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        for (Player player : players) {
            tableModel.addRow(new Object[]{
                    player.getName(),
                    currencyFormat.format(player.getBuyIn()),
                    currencyFormat.format(player.getCashOut()),
                    currencyFormat.format(player.getProfit())
            });
        }
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : ALT_FOREGROUND_COLOR);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Homegamepayoutapp frame = new Homegamepayoutapp();
            frame.setVisible(true);
        });
    }
}
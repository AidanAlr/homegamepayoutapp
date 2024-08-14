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

public class Homegamepayoutapp extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color FOREGROUND_COLOR = Color.BLACK;
    private static final Color ALT_FOREGROUND_COLOR = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color SECONDARY_BACKGROUND = new Color(45, 45, 45);
    private static final Font MAIN_FONT = new Font("Roboto", Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font("Roboto", Font.BOLD, 18);

    private final HomeGame game;
    private JTextField nameField, buyInField, cashOutField;
    private DefaultTableModel tableModel;
    private JLabel currentBuyinCashoutLabel;

    public Homegamepayoutapp() {
        game = new HomeGame();

        setTitle("Poker Payout Calculator");
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        mainPanel.add(getHeader(), BorderLayout.NORTH);
        setupInputAndTablePanel(mainPanel);
        setupStatusBar(mainPanel);
        add(mainPanel, BorderLayout.CENTER);

        pack();
        setSize(600, 600);
        setLocationRelativeTo(null);
    }
    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.reset();
            updatePlayerTable();
            updateCurrentBuyinCashoutLabel();
            nameField.setText("");
            buyInField.setText("");
            cashOutField.setText("");
        }
    }

    private JButton createResetButton() {
        JButton resetButton = createStyledButton("Reset");
        resetButton.addActionListener(new ResetButtonListener());
        return resetButton;
    }

    private JPanel getHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel headerLabel = new JLabel("Poker Payout Calculator", SwingConstants.CENTER);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(ALT_FOREGROUND_COLOR);
        headerLabel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton resetButton = createResetButton();

        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(resetButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void setupInputAndTablePanel(JPanel mainPanel) {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);

        // Setup input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 0, 15));
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(BACKGROUND_COLOR);


        // Create input fields
        nameField = createStyledTextField(20);
        buyInField = createStyledTextField(10);
        cashOutField = createStyledTextField(10);

        // Add input fields to input panel
        inputPanel.add(createStyledLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(createStyledLabel("Buy-in:"));
        inputPanel.add(buyInField);
        inputPanel.add(createStyledLabel("Cash-out:"));
        inputPanel.add(cashOutField);

        // Add input panel and button panel to center panel, set up player table
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(getButtonPanel(), BorderLayout.SOUTH);
        setupPlayerTable(centerPanel);

        // Add center panel to main panel
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void setupPlayerTable(JPanel centerPanel) {
        tableModel = new DefaultTableModel(new String[]{"Name", "Buy-in", "Cash-out", "Net"}, 0);
        JTable playerTable = new JTable(tableModel) {
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

    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton addPlayerButton = createStyledButton("Add Player");
        addPlayerButton.addActionListener(new AddPlayerListener());
        JButton calculatePayoutsButton = createStyledButton("Calculate Payouts");
        calculatePayoutsButton.addActionListener(new CalculatePayoutsListener());
        buttonPanel.add(addPlayerButton);
        buttonPanel.add(calculatePayoutsButton);
        return buttonPanel;
    }

    private void setupStatusBar(JPanel mainPanel) {
        currentBuyinCashoutLabel = new JLabel("Total Buyin: " + game.getTotalBuyin() + " Total Cashout: " + game.getTotalCashout());
        currentBuyinCashoutLabel.setForeground(ALT_FOREGROUND_COLOR);
        mainPanel.add(currentBuyinCashoutLabel, BorderLayout.SOUTH);
    }

    private void updateCurrentBuyinCashoutLabel(){
        currentBuyinCashoutLabel.setText("Current Buyin: " + game.getTotalBuyin() + " Current Cashout: " + game.getTotalCashout());
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
                return;
            }

            float buyIn;
            float cashOut;
            try {
                buyIn = Float.parseFloat(buyInField.getText().trim());
                cashOut = Float.parseFloat(cashOutField.getText().trim());
            } catch (NumberFormatException ex) {
                return;
            }
            // Create a new player and add them to the game
            Player player = new Player(name, buyIn, cashOut);
            game.addPlayer(player);

            updatePlayerTable();
            updateCurrentBuyinCashoutLabel();

            // Clear input fields
            nameField.setText("");
            buyInField.setText("");
            cashOutField.setText("");
        }
    }

    private class CalculatePayoutsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (game.getParticipants().isEmpty()) {

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
        for (Player player : game.getParticipants()) {
            tableModel.addRow(new Object[]{
                    player.getName(),
                    currencyFormat.format(player.getBuyIn()),
                    currencyFormat.format(player.getCashOut()),
                    currencyFormat.format(player.getBuyIn() - player.getCashOut())
            });
        }
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
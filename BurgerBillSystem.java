import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BurgerBillSystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private String customerName;
    private String customerPhone;
    private ArrayList<String> orderItems = new ArrayList<>();
    private double totalAmount = 0.0;

    public BurgerBillSystem() {
        setTitle("Burger King Bill Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        createCustomerNameWindow();
        createBurgerWindow();
        createFriesWindow();
        createDrinkWindow();
        createTotalWindow();
        createBillWindow();

        add(cardPanel);
    }

    private void createCustomerNameWindow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 222, 173));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Enter Customer Name:");
        JTextField nameField = new JTextField(15);
        JLabel phoneLabel = new JLabel("Enter Phone Number:");
        JTextField phoneField = new JTextField(15);
        JButton nextButton = new JButton("Next");

        nextButton.addActionListener(e -> {
            customerName = nameField.getText().trim();
            customerPhone = phoneField.getText().trim();

            if (customerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Customer name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (customerPhone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone number cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!customerPhone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit phone number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            cardLayout.next(cardPanel);
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridy++;
        panel.add(nameField, gbc);
        gbc.gridy++;
        panel.add(phoneLabel, gbc);
        gbc.gridy++;
        panel.add(phoneField, gbc);
        gbc.gridy++;
        panel.add(nextButton, gbc);

        cardPanel.add(panel, "CustomerName");
    }

    private void createBurgerWindow() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 178, 102));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] burgerTypes = {"Veg Whopper", "Paneer Royale", "Crispy Veg"};
        String[] sizes = {"Small", "Medium", "Large"};
        String[] addons = {"Cheese", "Extra Sauce", "Jalapenos"};

        JComboBox<String> burgerCombo = new JComboBox<>(burgerTypes);
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);

        JPanel addonsPanel = new JPanel();
        addonsPanel.setLayout(new BoxLayout(addonsPanel, BoxLayout.Y_AXIS));
        addonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addonsPanel.setBorder(BorderFactory.createTitledBorder("Select Addons"));

        JCheckBox cheeseCheck = new JCheckBox("Cheese");
        JCheckBox sauceCheck = new JCheckBox("Extra Sauce");
        JCheckBox jalapenosCheck = new JCheckBox("Jalapenos");
        addonsPanel.add(cheeseCheck);
        addonsPanel.add(sauceCheck);
        addonsPanel.add(jalapenosCheck);

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel qtyLabel = new JLabel("Select Quantity:");
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantityPanel.add(qtyLabel);
        quantityPanel.add(qtySpinner);

        JButton addButton = new JButton("Add to Bill");
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel amountLabel = new JLabel("Amount: ₹0.00");
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ActionListener calculateAmountListener = e -> {
            String burger = (String) burgerCombo.getSelectedItem();
            String size = (String) sizeCombo.getSelectedItem();
            int quantity = (int) qtySpinner.getValue();
            ArrayList<String> selectedAddons = new ArrayList<>();

            if (cheeseCheck.isSelected()) selectedAddons.add("Cheese");
            if (sauceCheck.isSelected()) selectedAddons.add("Extra Sauce");
            if (jalapenosCheck.isSelected()) selectedAddons.add("Jalapenos");

            double amount = calculateBurgerAmount(burger, size, selectedAddons) * quantity;
            amountLabel.setText(String.format("Amount: ₹%.2f", amount));
        };

        burgerCombo.addActionListener(calculateAmountListener);
        sizeCombo.addActionListener(calculateAmountListener);
        cheeseCheck.addActionListener(calculateAmountListener);
        sauceCheck.addActionListener(calculateAmountListener);
        jalapenosCheck.addActionListener(calculateAmountListener);
        qtySpinner.addChangeListener(e -> calculateAmountListener.actionPerformed(null));

        addButton.addActionListener(e -> {
            String burger = (String) burgerCombo.getSelectedItem();
            String size = (String) sizeCombo.getSelectedItem();
            int quantity = (int) qtySpinner.getValue();
            ArrayList<String> selectedAddons = new ArrayList<>();

            if (cheeseCheck.isSelected()) selectedAddons.add("Cheese");
            if (sauceCheck.isSelected()) selectedAddons.add("Extra Sauce");
            if (jalapenosCheck.isSelected()) selectedAddons.add("Jalapenos");

            double amountPerItem = calculateBurgerAmount(burger, size, selectedAddons);
            double totalItemAmount = amountPerItem * quantity;
            totalAmount += totalItemAmount;
            orderItems.add(String.format("%s (%s) x%d - ₹%.2f", burger, size, quantity, totalItemAmount));
            amountLabel.setText(String.format("Amount: ₹%.2f", totalItemAmount));

            cheeseCheck.setSelected(false);
            sauceCheck.setSelected(false);
            jalapenosCheck.setSelected(false);
            qtySpinner.setValue(1);
            calculateAmountListener.actionPerformed(null);

            JOptionPane.showMessageDialog(this, "Burger added to bill!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton nextButton = new JButton("Next");
        nextButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextButton.addActionListener(e -> cardLayout.next(cardPanel));

        panel.add(createLabeledComponent("Select Burger:", burgerCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createLabeledComponent("Select Size:", sizeCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addonsPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(quantityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(amountLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(addButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(nextButton);

        cardPanel.add(panel, "Burger");
    }

    private void createFriesWindow() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 204, 153));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] friesTypes = {"Classic Fries", "Cheesy Fries", "Peri Peri Fries"};
        String[] sizes = {"Small", "Medium", "Large"};

        JComboBox<String> friesCombo = new JComboBox<>(friesTypes);
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel qtyLabel = new JLabel("Select Quantity:");
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantityPanel.add(qtyLabel);
        quantityPanel.add(qtySpinner);

        JButton addButton = new JButton("Add to Bill");
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel amountLabel = new JLabel("Amount: ₹0.00");
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ActionListener calculateAmountListener = e -> {
            String fries = (String) friesCombo.getSelectedItem();
            String size = (String) sizeCombo.getSelectedItem();
            int quantity = (int) qtySpinner.getValue();
            double amount = calculateFriesAmount(fries, size) * quantity;
            amountLabel.setText(String.format("Amount: ₹%.2f", amount));
        };

        friesCombo.addActionListener(calculateAmountListener);
        sizeCombo.addActionListener(calculateAmountListener);
        qtySpinner.addChangeListener(e -> calculateAmountListener.actionPerformed(null));

        addButton.addActionListener(e -> {
            String fries = (String) friesCombo.getSelectedItem();
            String size = (String) sizeCombo.getSelectedItem();
            int quantity = (int) qtySpinner.getValue();
            double amountPerItem = calculateFriesAmount(fries, size);
            double totalItemAmount = amountPerItem * quantity;
            totalAmount += totalItemAmount;
            orderItems.add(String.format("%s (%s) x%d - ₹%.2f", fries, size, quantity, totalItemAmount));
            amountLabel.setText(String.format("Amount: ₹%.2f", totalItemAmount));

            qtySpinner.setValue(1);
            calculateAmountListener.actionPerformed(null);

            JOptionPane.showMessageDialog(this, "Fries added to bill!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton nextButton = new JButton("Next");
        nextButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextButton.addActionListener(e -> cardLayout.next(cardPanel));

        panel.add(createLabeledComponent("Select Fries:", friesCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createLabeledComponent("Select Size:", sizeCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(quantityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(amountLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(addButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(nextButton);

        cardPanel.add(panel, "Fries");
    }

    private void createDrinkWindow() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(204, 255, 204));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] drinkTypes = {"Cola", "Lemonade", "Iced Tea"};
        String[] sizes = {"Small", "Medium", "Large"};

        JComboBox<String> drinkCombo = new JComboBox<>(drinkTypes);
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel qtyLabel = new JLabel("Select Quantity:");
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantityPanel.add(qtyLabel);
        quantityPanel.add(qtySpinner);

        JButton addButton = new JButton("Add to Bill");
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel amountLabel = new JLabel("Amount: ₹0.00");
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ActionListener calculateAmountListener = e -> {
            String drink = (String) drinkCombo.getSelectedItem();
            String size = (String) sizeCombo.getSelectedItem();
            int quantity = (int) qtySpinner.getValue();
            double amount = calculateDrinkAmount(drink, size) * quantity;
            amountLabel.setText(String.format("Amount: ₹%.2f", amount));
        };

        drinkCombo.addActionListener(calculateAmountListener);
        sizeCombo.addActionListener(calculateAmountListener);
        qtySpinner.addChangeListener(e -> calculateAmountListener.actionPerformed(null));

        addButton.addActionListener(e -> {
            String drink = (String) drinkCombo.getSelectedItem();
            String size = (String) sizeCombo.getSelectedItem();
            int quantity = (int) qtySpinner.getValue();
            double amountPerItem = calculateDrinkAmount(drink, size);
            double totalItemAmount = amountPerItem * quantity;
            totalAmount += totalItemAmount;
            orderItems.add(String.format("%s (%s) x%d - ₹%.2f", drink, size, quantity, totalItemAmount));
            amountLabel.setText(String.format("Amount: ₹%.2f", totalItemAmount));

            qtySpinner.setValue(1);
            calculateAmountListener.actionPerformed(null);

            JOptionPane.showMessageDialog(this, "Drink added to bill!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton nextButton = new JButton("Next");
        nextButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextButton.addActionListener(e -> {
            cardLayout.next(cardPanel);
            updateTotalWindow();
        });

        panel.add(createLabeledComponent("Select Drink:", drinkCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createLabeledComponent("Select Size:", sizeCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(quantityPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(amountLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(addButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(nextButton);

        cardPanel.add(panel, "Drink");
    }

    private void createTotalWindow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(204, 229, 255));

        JLabel totalLabel = new JLabel();
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        totalLabel.setForeground(new Color(0, 0, 128));

        JTextField paymentField = new JTextField(10);
        JLabel balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JLabel insufficientLabel = new JLabel();
        insufficientLabel.setForeground(Color.RED);
        insufficientLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JButton calculateButton = new JButton("Calculate Balance");
        calculateButton.setPreferredSize(new Dimension(150, 30));
        calculateButton.addActionListener(e -> {
            try {
                double payment = Double.parseDouble(paymentField.getText());
                double balance = payment - totalAmount;
                if (balance < 0) {
                    insufficientLabel.setText("Insufficient Amount");
                    balanceLabel.setText("");
                } else {
                    insufficientLabel.setText("");
                    balanceLabel.setText(String.format("Balance: ₹%.2f", balance));
                }
            } catch (NumberFormatException ex) {
                balanceLabel.setText("Invalid payment amount");
                insufficientLabel.setText("");
            }
        });

        JButton nextButton = new JButton("Generate Bill");
        nextButton.setPreferredSize(new Dimension(150, 30));
        nextButton.addActionListener(e -> {
            cardLayout.next(cardPanel);
            generateBill();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(totalLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(new JLabel("Enter Payment:"), gbc);

        gbc.gridy++;
        panel.add(paymentField, gbc);

        gbc.gridy++;
        panel.add(calculateButton, gbc);

        gbc.gridy++;
        panel.add(balanceLabel, gbc);

        gbc.gridy++;
        panel.add(insufficientLabel, gbc);

        gbc.gridy++;
        panel.add(nextButton, gbc);

        cardPanel.add(panel, "Total");
    }

    private void createBillWindow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(34, 49, 63));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextArea billArea = new JTextArea(20, 40);
        billArea.setEditable(false);
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        billArea.setForeground(Color.WHITE);
        billArea.setBackground(new Color(34, 49, 63));
        JScrollPane scrollPane = new JScrollPane(billArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JButton printButton = new JButton("Print Bill");
        printButton.setPreferredSize(new Dimension(150, 30));
        printButton.addActionListener(e -> {
            try {
                billArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error printing bill: " + ex.getMessage());
            }
        });

        gbc.gridwidth = 2;
        panel.add(scrollPane, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(printButton, gbc);

        cardPanel.add(panel, "Bill");
    }

    private void updateTotalWindow() {
        JPanel panel = (JPanel) cardPanel.getComponent(4);
        JLabel totalLabel = (JLabel) panel.getComponent(0);
        totalLabel.setText(String.format("Total Amount: ₹%.2f", totalAmount));
    }

    private void generateBill() {
        JPanel panel = (JPanel) cardPanel.getComponent(5);
        JTextArea billArea = (JTextArea) ((JScrollPane) panel.getComponent(0)).getViewport().getView();

        StringBuilder bill = new StringBuilder();
        bill.append("********** Burger King **********\n");
        bill.append("   Customer Name: ").append(customerName).append("\n");
        bill.append("===================================\n");
        bill.append("            Order Details:\n");
        bill.append("-----------------------------------\n");
        for (String item : orderItems) {
            bill.append(item).append("\n");
        }
        bill.append("-----------------------------------\n");
        bill.append(String.format("Total Amount: ₹%.2f\n", totalAmount));
        bill.append("===================================\n");
        bill.append("       Thank you for choosing us!\n");
        bill.append("***********************************");

        billArea.setText(bill.toString());
    }

    private double calculateBurgerAmount(String burger, String size, ArrayList<String> addons) {
        double basePrice = 0;
        switch (burger) {
            case "Veg Whopper":
                basePrice = 129;
                break;
            case "Paneer Royale":
                basePrice = 169;
                break;
            case "Crispy Veg":
                basePrice = 70;
                break;
        }

        double sizeMultiplier = 1;
        switch (size) {
            case "Medium":
                sizeMultiplier = 1.2;
                break;
            case "Large":
                sizeMultiplier = 1.4;
                break;
        }

        double addonPrice = addons.size() * 20;
        return (basePrice * sizeMultiplier) + addonPrice;
    }

    private double calculateFriesAmount(String fries, String size) {
        double basePrice = 0;
        switch (fries) {
            case "Classic Fries":
                basePrice = 70;
                break;
            case "Cheesy Fries":
                basePrice = 100;
                break;
            case "Peri Peri Fries":
                basePrice = 90;
                break;
        }

        double sizeMultiplier = 1;
        switch (size) {
            case "Medium":
                sizeMultiplier = 1.3;
                break;
            case "Large":
                sizeMultiplier = 1.6;
                break;
        }

        return basePrice * sizeMultiplier;
    }

    private double calculateDrinkAmount(String drink, String size) {
        double basePrice = 0;
        switch (drink) {
            case "Cola":
                basePrice = 60;
                break;
            case "Lemonade":
                basePrice = 50;
                break;
            case "Iced Tea":
                basePrice = 55;
                break;
        }

        double sizeMultiplier = 1;
        switch (size) {
            case "Medium":
                sizeMultiplier = 1.2;
                break;
            case "Large":
                sizeMultiplier = 1.4;
                break;
        }

        return basePrice * sizeMultiplier;
    }

    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(new JLabel(label));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(component);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BurgerBillSystem system = new BurgerBillSystem();
            system.setVisible(true);
        });
    }
}

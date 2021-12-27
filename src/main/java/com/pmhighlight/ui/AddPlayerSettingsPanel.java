package com.pmhighlight.ui;

import com.pmhighlight.PmHighlightPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddPlayerSettingsPanel extends JPanel
{
    private final PmHighlightPlugin plugin;

    private final JLabel nameInputLabel = new JLabel();
    private final FlatTextField nameInputField = new FlatTextField();

    private final JLabel nameColorLabel = new JLabel();
    private final JLabel nameColorIndicator = new JLabel();
    private RuneliteColorPicker nameColorPicker;

    private final JLabel messageColorLabel = new JLabel();
    private final JLabel messageColorIndicator = new JLabel();
    private RuneliteColorPicker messageColorPicker;

    private final JLabel logColorLabel = new JLabel();
    private final JLabel logColorIndicator = new JLabel();
    private RuneliteColorPicker logColorPicker;

    public AddPlayerSettingsPanel(PmHighlightPlugin plugin)
    {
        this.plugin = plugin;

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(5, 0, 5, 0));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 5;
        constraints.gridx = 0;
        constraints.gridy = 0;

        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel("New player highlight");
        titlePanel.add(title);
        titlePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        titlePanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        add(titlePanel, constraints);
        constraints.gridy++;

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 8));

        inputPanel.setBorder(new EmptyBorder(0, 10, 10, 5));
        inputPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        nameInputLabel.setText("Player name:");
        nameInputField.setBackground(ColorScheme.DARK_GRAY_COLOR);
        inputPanel.add(nameInputLabel);
        inputPanel.add(nameInputField);

        // name color picker
        nameColorLabel.setText("Name color:");
        nameColorIndicator.setText(plugin.colorToHexString((plugin.getDefaultColor())));
        nameColorIndicator.setForeground(Color.WHITE);
        nameColorIndicator.setBackground(plugin.getDefaultColor());
        nameColorIndicator.setOpaque(true);

        nameColorIndicator.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openColorPicker(nameColorPicker, nameColorIndicator);
            }
        });

        inputPanel.add(nameColorLabel);
        inputPanel.add(nameColorIndicator);

        // Message color picker
        messageColorLabel.setText("Message color:");
        messageColorIndicator.setText(plugin.colorToHexString((plugin.getDefaultColor())));
        messageColorIndicator.setForeground(Color.WHITE);
        messageColorIndicator.setBackground(plugin.getDefaultColor());
        messageColorIndicator.setOpaque(true);

        messageColorIndicator.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openColorPicker(messageColorPicker, messageColorIndicator);
            }
        });

        inputPanel.add(messageColorLabel);
        inputPanel.add(messageColorIndicator);

        // Log in/out message color
        logColorLabel.setText("Log message color:");
        logColorIndicator.setText(plugin.colorToHexString((plugin.getDefaultColor())));
        logColorIndicator.setForeground(Color.WHITE);
        logColorIndicator.setBackground(plugin.getDefaultColor());
        logColorIndicator.setOpaque(true);

        logColorIndicator.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openColorPicker(logColorPicker, logColorIndicator);
            }
        });

        inputPanel.add(logColorLabel);
        inputPanel.add(logColorIndicator);

        add(inputPanel, constraints);
        constraints.gridy++;

        JPanel actionsPanel = createActionsPanel();
        add(actionsPanel, constraints);
    }

    private JPanel createActionsPanel()
    {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        buttonPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(l -> {
            plugin.setSettingsCreationPanelState(false);
        });

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(l -> {
            plugin.createPlayerSettings(nameInputField.getText(),
                                        nameColorIndicator.getText(),
                                        messageColorIndicator.getText(),
                                        logColorIndicator.getText());
            plugin.updateConfig();
            plugin.setSettingsCreationPanelState(false);
        });

        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        return buttonPanel;
    }

    public void openColorPicker(RuneliteColorPicker colorPicker, JLabel indicatorLabel)
    {
        colorPicker = plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this),
                Color.decode(indicatorLabel.getText()),
                "Choose a color",
                true
        );

        colorPicker.setLocation(getLocationOnScreen());

        colorPicker.setOnColorChange(selectedColor -> {
            indicatorLabel.setText(plugin.colorToHexString(selectedColor));
            indicatorLabel.setBackground(selectedColor);
        });

        colorPicker.setVisible(true);
    }

}

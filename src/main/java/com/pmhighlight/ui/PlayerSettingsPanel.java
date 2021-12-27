package com.pmhighlight.ui;

import com.pmhighlight.PlayerSettings;
import com.pmhighlight.PmHighlightPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlayerSettingsPanel extends JPanel
{

    private final PmHighlightPlugin plugin;
    private String playerName;
    private PlayerSettings settings;

    private boolean showDetails = false;
    private final JPanel detailsPanel;

    private final JLabel nameColorLabel = new JLabel();
    private final JLabel nameColorIndicator = new JLabel();
    private RuneliteColorPicker nameColorPicker;
    private JCheckBox nameHighlightSettingCheckbox;

    private final JLabel messageColorLabel = new JLabel();
    private final JLabel messageColorIndicator = new JLabel();
    private RuneliteColorPicker messageColorPicker;
    private JCheckBox messageHighlightSettingCheckbox;

    private final JLabel logColorLabel = new JLabel();
    private final JLabel logColorIndicator = new JLabel();
    private RuneliteColorPicker logColorPicker;
    private JCheckBox logHighlightSettingCheckbox;

    public PlayerSettingsPanel(PmHighlightPlugin plugin, String playerName, PlayerSettings settings)
    {
        this.plugin = plugin;
        this.playerName = playerName;
        this.settings = settings;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel view = new JPanel(new BorderLayout());
        view.setBorder(new EmptyBorder(2, 2, 2, 2));
        view.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
        JLabel nameLabel = new JLabel(playerName);

        titlePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showDetails = !showDetails;
                detailsPanel.setVisible(showDetails);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                titlePanel.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                titlePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
            }
        });

        titlePanel.add(nameLabel, BorderLayout.CENTER);
        detailsPanel = createDetailsJPanel(settings);
        detailsPanel.setVisible(false);

        view.add(titlePanel, BorderLayout.NORTH);
        view.add(detailsPanel, BorderLayout.CENTER);
        add(view);
    }

    private JPanel createDetailsJPanel(PlayerSettings settings)
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 5;
        constraints.gridx = 0;
        constraints.gridy = 0;

        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JLabel settingsTitle = new JLabel("Settings");
        settingsTitle.setBorder(new EmptyBorder(10, 5, 5, 0));
        panel.add(settingsTitle, constraints);
        constraints.gridy++;

        JPanel settingsPanel = createSettingsPanel();
        panel.add(settingsPanel, constraints);
        constraints.gridy++;

        JLabel colorTile = new JLabel("Colors");
        colorTile.setBorder(new EmptyBorder(10, 0, 5, 0));
        panel.add(colorTile, constraints);
        constraints.gridy++;

        JPanel colorPanel = createColorDetailsPanel();
        panel.add(colorPanel, constraints);
        constraints.gridy++;

        JPanel actionsPanel = createActionPanel();
        panel.add(actionsPanel, constraints);

        return panel;
    }

    private JPanel createSettingsPanel()
    {
        JPanel settingsPanel = new JPanel(new GridLayout(3, 1));
        settingsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        nameHighlightSettingCheckbox = new JCheckBox("Highlight name");
        nameHighlightSettingCheckbox.setSelected(settings.isNameHighlightEnabled());
        nameHighlightSettingCheckbox.addItemListener(event -> {
            int state = event.getStateChange();

            boolean enabled = state == 1;
            settings.setNameHighlightEnabled(enabled);
            plugin.updatePlayerSettings(playerName, settings);
            plugin.updateConfig();
        });

        messageHighlightSettingCheckbox = new JCheckBox("Highlight message");
        messageHighlightSettingCheckbox.setSelected(settings.isMessageHighlightEnabled());
        messageHighlightSettingCheckbox.addItemListener(event -> {
            int state = event.getStateChange();

            boolean enabled = state == 1;
            settings.setMessageHighlightEnabled(enabled);
            plugin.updatePlayerSettings(playerName, settings);
            plugin.updateConfig();
        });

        logHighlightSettingCheckbox = new JCheckBox("Highlight log in/out message");
        logHighlightSettingCheckbox.setSelected(settings.isLogHighlightEnabled());
        logHighlightSettingCheckbox.addItemListener(event -> {
            int state = event.getStateChange();

            boolean enabled = state == 1;
            settings.setLogHighlightEnabled(enabled);
            plugin.updatePlayerSettings(playerName, settings);
            plugin.updateConfig();
        });

        settingsPanel.add(nameHighlightSettingCheckbox);
        settingsPanel.add(messageHighlightSettingCheckbox);
        settingsPanel.add(logHighlightSettingCheckbox);

        return settingsPanel;
    }

    private JPanel createColorDetailsPanel()
    {
        JPanel colorPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        colorPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        nameColorLabel.setText("Name color:");
        nameColorIndicator.setText(settings.getNameColor());
        nameColorIndicator.setForeground(Color.WHITE);
        nameColorIndicator.setBackground(Color.decode(settings.getNameColor()));
        nameColorIndicator.setPreferredSize(new Dimension(2, 2));
        nameColorIndicator.setOpaque(true);

        nameColorIndicator.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openColorPicker(nameColorPicker, nameColorLabel, nameColorIndicator, "name");
            }
        });

        colorPanel.add(nameColorLabel);
        colorPanel.add(nameColorIndicator);

        // Message color picker
        messageColorLabel.setText("Message color:");
        messageColorIndicator.setText(settings.getMessageColor());
        messageColorIndicator.setForeground(Color.WHITE);
        messageColorIndicator.setBackground(Color.decode(settings.getMessageColor()));
        messageColorIndicator.setOpaque(true);

        messageColorIndicator.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openColorPicker(messageColorPicker, messageColorLabel, messageColorIndicator, "message");
            }
        });

        colorPanel.add(messageColorLabel);
        colorPanel.add(messageColorIndicator);

        // Log in/out message color
        logColorLabel.setText("Log message color:");
        logColorIndicator.setText(settings.getLogColor());
        logColorIndicator.setForeground(Color.WHITE);
        logColorIndicator.setBackground(Color.decode(settings.getLogColor()));
        logColorIndicator.setOpaque(true);

        logColorIndicator.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                openColorPicker(logColorPicker, logColorLabel, logColorIndicator, "log");
            }
        });

        colorPanel.add(logColorLabel);
        colorPanel.add(logColorIndicator);

        return colorPanel;
    }

    private JPanel createActionPanel()
    {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 8));
        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(l -> {
            int confirm = JOptionPane.showConfirmDialog(PlayerSettingsPanel.this,
                    "Are you sure you want to reset the settings for this player?",
                    "Warning", JOptionPane.OK_CANCEL_OPTION);

            if ( confirm == 0 ) {
                Color defaultColor = plugin.getDefaultColor();
                String defaultColorhex = plugin.colorToHexString(defaultColor);
                boolean nameEnabled = plugin.getDefaultNameHighlightSetting();
                boolean messageEnabled = plugin.getDefaultMessageHighlightSetting();
                boolean logEnabled = plugin.getDefaultLogNHighlightSetting();

                settings.setNameColor(defaultColorhex);
                nameColorIndicator.setText(defaultColorhex);
                nameColorIndicator.setBackground(defaultColor);
                settings.setNameHighlightEnabled(nameEnabled);
                nameHighlightSettingCheckbox.setSelected(nameEnabled);

                settings.setMessageColor(defaultColorhex);
                messageColorIndicator.setText(defaultColorhex);
                messageColorIndicator.setBackground(defaultColor);
                settings.setMessageHighlightEnabled(messageEnabled);
                messageHighlightSettingCheckbox.setSelected(messageEnabled);

                settings.setLogColor(defaultColorhex);
                logColorIndicator.setText(defaultColorhex);
                logColorIndicator.setBackground(defaultColor);
                settings.setLogHighlightEnabled(logEnabled);
                logHighlightSettingCheckbox.setSelected(logEnabled);

                plugin.updatePlayerSettings(playerName, settings);
                plugin.updateConfig();
            }
        });

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(l -> {
            int confirm = JOptionPane.showConfirmDialog(PlayerSettingsPanel.this,
                    "Are you sure you want to delete the settings for this player?",
                    "Warning", JOptionPane.OK_CANCEL_OPTION);

            if ( confirm == 0 ) {
                plugin.removePlayerSettings(playerName);
                plugin.updateConfig();
            }
        });

        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(resetButton);
        panel.add(removeButton);

        return panel;
    }

    public void openColorPicker(RuneliteColorPicker colorPicker, JLabel label, JLabel indicatorLabel, String setting)
    {
        colorPicker = plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this),
                Color.decode(indicatorLabel.getText()),
                "Pick a color",
                true
        );

        colorPicker.setLocation(getLocationOnScreen());

        colorPicker.setOnColorChange(selectedColor -> {
            String hex = plugin.colorToHexString(selectedColor);
            indicatorLabel.setText(hex);
            indicatorLabel.setBackground(selectedColor);

            switch(setting) {
                case "name":
                    settings.setNameColor(hex);
                    break;
                case "message":
                    settings.setMessageColor(hex);
                    break;
                case "log":
                    settings.setLogColor(hex);
                    break;
                default:
                    break;
            }

        });

        colorPicker.setOnClose(c -> {
            plugin.updatePlayerSettings(playerName, settings);
            plugin.updateConfig();
        });

        colorPicker.setVisible(true);
    }
}

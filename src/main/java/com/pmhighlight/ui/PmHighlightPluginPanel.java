package com.pmhighlight.ui;

import com.pmhighlight.PlayerSettings;
import com.pmhighlight.PmHighlightPlugin;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class PmHighlightPluginPanel extends PluginPanel
{
    private static final ImageIcon ICON_ADD;
    private final JLabel addPlayerSettings = new JLabel(ICON_ADD);
    private final JPanel configView = new JPanel(new GridBagLayout());
    private final PluginErrorPanel noPlayerSettingsPanel = new PluginErrorPanel();
    private final JLabel title = new JLabel();
    private final PmHighlightPlugin plugin;

    @Getter
    private AddPlayerSettingsPanel addPlayerSettingsPanel;

    static
    {
        final BufferedImage icon = ImageUtil.loadImageResource(PmHighlightPluginPanel.class, "/icon_add.png");
        ICON_ADD = new ImageIcon(icon);
    }

    public PmHighlightPluginPanel(PmHighlightPlugin plugin)
    {
        this.plugin = plugin;

        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());

        // North panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(1, 0, 10, 0));

        title.setText("PM Highlights");
        title.setForeground(Color.WHITE);
        northPanel.add(title, BorderLayout.WEST);

        addPlayerSettings.setForeground(new Color(110, 225, 110));
        addPlayerSettings.setToolTipText("Add player highlight");
        addPlayerSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showSettingCreationPanel(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e); //TODO: icon
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e); // TODO: icon
            }
        });
        northPanel.add(addPlayerSettings, BorderLayout.EAST);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        noPlayerSettingsPanel.setContent("PM Highlights", "No players configured");
        noPlayerSettingsPanel.setVisible(false);
        configView.add(noPlayerSettingsPanel, constraints);
        constraints.gridy++;

        addPlayerSettingsPanel = new AddPlayerSettingsPanel(plugin);
        addPlayerSettingsPanel.setVisible(false);
        configView.add(addPlayerSettingsPanel, constraints);
        constraints.gridy++;

        centerPanel.add(configView, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void rebuild()
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        configView.removeAll();
        int playerSettingsCount = 0;
        HashMap<String, PlayerSettings> settingsHashMap = plugin.getPlayerSettingsMap();
        for (String key : settingsHashMap.keySet()) {
            PlayerSettingsPanel playerSettingsPanel = new PlayerSettingsPanel(plugin, key, settingsHashMap.get(key));
            configView.add(playerSettingsPanel, constraints);
            constraints.gridy++;

            playerSettingsCount++;
        }

        noPlayerSettingsPanel.setVisible(playerSettingsCount == 0);
        title.setVisible(playerSettingsCount > 0);
        configView.add(noPlayerSettingsPanel, constraints);
        constraints.gridy++;

        configView.add(addPlayerSettingsPanel, constraints);
        constraints.gridy++;

        repaint();
        revalidate();
    }

    public void showSettingCreationPanel(boolean show)
    {
        if ( show ) {
            noPlayerSettingsPanel.setVisible(false);
        } else {
            rebuild();
        }

        addPlayerSettingsPanel.setVisible(show);
        addPlayerSettings.setVisible(!show);
    }

}

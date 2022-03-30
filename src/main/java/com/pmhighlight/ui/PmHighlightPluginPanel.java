package com.pmhighlight.ui;

import com.pmhighlight.PlayerSettings;
import com.pmhighlight.PmHighlightPlugin;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class PmHighlightPluginPanel extends PluginPanel
{
    private static final ImageIcon ICON_ADD;

    private final JPanel configView = new JPanel(new GridBagLayout());
    private final PluginErrorPanel noPlayerSettingsPanel = new PluginErrorPanel();
    private final JLabel title = new JLabel();
    private final PmHighlightPlugin plugin;

    private static final ImageIcon SECTION_EXPAND_ICON;
    private static final ImageIcon SECTION_EXPAND_ICON_HOVER;
    private static final ImageIcon SECTION_RETRACT_ICON;
    private static final ImageIcon SECTION_RETRACT_ICON_HOVER;

    private JPanel buttonPanel;

    static
    {
        BufferedImage sectionRetractIcon = ImageUtil.loadImageResource(PmHighlightPluginPanel.class, "/util/arrow_right.png");
        sectionRetractIcon = ImageUtil.luminanceOffset(sectionRetractIcon, -121);
        SECTION_EXPAND_ICON = new ImageIcon(sectionRetractIcon);
        SECTION_EXPAND_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(sectionRetractIcon, -100));
        final BufferedImage sectionExpandIcon = ImageUtil.rotateImage(sectionRetractIcon, Math.PI / 2);
        SECTION_RETRACT_ICON = new ImageIcon(sectionExpandIcon);
        SECTION_RETRACT_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(sectionExpandIcon, -100));
    }


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

        buttonPanel = buildButtonPanel();
        northPanel.add(buttonPanel, BorderLayout.EAST);

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

    /**
     * Build the button panel containing the player and group settings buttons.
     * @return
     */
    public JPanel buildButtonPanel()
    {
        JPanel buttonPanel = new JPanel();
        JLabel addGroupSettings = new JLabel(ICON_ADD);
        addGroupSettings.setToolTipText("Add group highlight");
        addGroupSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showSettingCreationPanel(true);
            }
        });
        buttonPanel.add(addGroupSettings, BorderLayout.CENTER);

        JLabel addPlayerSettings = new JLabel(ICON_ADD);
        addPlayerSettings.setToolTipText("Add player highlight");
        addPlayerSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showSettingCreationPanel(true);
            }
        });

        buttonPanel.add(addPlayerSettings, BorderLayout.EAST);

        return buttonPanel;
    }

    public void rebuild()
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        configView.removeAll();

        // TODO: create methods / vieews to add group settings
        configView.add(addPlayerSettingsPanel, constraints);
        constraints.gridy++;

        HashMap<String, PlayerSettings> settingsHashMap = plugin.getPlayerSettingsMap();
        final JPanel playerSettingsSectionContent = buildPlayerSettingsSectionContent(settingsHashMap);
        JPanel playerSettingsSection = buildSection("Player settings", playerSettingsSectionContent);
        configView.add(playerSettingsSection, constraints);

        constraints.gridy++;

        // TODO: implement group settings
        final JPanel groupSettingsSectionContent = new JPanel();
        JPanel groupSettingsSection = buildSection("Group settings", groupSettingsSectionContent);
        configView.add(groupSettingsSection, constraints);

        repaint();
        revalidate();
    }

    /**
     * Build a settings section panel
     * @param name Name of the section panel
     * @param content Panel to wrap in the section
     *
     * @return JPanel section
     */
    public JPanel buildSection(String name, JPanel content)
    {
        JPanel sectionWrapper = new JPanel(new BorderLayout());

        JPanel sectionHeader = new JPanel(new BorderLayout());
        JButton sectionToggle = new JButton(SECTION_RETRACT_ICON);
        sectionToggle.setPreferredSize(new Dimension(18, 0));
        sectionToggle.setBorder(new EmptyBorder(0, 0, 0, 5));
        sectionToggle.setToolTipText("Retract");
        SwingUtil.removeButtonDecorations(sectionToggle);

        JLabel sectionName = new JLabel(name);

        sectionName.setForeground(ColorScheme.BRAND_ORANGE);
        CompoundBorder sectionHeaderBorder = new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, ColorScheme.MEDIUM_GRAY_COLOR),
                new EmptyBorder(BORDER_OFFSET, 0, BORDER_OFFSET, 0));

        sectionHeader.add(sectionToggle, BorderLayout.WEST);
        sectionHeader.add(sectionName, BorderLayout.CENTER);
        sectionHeader.setBorder(sectionHeaderBorder);

        sectionWrapper.add(sectionHeader, BorderLayout.NORTH);
        sectionWrapper.add(content, BorderLayout.CENTER);

        // Make the button, section and name clickable
        final MouseAdapter playerSectionMouseAdapter = new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                toggleSection(sectionToggle, content);
            }
        };
        sectionToggle.addActionListener(actionEvent -> toggleSection(sectionToggle, content));
        sectionName.addMouseListener(playerSectionMouseAdapter);
        sectionHeader.addMouseListener(playerSectionMouseAdapter);

        return sectionWrapper;
    }

    public JPanel buildPlayerSettingsSectionContent(HashMap<String, PlayerSettings> playerSettingsHashMap)
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        for (String key : playerSettingsHashMap.keySet()) {
            PlayerSettingsPanel playerSettingsPanel = new PlayerSettingsPanel(plugin, key, playerSettingsHashMap.get(key));
            panel.add(playerSettingsPanel, constraints);
            constraints.gridy++;
        }

        return panel;
    }

    private void toggleSection(JButton button, JPanel sectionContents)
    {
        boolean newState = !sectionContents.isVisible();
        sectionContents.setVisible(newState);
        button.setIcon(newState ? SECTION_RETRACT_ICON : SECTION_EXPAND_ICON);
        button.setRolloverIcon(newState ? SECTION_RETRACT_ICON_HOVER : SECTION_EXPAND_ICON_HOVER);
        button.setToolTipText(newState ? "Retract" : "Expand");
        SwingUtilities.invokeLater(sectionContents::revalidate);
    }

    public void showSettingCreationPanel(boolean show)
    {
        if ( show ) {
            noPlayerSettingsPanel.setVisible(false);
        } else {
            rebuild();
        }

        addPlayerSettingsPanel.setVisible(show);
        buttonPanel.setVisible(!show);
    }

}

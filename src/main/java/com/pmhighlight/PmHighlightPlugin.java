package com.pmhighlight;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import com.pmhighlight.ui.PmHighlightPluginPanel;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = "PM Highlight",
        description = "Highlight private messages in different colors for certain players",
        tags = {"highlight", "color", "private", "chat", "friends"}
)
public class PmHighlightPlugin extends Plugin
{
    @Inject
    protected ClientToolbar clientToolbar;

    protected NavigationButton navigationButton;
    protected PmHighlightPluginPanel pluginPanel;

    @Inject
    private PmHighlightConfig config;

    @Inject
    private ConfigManager configManager;

    @Getter
    private final HashMap<String, PlayerSettings> playerSettingsMap = new HashMap();

    @Getter
    @Inject
    private ColorPickerManager colorPickerManager;

    private static final String CONFIG_GROUP_NAME = "pmhighlight";
    private static final String CONFIG_KEY = "playersettings";
    private static final String LOG_REGEX = "(?<name>.+)\\shas\\slogged\\s(?<method>in|out).";
    private static final String NAME_REGEX = "(?<icon>\\<img=\\d+>)?(?<name>.*)";
    private Pattern logPattern;
    private Pattern namePattern;

    @Provides
    PmHighlightConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PmHighlightConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        logPattern = Pattern.compile(LOG_REGEX);
        namePattern = Pattern.compile(NAME_REGEX);
        pluginPanel = new PmHighlightPluginPanel(this);
        String configJson = configManager.getConfiguration(CONFIG_GROUP_NAME, CONFIG_KEY);
        loadConfig(configJson);

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon_marker.png");

        pluginPanel.rebuild();
        navigationButton = NavigationButton.builder()
                                            .tooltip("PM Highlights")
                                            .icon(icon)
                                            .priority(5)
                                            .panel(pluginPanel)
                                            .build();

        clientToolbar.addNavigation(navigationButton);

    }

    @Override
    protected void shutDown()
    {
        updateConfig();
        clientToolbar.removeNavigation(navigationButton);
        playerSettingsMap.clear();
        pluginPanel = null;
        navigationButton = null;
    }

    @Subscribe(priority = 1)
    public void onChatMessage(ChatMessage message)
    {
        ChatMessageType type = message.getType();

        /*
         * Update log in/out message.
         */
        if ( type == ChatMessageType.LOGINLOGOUTNOTIFICATION ) {
            MessageNode messageNode = message.getMessageNode();
            Matcher logMatcher = logPattern.matcher(messageNode.getValue());

            if ( logMatcher.find() ) {
                if ( logMatcher.group("name") != null ) {
                    String playerName = logMatcher.group("name");
                    playerName = Text.toJagexName(playerName);

                    if ( playerSettingsMap.containsKey(playerName) ) {
                        PlayerSettings settings = playerSettingsMap.get(playerName);

                        if ( settings.isLogHighlightEnabled() ) {
                            Color color = Color.decode(settings.getLogColor());
                            messageNode.setValue(wrapWithColorTags(messageNode.getValue(), color));
                        }
                    }
                }
            }
        }

        /*
         * Check whether the message is either a private chat message, allow messages with the following types to
         * be coloured:
         *  - PRIVATECHAT: incomming private messages
         *  - PRIVATECHATOUT: outgoing private messages
         *  - FRIENDSCHAT: friends chat messages TODO: check if this is both incomming and outgoing
         *  - CLAN_CHAT: clan chat messages TODO: check if this is both incomming and outgoing
         *
         * TODO: add options to enable / disable friends and clan chat highlighting
         */
        if ( type == ChatMessageType.PRIVATECHAT ||
             type == ChatMessageType.PRIVATECHATOUT ||
             type == ChatMessageType.FRIENDSCHAT ||
             type == ChatMessageType.CLAN_CHAT ) {

            MessageNode messageNode = message.getMessageNode();

            String messageName = Text.toJagexName(messageNode.getName());
            Matcher nameMatcher = namePattern.matcher(messageName);

            String icon = "";
            String playerName = "";

            if (nameMatcher.find() ) {
                if ( nameMatcher.group("icon") != null ) {
                    icon = nameMatcher.group("icon");
                }

                if ( nameMatcher.group("name") != null ) {
                    playerName = nameMatcher.group("name");
                }

                // TODO: check for group settings
                if ( playerSettingsMap.containsKey(playerName)) {
                    PlayerSettings settings = playerSettingsMap.get(playerName);

                    if ( settings.isNameHighlightEnabled() ) {
                        Color nameColor = Color.decode(settings.getNameColor());
                        String coloredName = wrapWithColorTags(playerName, nameColor);

                        // Prepend name with the icon if it was set
                        if (!icon.isEmpty()) {
                            coloredName = icon + coloredName;
                        }
                        messageNode.setName(wrapWithColorTags(coloredName, nameColor));
                    }

                    if ( settings.isMessageHighlightEnabled() ) {
                        Color messageColor = Color.decode(settings.getMessageColor());
                        messageNode.setValue(wrapWithColorTags(messageNode.getValue(), messageColor));
                    }
                }
            }
        }

        /*
         * TODO: investigate if notifications in the clan/friends chat can be coloroued and
         *  what type they are.
         */
    }

    /**
     * Wrap a string with color tags.
     * @param text The text to wrap
     * @param color Color to wrap the text with
     * @return String
     */
    private String wrapWithColorTags(String text, Color color)
    {
        return ColorUtil.wrapWithColorTag(
                text.replace(ColorUtil.CLOSING_COLOR_TAG, ColorUtil.colorTag(color)),
                color
        );
    }

    /**
     * Get the default color
     * @return Color defaultColor
     */
    public Color getDefaultColor()
    {
        return config.defaultColor();
    }

    /**
     * Get the default value for weather the name should be highlighted
     * @return boolean
     */
    public boolean getDefaultNameHighlightSetting()
    {
        return config.highlightUsernameDefault();
    }

    /**
     * Get the default value for weather the message should be highlighted
     * @return boolean
     */
    public boolean getDefaultMessageHighlightSetting()
    {
        return config.highlightMessageDefault();
    }

    /**
     * Get the default value for weather the log in/out message should be highlighted
     * @return boolean
     */
    public boolean getDefaultLogNHighlightSetting()
    {
        return config.highlightLoggedInOutDefault();
    }

    /**
     * Convert any Color to it's equivalent RGB HEX string.
     * @param color color
     * @return String RGB HEX string
     */
    public String colorToHexString(Color color)
    {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChangedEvent)
    {
        if ( configChangedEvent.getGroup().equals(CONFIG_GROUP_NAME) && configChangedEvent.getKey().equals(CONFIG_KEY) ) {
            loadConfig(configChangedEvent.getNewValue());
        }
    }

    /**
     * Helper to enable/disable the new user creation panel.
     * @param show show Whether or not to show the panel.
     */
    public void setSettingsCreationPanelState(boolean show)
    {
        pluginPanel.showSettingCreationPanel(show);
    }

    /**
     * Update the config with player settings, this converts the map from memory into a json string.
     */
    public void updateConfig()
    {
        if (playerSettingsMap.isEmpty()) {
            configManager.unsetConfiguration(CONFIG_GROUP_NAME, CONFIG_KEY);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(playerSettingsMap);
            configManager.setConfiguration(CONFIG_GROUP_NAME, CONFIG_KEY, json);
        }
    }

    /**
     * Load the player settings map from a json string
     * @param json JSON stringified map that contains player settings.
     */
    private void loadConfig(String json)
    {
        playerSettingsMap.clear();

        Gson gson = new Gson();
        Map<String, PlayerSettings> list = gson.fromJson(json, new TypeToken<HashMap<String, PlayerSettings>>() {}.getType());

        if ( list != null ) {
            for (String key : list.keySet()) {
                playerSettingsMap.put(key, list.get(key));
            }
        }

    }

    /**
     * Create a new PlayerSetting object and add it tot he Map
     * @param name Player name
     * @param nameColor Color to highlight player name with
     * @param messageColor Color to highlight message with
     * @param logColor Color to highlight log in/out message with
     */
    public void createPlayerSettings(String name, String nameColor, String messageColor, String logColor)
    {
        PlayerSettings settings = new PlayerSettings();
        settings.setNameColor(nameColor);
        settings.setMessageColor(messageColor);
        settings.setLogColor(logColor);
        settings.setNameHighlightEnabled(getDefaultNameHighlightSetting());
        settings.setMessageHighlightEnabled(getDefaultMessageHighlightSetting());
        settings.setLogHighlightEnabled(getDefaultLogNHighlightSetting());

        playerSettingsMap.put(name, settings);
        pluginPanel.rebuild();
    }

    /**
     * Replacea a player's settings
     * @param name Name of the player
     * @param settings PlayerSettings object to set
     */
    public void updatePlayerSettings(String name, PlayerSettings settings)
    {
        playerSettingsMap.put(name, settings);
    }

    /**
     * Remove a player's settings by player name
     * @param name Player name
     */
    public void removePlayerSettings(String name)
    {
        if ( playerSettingsMap.containsKey(name)) {
            playerSettingsMap.remove(name);
            pluginPanel.rebuild();
        }
    }
}

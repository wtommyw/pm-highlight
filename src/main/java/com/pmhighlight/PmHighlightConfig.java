package com.pmhighlight;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("pmhighlight")
public interface PmHighlightConfig extends Config
{
    @ConfigItem(
            keyName = "highlightUsername",
            name = "Highlight username default",
            description = "Whether or not to highlight the player name by default",
            position = 14
    )
    default boolean highlightUsernameDefault() { return true; }

    @ConfigItem(
            keyName = "highlightMessage",
            name = "Highlight message default",
            description = "Whether or not to highlight the message by default",
            position = 14
    )
    default boolean highlightMessageDefault() { return true; }

    @ConfigItem(
            keyName = "highlightLog",
            name = "Highlight log message default",
            description = "Whether or not to highlight the log in/out message by default",
            position = 14
    )
    default boolean highlightLoggedInOutDefault() { return true; }

    @ConfigItem(
            keyName = "color",
            name = "Default color",
            description = "Default color to use for highlights",
            position = 14
    )
    default Color defaultColor() { return Color.GREEN; }

    @ConfigItem(
            keyName = "notifyOnLogin",
            name = "Notify on log in",
            description = "Default notification setting",
            position = 14
    )
    default boolean notifyOnLogin() {
        return false;
    }
}

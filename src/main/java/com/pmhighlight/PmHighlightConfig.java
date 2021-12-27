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
            description = "",
            position = 14
    )
    default boolean highlightUsernameDefault() { return true; }

    @ConfigItem(
            keyName = "highlightMessage",
            name = "Highlight message default",
            description = "",
            position = 14
    )
    default boolean highlightMessageDefault() { return true; }

    @ConfigItem(
            keyName = "highlightLog",
            name = "Highlight log message default",
            description = "",
            position = 14
    )
    default boolean highlightLoggedInOutDefault() { return true; }

    @ConfigItem(
            keyName = "color",
            name = "Default color",
            description = "Set the default highlight color",
            position = 14
    )
    default Color defaultColor() { return Color.ORANGE; }
}

package com.pmhighlight;

import lombok.*;

import java.awt.Color;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSettings
{
//    private String playerName;
    private String nameColor;
    private String messageColor;
    private String logColor;
    private boolean nameHighlightEnabled;
    private boolean messageHighlightEnabled;
    private boolean logHighlightEnabled;
}

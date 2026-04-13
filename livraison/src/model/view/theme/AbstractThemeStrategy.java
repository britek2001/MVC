package mvc.model.view.theme;

import java.awt.Color;

public abstract class AbstractThemeStrategy implements ThemeStrategy {

    private final String name;
    private final Color gameAreaBackgroundColor;
    private final Color infoPanelBackgroundColor;
    private final Color separatorColor;
    private final Color hudCardBackgroundColor;
    private final Color hudCardBorderColor;
    private final Color hudTextColor;
    private final Color hudMutedTextColor;
    private final Color buttonPrimaryBackground;
    private final Color buttonSecondaryBackground;
    private final Color buttonSecondaryForeground;
    private final Color buttonBorderColor;

    protected AbstractThemeStrategy(
            String name,
            Color gameAreaBackgroundColor,
            Color infoPanelBackgroundColor,
            Color separatorColor,
            Color hudCardBackgroundColor,
            Color hudCardBorderColor,
            Color hudTextColor,
            Color hudMutedTextColor,
            Color buttonPrimaryBackground,
            Color buttonSecondaryBackground,
            Color buttonSecondaryForeground,
            Color buttonBorderColor) {
        this.name = name;
        this.gameAreaBackgroundColor = gameAreaBackgroundColor;
        this.infoPanelBackgroundColor = infoPanelBackgroundColor;
        this.separatorColor = separatorColor;
        this.hudCardBackgroundColor = hudCardBackgroundColor;
        this.hudCardBorderColor = hudCardBorderColor;
        this.hudTextColor = hudTextColor;
        this.hudMutedTextColor = hudMutedTextColor;
        this.buttonPrimaryBackground = buttonPrimaryBackground;
        this.buttonSecondaryBackground = buttonSecondaryBackground;
        this.buttonSecondaryForeground = buttonSecondaryForeground;
        this.buttonBorderColor = buttonBorderColor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Color getGameAreaBackgroundColor() {
        return gameAreaBackgroundColor;
    }

    @Override
    public Color getInfoPanelBackgroundColor() {
        return infoPanelBackgroundColor;
    }

    @Override
    public Color getSeparatorColor() {
        return separatorColor;
    }

    @Override
    public Color getHudCardBackgroundColor() {
        return hudCardBackgroundColor;
    }

    @Override
    public Color getHudCardBorderColor() {
        return hudCardBorderColor;
    }

    @Override
    public Color getHudTextColor() {
        return hudTextColor;
    }

    @Override
    public Color getHudMutedTextColor() {
        return hudMutedTextColor;
    }

    @Override
    public Color getButtonPrimaryBackground() {
        return buttonPrimaryBackground;
    }

    @Override
    public Color getButtonSecondaryBackground() {
        return buttonSecondaryBackground;
    }

    @Override
    public Color getButtonSecondaryForeground() {
        return buttonSecondaryForeground;
    }

    @Override
    public Color getButtonBorderColor() {
        return buttonBorderColor;
    }
}
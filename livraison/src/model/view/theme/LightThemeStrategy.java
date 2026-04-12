package mvc.model.view.theme;

import java.awt.Color;
import java.awt.Font;

public class LightThemeStrategy implements ThemeStrategy {

    @Override
    public String getName() {
        return "Light";
    }

    @Override
    public Color getGameAreaBackgroundColor() {
        return new Color(248, 250, 252);
    }

    @Override
    public Color getInfoPanelBackgroundColor() {
        return new Color(241, 245, 249);
    }

    @Override
    public Color getSeparatorColor() {
        return new Color(203, 213, 225);
    }

    @Override
    public Color getHudCardBackgroundColor() {
        return new Color(255, 255, 255, 235);
    }

    @Override
    public Color getHudCardBorderColor() {
        return new Color(203, 213, 225);
    }

    @Override
    public Color getHudTextColor() {
        return new Color(15, 23, 42);
    }

    @Override
    public Color getHudMutedTextColor() {
        return new Color(71, 85, 105);
    }

    @Override
    public Color getButtonPrimaryBackground() {
        return new Color(37, 99, 235);
    }

    @Override
    public Color getButtonPrimaryForeground() {
        return Color.WHITE;
    }

    @Override
    public Color getButtonSecondaryBackground() {
        return new Color(226, 232, 240);
    }

    @Override
    public Color getButtonSecondaryForeground() {
        return new Color(15, 23, 42);
    }

    @Override
    public Color getButtonBorderColor() {
        return new Color(148, 163, 184);
    }

    @Override
    public Font getButtonFont() {
        return new Font("SansSerif", Font.BOLD, 13);
    }

    @Override
    public int getControlArc() {
        return 14;
    }
}

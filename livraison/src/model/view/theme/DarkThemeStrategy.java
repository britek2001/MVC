package mvc.model.view.theme;

import java.awt.Color;
import java.awt.Font;

public class DarkThemeStrategy implements ThemeStrategy {

    @Override
    public String getName() {
        return "Dark";
    }

    @Override
    public Color getGameAreaBackgroundColor() {
        return new Color(15, 23, 42);
    }

    @Override
    public Color getInfoPanelBackgroundColor() {
        return new Color(30, 41, 59);
    }

    @Override
    public Color getSeparatorColor() {
        return new Color(71, 85, 105);
    }

    @Override
    public Color getHudCardBackgroundColor() {
        return new Color(15, 23, 42, 230);
    }

    @Override
    public Color getHudCardBorderColor() {
        return new Color(71, 85, 105);
    }

    @Override
    public Color getHudTextColor() {
        return new Color(241, 245, 249);
    }

    @Override
    public Color getHudMutedTextColor() {
        return new Color(203, 213, 225);
    }

    @Override
    public Color getButtonPrimaryBackground() {
        return new Color(59, 130, 246);
    }

    @Override
    public Color getButtonPrimaryForeground() {
        return Color.WHITE;
    }

    @Override
    public Color getButtonSecondaryBackground() {
        return new Color(51, 65, 85);
    }

    @Override
    public Color getButtonSecondaryForeground() {
        return new Color(241, 245, 249);
    }

    @Override
    public Color getButtonBorderColor() {
        return new Color(100, 116, 139);
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

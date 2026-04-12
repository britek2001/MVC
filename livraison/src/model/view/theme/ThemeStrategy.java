package mvc.model.view.theme;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;

public interface ThemeStrategy {
    String getName();

    Color getGameAreaBackgroundColor();
    Color getInfoPanelBackgroundColor();
    Color getSeparatorColor();

    Color getHudCardBackgroundColor();
    Color getHudCardBorderColor();
    Color getHudTextColor();
    Color getHudMutedTextColor();

    Color getButtonPrimaryBackground();
    Color getButtonPrimaryForeground();
    Color getButtonSecondaryBackground();
    Color getButtonSecondaryForeground();
    Color getButtonBorderColor();

    Font getButtonFont();
    int getControlArc();

    default void stylePrimaryButton(JButton button) {
        styleButton(button, true);
    }

    default void styleSecondaryButton(JButton button) {
        styleButton(button, false);
    }

    private void styleButton(JButton button, boolean primary) {
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFont(getButtonFont());
        button.setBackground(primary ? getButtonPrimaryBackground() : getButtonSecondaryBackground());
        button.setForeground(primary ? getButtonPrimaryForeground() : getButtonSecondaryForeground());
        button.setBorder(new javax.swing.border.LineBorder(getButtonBorderColor(), 1, true));
    }
}

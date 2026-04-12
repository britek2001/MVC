package mvc.model.view.theme;

import javax.swing.JButton;

public class StyledButtonFactory {
    private final ThemeStrategy theme;

    public StyledButtonFactory(ThemeStrategy theme) {
        this.theme = theme;
    }

    public JButton createPrimaryButton(String text, Runnable onClick) {
        JButton button = new JButton(text);
        theme.stylePrimaryButton(button);
        if (onClick != null) {
            button.addActionListener(e -> onClick.run());
        }
        return button;
    }

    public JButton createSecondaryButton(String text, Runnable onClick) {
        JButton button = new JButton(text);
        theme.styleSecondaryButton(button);
        if (onClick != null) {
            button.addActionListener(e -> onClick.run());
        }
        return button;
    }
}

package mvc.model.view.theme;

import java.awt.Font;
import javax.swing.JButton;

public class StyledButtonFactory {
    private final ThemeStrategy theme;
    
    public StyledButtonFactory(ThemeStrategy theme) {
        this.theme = theme;
    }

    public JButton createPrimaryButton(String text, Runnable onClick) {
        JButton button = new JButton(text);
        applyButtonStyle(button);
        if (onClick != null) {
            button.addActionListener(e -> onClick.run());
        }
        return button;
    }


    private void applyButtonStyle(JButton button) {
        if (theme != null) {
            theme.stylePrimaryButton(button);
            return;
        }
        button.setFont(new Font("Monospaced", Font.BOLD, 11));
        button.setFocusPainted(false);
    }
}

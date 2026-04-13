package mvc.model.view.theme;

import java.awt.Color;

public class DarkThemeStrategy extends AbstractThemeStrategy {

    public DarkThemeStrategy() {
        super(
                "Dark",
                new Color(15, 23, 42),
                new Color(30, 41, 59),
                new Color(71, 85, 105),
                new Color(15, 23, 42, 230),
                new Color(71, 85, 105),
                new Color(241, 245, 249),
                new Color(203, 213, 225),
                new Color(59, 130, 246),
                new Color(51, 65, 85),
                new Color(241, 245, 249),
                new Color(100, 116, 139));
    }
}

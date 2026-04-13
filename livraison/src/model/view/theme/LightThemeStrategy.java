package mvc.model.view.theme;

import java.awt.Color;

public class LightThemeStrategy extends AbstractThemeStrategy {

    public LightThemeStrategy() {
        super(
                "Light",
                new Color(248, 250, 252),
                new Color(241, 245, 249),
                new Color(203, 213, 225),
                new Color(255, 255, 255, 235),
                new Color(203, 213, 225),
                new Color(15, 23, 42),
                new Color(71, 85, 105),
                new Color(37, 99, 235),
                new Color(226, 232, 240),
                new Color(15, 23, 42),
                new Color(148, 163, 184));
    }
}

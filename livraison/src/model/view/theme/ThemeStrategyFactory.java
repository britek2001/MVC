package mvc.model.view.theme;

public final class ThemeStrategyFactory {

    public static final String THEME_LIGHT = "Light";
    public static final String THEME_DARK = "Dark";

    private ThemeStrategyFactory() {
    }

    public static ThemeStrategy fromName(String name) {
        if (THEME_DARK.equalsIgnoreCase(name)) {
            return new DarkThemeStrategy();
        }
        return new LightThemeStrategy();
    }
}

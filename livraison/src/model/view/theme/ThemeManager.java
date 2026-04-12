package mvc.model.view.theme;

public final class ThemeManager {
    private static ThemeStrategy currentTheme = new LightThemeStrategy();

    private ThemeManager() {
    }

    public static ThemeStrategy getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(ThemeStrategy theme) {
        if (theme != null) {
            currentTheme = theme;
        }
    }
}

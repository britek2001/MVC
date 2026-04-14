package mvc.model.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridLayout;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.JDialog;
import mvc.model.view.theme.StyledButtonFactory;
import mvc.model.view.theme.ThemeManager;
import mvc.model.view.theme.ThemeStrategyFactory;

public class MainMenuView {

    private static final Color RETRO_PURPLE_BG = new Color(56, 14, 92);
    private static final Color RETRO_PURPLE_PANEL = new Color(76, 25, 120);
    private static final Color RETRO_NEON_PINK = new Color(255, 88, 190);
    private static final Color RETRO_TEXT = new Color(245, 232, 255);
    private static final Color RETRO_INPUT_BG = new Color(100, 45, 150);
    private static final String FONT_FAMILY_MONOSPACED = "Monospaced";
    private static final Font RETRO_TITLE_FONT = new Font(FONT_FAMILY_MONOSPACED, Font.BOLD, 22);
    private static final Font RETRO_LABEL_FONT = new Font(FONT_FAMILY_MONOSPACED, Font.BOLD, 13);

    public static final String STRATEGY_RANDOM = "Random Generation";
    public static final String STRATEGY_AI = "AI Player";
    public static final String STRATEGY_TWO_PLAYERS = "Two Players";

    public record MenuSelection(
            int level,
            String difficultyLabel,
            String strategyLabel,
            boolean isTwoPlayers,
            String redPlayerName,
            String bluePlayerName) {
    }

    public void show(Consumer<MenuSelection> onStart) {
        Objects.requireNonNull(onStart, "onStart must not be null");

        SwingUtilities.invokeLater(() -> {
            JFrame menuFrame = new JFrame("ASI GAME");
            menuFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            menuFrame.setSize(460, 340);
            menuFrame.setLocationRelativeTo(null);
            menuFrame.getContentPane().setBackground(RETRO_PURPLE_BG);

            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RETRO_NEON_PINK, 2),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
            panel.setLayout(new GridBagLayout());
            panel.setBackground(RETRO_PURPLE_PANEL);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0, 0, 10, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel titleLabel = new JLabel("MAX GAME - MENU");
            titleLabel.setFont(RETRO_TITLE_FONT);
            titleLabel.setForeground(RETRO_NEON_PINK);
            panel.add(titleLabel, gbc);

            JLabel strategyLabel = new JLabel("Selection de strategie:");
            String[] strategies = {STRATEGY_RANDOM, STRATEGY_AI, STRATEGY_TWO_PLAYERS};
            JComboBox<String> strategyComboBox = new JComboBox<>(strategies);

            JLabel difficultyLabel = new JLabel("Selection de Difficulte:");
            String[] difficulties = {"Facile", "Moyen", "Difficile", "Tres difficile", "Extreme"};
            JComboBox<String> difficultyComboBox = new JComboBox<>(difficulties);

            JLabel themeLabel = new JLabel("Theme:");
            styleRetroLabel(themeLabel);

            JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            themePanel.setBackground(RETRO_PURPLE_PANEL);
            themePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            ButtonGroup themeGroup = new ButtonGroup();
            JRadioButton lightButton = new JRadioButton(ThemeStrategyFactory.THEME_LIGHT, true);
            JRadioButton darkButton = new JRadioButton(ThemeStrategyFactory.THEME_DARK);

            styleRetroRadioButton(lightButton);
            styleRetroRadioButton(darkButton);

            themeGroup.add(lightButton);
            themeGroup.add(darkButton);

            themePanel.add(lightButton);
            themePanel.add(darkButton);

            styleRetroLabel(strategyLabel);
            styleRetroLabel(difficultyLabel);
            styleRetroCombo(strategyComboBox);
            styleRetroCombo(difficultyComboBox);

            StyledButtonFactory buttonFactory = new StyledButtonFactory(ThemeManager.getCurrentTheme());
            JButton startButton = buttonFactory.createPrimaryButton(" Commencer le jeu ", () -> {
                String selectedStrategy = (String) strategyComboBox.getSelectedItem();
                String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
                String selectedTheme = lightButton.isSelected() ? ThemeStrategyFactory.THEME_LIGHT : ThemeStrategyFactory.THEME_DARK;
                ThemeManager.setCurrentTheme(ThemeStrategyFactory.fromName(selectedTheme));
                int level = mapDifficultyToLevel(selectedDifficulty);

                if (STRATEGY_TWO_PLAYERS.equals(selectedStrategy)) {
                    MenuSelection selection = askTwoPlayerNames(menuFrame, level, selectedDifficulty);
                    if (selection == null) {
                        return;
                    }

                    menuFrame.dispose();
                    onStart.accept(selection);
                    return;
                }

                menuFrame.dispose();
                onStart.accept(new MenuSelection(level, selectedDifficulty, selectedStrategy, false, "", ""));
            });
            startButton.setFont(new Font(FONT_FAMILY_MONOSPACED, Font.BOLD, 11));

            gbc.gridy++;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(2, 0, 2, 8);
            panel.add(strategyLabel, gbc);

            gbc.gridx = 1;
            gbc.insets = new Insets(2, 0, 2, 0);
            panel.add(strategyComboBox, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.insets = new Insets(2, 0, 2, 8);
            panel.add(difficultyLabel, gbc);

            gbc.gridx = 1;
            gbc.insets = new Insets(2, 0, 2, 0);
            panel.add(difficultyComboBox, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.insets = new Insets(2, 0, 2, 8);
            panel.add(themeLabel, gbc);

            gbc.gridx = 1;
            gbc.insets = new Insets(2, 0, 2, 0);
            gbc.anchor = GridBagConstraints.WEST;
            panel.add(themePanel, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(14, 0, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(startButton, gbc);

            menuFrame.add(panel);
            menuFrame.setVisible(true);
        });
    }

    private void styleRetroLabel(JLabel label) {
        label.setForeground(RETRO_TEXT);
        label.setFont(RETRO_LABEL_FONT);
    }

    private void styleRetroCombo(JComboBox<String> comboBox) {
        comboBox.setBackground(RETRO_INPUT_BG);
        comboBox.setForeground(Color.WHITE);
        comboBox.setFont(RETRO_LABEL_FONT);
        comboBox.setBorder(BorderFactory.createLineBorder(RETRO_NEON_PINK, 1));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                label.setFont(RETRO_LABEL_FONT);

                if (isSelected) {
                    label.setBackground(RETRO_NEON_PINK);
                    label.setForeground(new Color(38, 7, 64));
                } else {
                    label.setBackground(RETRO_INPUT_BG);
                    label.setForeground(RETRO_TEXT);
                }
                return label;
            }
        });
    }

    private void styleRetroRadioButton(JRadioButton button) {
        button.setBackground(RETRO_PURPLE_PANEL);
        button.setForeground(RETRO_TEXT);
        button.setFont(RETRO_LABEL_FONT);
        button.setFocusPainted(false);
    }

    private MenuSelection askTwoPlayerNames(JFrame parentFrame, int level, String difficultyLabel) {
        JTextField redPlayerField = new JTextField(15);
        JTextField bluePlayerField = new JTextField(15);

        redPlayerField.setBackground(RETRO_INPUT_BG);
        redPlayerField.setForeground(RETRO_TEXT);
        redPlayerField.setCaretColor(RETRO_TEXT);
        redPlayerField.setBorder(BorderFactory.createLineBorder(RETRO_NEON_PINK, 1));

        bluePlayerField.setBackground(RETRO_INPUT_BG);
        bluePlayerField.setForeground(RETRO_TEXT);
        bluePlayerField.setCaretColor(RETRO_TEXT);
        bluePlayerField.setBorder(BorderFactory.createLineBorder(RETRO_NEON_PINK, 1));

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBackground(RETRO_PURPLE_PANEL);

        JLabel redLabel = new JLabel("Nom du joueur rouge:");
        redLabel.setForeground(RETRO_TEXT);
        redLabel.setFont(RETRO_LABEL_FONT);

        JLabel blueLabel = new JLabel("Nom du joueur bleu:");
        blueLabel.setForeground(RETRO_TEXT);
        blueLabel.setFont(RETRO_LABEL_FONT);

        form.add(redLabel);
        form.add(redPlayerField);
        form.add(blueLabel);
        form.add(bluePlayerField);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(RETRO_PURPLE_PANEL);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(form, BorderLayout.CENTER);

        StyledButtonFactory buttonFactory = new StyledButtonFactory(ThemeManager.getCurrentTheme());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(RETRO_PURPLE_PANEL);

        final boolean[] confirmed = {false};
        JDialog dialog = new JDialog(parentFrame, "Two Players Setup", true);

        JButton cancelButton = buttonFactory.createPrimaryButton("Annuler", dialog::dispose);
        JButton okButton = buttonFactory.createPrimaryButton("OK", () -> {
            confirmed[0] = true;
            dialog.dispose();
        });

        actions.add(cancelButton);
        actions.add(okButton);
        content.add(actions, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);

        if (!confirmed[0]) {
            return null;
        }

        return new MenuSelection(
                level,
                difficultyLabel,
            STRATEGY_TWO_PLAYERS,
                true,
                redPlayerField.getText(),
                bluePlayerField.getText());
    }

    private int mapDifficultyToLevel(String difficulty) {
        return switch (difficulty) {
            case "Facile" -> 1;
            case "Moyen" -> 2;
            case "Difficile" -> 3;
            case "Tres difficile" -> 4;
            case "Extreme" -> 5;
            default -> 1;
        };
    }
}

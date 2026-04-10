package mvc.model.view;

import java.awt.GridLayout;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class MainMenuView {

    public record MenuSelection(
            int level,
            String difficultyLabel,
            boolean isTwoPlayers,
            String redPlayerName,
            String bluePlayerName) {
    }

    public void show(Consumer<MenuSelection> onStart) {
        Objects.requireNonNull(onStart, "onStart must not be null");

        SwingUtilities.invokeLater(() -> {
            JFrame menuFrame = new JFrame("ASI GAME");
            menuFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            menuFrame.setSize(400, 300);
            menuFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel strategyLabel = new JLabel("Selection de strategie:");
            String[] strategies = {"Random Generation", "Two Players"};
            JComboBox<String> strategyComboBox = new JComboBox<>(strategies);

            JLabel difficultyLabel = new JLabel("Selection de Difficulte:");
            String[] difficulties = {"Facile", "Moyen", "Difficile", "Tres difficile", "Extreme"};
            JComboBox<String> difficultyComboBox = new JComboBox<>(difficulties);

            JButton startButton = new JButton(" Comencer le jeu ");
            startButton.addActionListener(e -> {
                String selectedStrategy = (String) strategyComboBox.getSelectedItem();
                String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
                int level = mapDifficultyToLevel(selectedDifficulty);

                if ("Two Players".equals(selectedStrategy)) {
                    MenuSelection selection = askTwoPlayerNames(menuFrame, level, selectedDifficulty);
                    if (selection == null) {
                        return;
                    }

                    menuFrame.dispose();
                    onStart.accept(selection);
                    return;
                }

                menuFrame.dispose();
                onStart.accept(new MenuSelection(level, selectedDifficulty, false, "", ""));
            });

            panel.add(strategyLabel);
            panel.add(strategyComboBox);
            panel.add(difficultyLabel);
            panel.add(difficultyComboBox);
            panel.add(startButton);

            menuFrame.add(panel);
            menuFrame.setVisible(true);
        });
    }

    private MenuSelection askTwoPlayerNames(JFrame parentFrame, int level, String difficultyLabel) {
        JTextField redPlayerField = new JTextField(15);
        JTextField bluePlayerField = new JTextField(15);

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Nom du joueur rouge:"));
        form.add(redPlayerField);
        form.add(new JLabel("Nom du joueur bleu:"));
        form.add(bluePlayerField);

        int result = JOptionPane.showConfirmDialog(
                parentFrame,
                form,
                "Two Players Setup",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        return new MenuSelection(
                level,
                difficultyLabel,
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

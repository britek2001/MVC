package mvc.model.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GameTutorialView {

    private static final Color RETRO_PURPLE_BG = new Color(56, 14, 92);
    private static final Color RETRO_PURPLE_PANEL = new Color(76, 25, 120);
    private static final Color RETRO_NEON_PINK = new Color(255, 88, 190);
    private static final Color RETRO_TEXT = new Color(245, 232, 255);
    private static final Font RETRO_TITLE_FONT = new Font("Monospaced", Font.BOLD, 20);
    private static final Font RETRO_TEXT_FONT = new Font("Monospaced", Font.PLAIN, 11);

    private void setCenteredSlideText(JTextPane contentArea, String text) {
        contentArea.setText(text);
        StyledDocument doc = contentArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    private void adjustTextHeight(JTextPane contentArea, JScrollPane scrollPane) {
        int viewportWidth = scrollPane.getViewport().getWidth();
        if (viewportWidth <= 0) {
            viewportWidth = 560;
        }
        contentArea.setSize(new Dimension(viewportWidth, Short.MAX_VALUE));
        int preferred = contentArea.getPreferredSize().height + 10;
        int clamped = Math.max(260, Math.min(preferred, 480));
        scrollPane.setPreferredSize(new Dimension(viewportWidth, clamped));
        scrollPane.revalidate();
    }


    private final String[] slideContents = {
        "\n"
            + "            OBJECTIF          \n"
            + "Placez 4 formes bleues pour couvrir\n"
            + "un maximum de surface.\n\n\n"
            + "            REGLE              \n"
            + "Les formes bleues ne doivent jamais\n"
            + "toucher les obstacles rouges.\n\n\n"
            + "            FORMES.             \n"
            + "Cercles  ·  Rectangles\n\n\n"
            + "            ACTIONS.            \n"
            + "Créer · Déplacer · Redimensionner · Supprimer\n\n"
            + "            COMMANDES            \n"
            + "Annuler · Rétablir\n\n\n"
            + "Jouez stratégique. Maximisez votre score.",

        "\n"
            + "           STRATÉGIE          \n"
            + " Placez vos 4 formes bleues\n"
            + " Évitez tous les obstacles rouges\n"
            + " Maximisez la zone couverte\n\n\n"
            + "           MODES DE JEU           \n\n"
            + "##  RANDOM  ##\n"
            + "    Les obstacles sont placés au hasard.\n"
            + "    Chaque partie est unique !\n"
            + "##  2 JOUEURS  ##\n"
            + "    C'est vous qui créez les obstacles !\n"
            + "    Jouez contre un ami.\n"
            + "##  IA  ##\n"
            + "    Affrontez l'ordinateur.\n"
            + "    L'IA joue comme un vrai adversaire avec des strategy adaptative.\n\n\n"
            + "           FORMES           \n"
            + "  Cercles     Rectangles\n\n\n"
            + "           VOTRE OUTIL DE JEU           \n"
            + "  Extrémité de la figure : redimensionner\n"
            + "  Centre de la figure : déplacement\n"
            + "  Le point d'action doit être centré au milieu.\n"
            + " + créer  X supprimer\n"
            + " <- annuler  -> rétablir\n"
            + "  Choix de figure dans chaque generation : Button Figure +  drag and drop\n\n\n"
            + "           OBJECTIF           \n"
            + "Une stratégie claire et maximiser l'espace,\n"
    };


    private int currentSlide = 0;

    public void show(Runnable onComplete) {
        SwingUtilities.invokeLater(() -> {
            JFrame tutorialFrame = new JFrame("TUTORIEL DU JEU");
            tutorialFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            tutorialFrame.setSize(650, 700);
            tutorialFrame.setLocationRelativeTo(null);
            tutorialFrame.getContentPane().setBackground(RETRO_PURPLE_BG);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(RETRO_NEON_PINK, 3),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)));
            mainPanel.setBackground(RETRO_PURPLE_PANEL);

            JLabel titleLabel = new JLabel("MAX SPACE", JLabel.CENTER);
            titleLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
            titleLabel.setForeground(RETRO_NEON_PINK);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            JTextPane contentArea = new JTextPane() {
                @Override
                public boolean getScrollableTracksViewportWidth() {
                    return true;
                }
            };
            contentArea.setEditable(false);
            contentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            contentArea.setBackground(RETRO_PURPLE_PANEL);
            contentArea.setForeground(RETRO_TEXT);
            contentArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setCenteredSlideText(contentArea, slideContents[0]);

            JScrollPane scrollPane = new JScrollPane(contentArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setBackground(RETRO_PURPLE_PANEL);
            scrollPane.getVerticalScrollBar().setForeground(RETRO_NEON_PINK);
            SwingUtilities.invokeLater(() -> adjustTextHeight(contentArea, scrollPane));
            
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setBackground(RETRO_PURPLE_PANEL);
            centerPanel.add(scrollPane, BorderLayout.CENTER);
            
            mainPanel.add(centerPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.setBackground(RETRO_PURPLE_PANEL);

            JLabel slideCounter = new JLabel("1 / " + slideContents.length);
            slideCounter.setFont(RETRO_TEXT_FONT);
            slideCounter.setForeground(RETRO_TEXT);

            JButton nextButton = new JButton("SUIVANT >");
            nextButton.setFont(new Font("Monospaced", Font.BOLD, 11));
            nextButton.setFocusPainted(false);

            JButton prevButton = new JButton("< PRÉCÉDENT");
            prevButton.setFont(new Font("Monospaced", Font.BOLD, 11));
            prevButton.setFocusPainted(false);
            prevButton.setEnabled(false);
            prevButton.addActionListener(e -> {
                if (currentSlide > 0) {
                    currentSlide--;
                    setCenteredSlideText(contentArea, slideContents[currentSlide]);
                    adjustTextHeight(contentArea, scrollPane);
                    prevButton.setEnabled(currentSlide > 0);
                    nextButton.setEnabled(currentSlide < slideContents.length - 1);
                    slideCounter.setText((currentSlide + 1) + " / " + slideContents.length);
                }
            });

            nextButton.addActionListener(e -> {
                if (currentSlide < slideContents.length - 1) {
                    currentSlide++;
                    setCenteredSlideText(contentArea, slideContents[currentSlide]);
                    adjustTextHeight(contentArea, scrollPane);
                    prevButton.setEnabled(currentSlide > 0);
                    nextButton.setEnabled(currentSlide < slideContents.length - 1);
                    slideCounter.setText((currentSlide + 1) + " / " + slideContents.length);
                }
            });

            JButton startGameButton = new JButton("COMMENCER !");
            startGameButton.setFont(new Font("Monospaced", Font.BOLD, 11));
            startGameButton.setFocusPainted(false);
            startGameButton.addActionListener(e -> {
                tutorialFrame.dispose();
                onComplete.run();
            });

            buttonPanel.add(prevButton);
            buttonPanel.add(Box.createHorizontalStrut(10));
            buttonPanel.add(slideCounter);
            buttonPanel.add(Box.createHorizontalStrut(10));
            buttonPanel.add(nextButton);
            buttonPanel.add(Box.createHorizontalStrut(20));
            buttonPanel.add(startGameButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            tutorialFrame.add(mainPanel);
            tutorialFrame.setVisible(true);
        });
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class MorpionBot extends JFrame {
    private JButton[][] cases;
    private boolean tourJoueurX;
    private String joueur1, joueur2;
    private String symboleJoueur1, symboleJoueur2;
    private JLabel statut;
    private boolean modeContreBot;
    private boolean partieEnCours = false;
    private String difficulteBot = ""; // Niveau de difficulté par défaut (vide tant qu'il n'est pas choisi)
    private JButton boutonLancerPartie;

    public MorpionBot() {
        setTitle("Jeu de Morpion");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        configurerPartie();
    }

    // Configurer la page de configuration
    private void configurerPartie() {
        JPanel panneauConfiguration = new JPanel(new GridLayout(8, 1));
        panneauConfiguration.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Bienvenue dans le Morpion", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel choixMode = new JPanel(new GridLayout(1, 2));
        JButton bouton2Joueurs = new JButton("2 Joueurs");
        JButton boutonBot = new JButton("Contre un Bot");
        bouton2Joueurs.addActionListener(e -> lancerConfiguration(false));
        boutonBot.addActionListener(e -> lancerConfiguration(true));
        choixMode.add(bouton2Joueurs);
        choixMode.add(boutonBot);

        panneauConfiguration.add(titre);
        panneauConfiguration.add(new JLabel(""));
        panneauConfiguration.add(new JLabel("Choisissez un mode de jeu :", SwingConstants.CENTER));
        panneauConfiguration.add(choixMode);

        setContentPane(panneauConfiguration);
        setVisible(true);
    }

    private void lancerConfiguration(boolean contreBot) {
        this.modeContreBot = contreBot;

        JPanel panneauFormulaire = new JPanel(new GridLayout(contreBot ? 7 : 6, 2));
        panneauFormulaire.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titre = new JLabel("Configuration de la Partie", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField champJoueur1 = new JTextField();
        JTextField champJoueur2 = new JTextField();
        champJoueur2.setEnabled(false);

        panneauFormulaire.add(titre);
        panneauFormulaire.add(new JLabel(""));

        panneauFormulaire.add(new JLabel("Nom du Joueur 1 :"));
        panneauFormulaire.add(champJoueur1);

        if (!contreBot) {
            panneauFormulaire.add(new JLabel("Nom du Joueur 2 :"));
            panneauFormulaire.add(champJoueur2);
            champJoueur2.setEnabled(true);
        }

        JPanel choixSymboles = new JPanel(new GridLayout(1, 2));
        JButton boutonX = new JButton("X (Bleu)");
        JButton boutonO = new JButton("O (Rouge)");

        boutonX.addActionListener(e -> {
            symboleJoueur1 = "X";
            symboleJoueur2 = "O";
            verifierEtActiverBouton(champJoueur1, champJoueur2);
        });

        boutonO.addActionListener(e -> {
            symboleJoueur1 = "O";
            symboleJoueur2 = "X";
            verifierEtActiverBouton(champJoueur1, champJoueur2);
        });

        panneauFormulaire.add(new JLabel("Choisissez votre symbole :"));
        panneauFormulaire.add(choixSymboles);
        choixSymboles.add(boutonX);
        choixSymboles.add(boutonO);

        if (contreBot) {
            // Ajouter la sélection de la difficulté
            panneauFormulaire.add(new JLabel("Choisissez la difficulté :"));
            JPanel choixDifficulte = new JPanel(new GridLayout(1, 3));
            JButton boutonFacile = new JButton("Facile");
            JButton boutonMoyen = new JButton("Moyen");
            JButton boutonDifficile = new JButton("Difficile");

            boutonFacile.addActionListener(e -> {
                difficulteBot = "Facile";
                verifierEtActiverBouton(champJoueur1, champJoueur2);
            });
            boutonMoyen.addActionListener(e -> {
                difficulteBot = "Moyen";
                verifierEtActiverBouton(champJoueur1, champJoueur2);
            });
            boutonDifficile.addActionListener(e -> {
                difficulteBot = "Difficile";
                verifierEtActiverBouton(champJoueur1, champJoueur2);
            });

            choixDifficulte.add(boutonFacile);
            choixDifficulte.add(boutonMoyen);
            choixDifficulte.add(boutonDifficile);
            panneauFormulaire.add(choixDifficulte);
        }

        boutonLancerPartie = new JButton("Lancer la Partie");
        boutonLancerPartie.setEnabled(false);  // Initialement désactivé
        boutonLancerPartie.addActionListener(e -> initialiserPartie(champJoueur1.getText(), contreBot ? "Bot" : champJoueur2.getText()));

        panneauFormulaire.add(boutonLancerPartie);

        setContentPane(panneauFormulaire);
        setVisible(true);
    }

    private void verifierEtActiverBouton(JTextField champJoueur1, JTextField champJoueur2) {
        // Vérifie que toutes les informations nécessaires sont fournies
        String joueur1Nom = champJoueur1.getText().trim();
        boolean estContreBot = modeContreBot;
        String joueur2Nom = modeContreBot ? "Bot" : champJoueur2.getText().trim();

        // Vérifie que le prénom, le symbole et la difficulté (pour le bot) sont définis
        boolean informationsCompletes = !joueur1Nom.isEmpty() && !symboleJoueur1.isEmpty() &&
                                         (estContreBot ? !difficulteBot.isEmpty() : !joueur2Nom.isEmpty());
        boutonLancerPartie.setEnabled(informationsCompletes);
    }

    private void initialiserPartie(String joueur1, String joueur2) {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.tourJoueurX = symboleJoueur1.equals("X");
        this.partieEnCours = true;

        cases = new JButton[3][3];
        JPanel panneauJeu = new JPanel(new GridLayout(3, 3));
        panneauJeu.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        Font font = new Font("Arial", Font.BOLD, 60);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton bouton = new JButton("");
                bouton.setFont(font);
                bouton.setFocusPainted(false);
                bouton.addActionListener(e -> jouer((JButton) e.getSource()));
                cases[i][j] = bouton;
                panneauJeu.add(bouton);
            }
        }

        statut = new JLabel("Tour de " + joueur1 + " (" + symboleJoueur1 + ")", SwingConstants.CENTER);
        statut.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel panneauPrincipal = new JPanel(new BorderLayout());
        panneauPrincipal.add(panneauJeu, BorderLayout.CENTER);
        panneauPrincipal.add(statut, BorderLayout.SOUTH);

        setContentPane(panneauPrincipal);
        setVisible(true);
    }

    private void jouer(JButton bouton) {
        if (!bouton.getText().equals("") || !partieEnCours) return;

        String symboleActuel = tourJoueurX ? "X" : "O";
        String joueurActuel = tourJoueurX ? joueur1 : joueur2;

        bouton.setText(symboleActuel);
        bouton.setForeground(tourJoueurX ? Color.BLUE : Color.RED);

        if (verifierVictoire(symboleActuel)) {
            afficherVictoire(symboleActuel);
        } else if (grillePleine()) {
            JOptionPane.showMessageDialog(this, "Match nul !");
            reinitialiserPartie();
        } else {
            tourJoueurX = !tourJoueurX;
            statut.setText("Tour de " + (tourJoueurX ? joueur1 : joueur2) + " (" + (tourJoueurX ? symboleJoueur1 : symboleJoueur2) + ")");

            if (!tourJoueurX && modeContreBot) {
                jouerBotAvecDelai();
            }
        }
    }

    private void jouerBotAvecDelai() {
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jouerBot();
        });
    }

    private void jouerBot() {
        ArrayList<JButton> boutonsLibres = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cases[i][j].getText().equals("")) {
                    boutonsLibres.add(cases[i][j]);
                }
            }
        }

        JButton boutonChoisi = boutonsLibres.get(new Random().nextInt(boutonsLibres.size()));
        jouer(boutonChoisi);
    }

    private void afficherVictoire(String symbole) {
        for (int i = 0; i < 3; i++) {
            if (cases[i][0].getText().equals(symbole) && cases[i][1].getText().equals(symbole) && cases[i][2].getText().equals(symbole)) {
                marquerVictoire(cases[i][0], cases[i][1], cases[i][2]);
                return;
            }
            if (cases[0][i].getText().equals(symbole) && cases[1][i].getText().equals(symbole) && cases[2][i].getText().equals(symbole)) {
                marquerVictoire(cases[0][i], cases[1][i], cases[2][i]);
                return;
            }
        }

        if (cases[0][0].getText().equals(symbole) && cases[1][1].getText().equals(symbole) && cases[2][2].getText().equals(symbole)) {
            marquerVictoire(cases[0][0], cases[1][1], cases[2][2]);
            return;
        }
        if (cases[0][2].getText().equals(symbole) && cases[1][1].getText().equals(symbole) && cases[2][0].getText().equals(symbole)) {
            marquerVictoire(cases[0][2], cases[1][1], cases[2][0]);
        }
    }

    private void marquerVictoire(JButton b1, JButton b2, JButton b3) {
        b1.setBackground(Color.GREEN);
        b2.setBackground(Color.GREEN);
        b3.setBackground(Color.GREEN);
        JOptionPane.showMessageDialog(this, (tourJoueurX ? joueur1 : joueur2) + " a gagné !");
        reinitialiserPartie();
    }

    private boolean verifierVictoire(String symbole) {
        for (int i = 0; i < 3; i++) {
            if (cases[i][0].getText().equals(symbole) && cases[i][1].getText().equals(symbole) && cases[i][2].getText().equals(symbole)) return true;
            if (cases[0][i].getText().equals(symbole) && cases[1][i].getText().equals(symbole) && cases[2][i].getText().equals(symbole)) return true;
        }
        return (cases[0][0].getText().equals(symbole) && cases[1][1].getText().equals(symbole) && cases[2][2].getText().equals(symbole)) ||
                (cases[0][2].getText().equals(symbole) && cases[1][1].getText().equals(symbole) && cases[2][0].getText().equals(symbole));
    }

    private boolean grillePleine() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cases[i][j].getText().equals("")) return false;
            }
        }
        return true;
    }

    private void reinitialiserPartie() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cases[i][j].setText("");
                cases[i][j].setBackground(null);
            }
        }
        tourJoueurX = symboleJoueur1.equals("X");
        statut.setText("Tour de " + (tourJoueurX ? joueur1 : joueur2) + " (" + (tourJoueurX ? symboleJoueur1 : symboleJoueur2) + ")");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MorpionBot::new);
    }
}

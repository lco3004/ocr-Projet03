package fr.ocr.utiles;

import static fr.ocr.params.LireParametres.getParam;
import static fr.ocr.params.Parametres.NOMBRE_D_ESSAIS;

/**
 * <p>
 *
 * @author laurent cordier
 * constantes du programme
 * </p>
 */
public enum Constantes {
    ;

    /**
     * version de l'application
     */
    public enum VersionPGM {
        VERSION_APPLICATION("V1.0.1");
        private String version;

        VersionPGM(String s) {
            version = s;
        }

        public String getVersion() {
            return version;
        }
    }

    /**
     * nom du fichier properties
     */
    public enum NomFichiersParametres {
        FICHIER_PARAM_MASTER_MIND("target/params.properties");

        private String nomFichier;

        NomFichiersParametres(String s) {
            nomFichier = s;
        }

        public String getNomFichier() {
            return nomFichier;
        }
    }

    /**
     * les couleurs du mastermind
     */
    public enum CouleursMastermind {
        AUBERGINE(0, 'A'),
        JAUNE(1, 'J'),
        VERT(2, 'V'),
        ROUGE(3, 'R'),
        BLEU(4, 'B'),
        ORANGE(5, 'O'),
        TURQUOISE(6, 'T'),
        MAUVE(7, 'M'),
        INDIGO(8, 'I'),
        CARMIN(9, 'C'),
        DORIAN(10, 'D');

        private int valeurFacialeDeLaCouleur;
        private char lettreInitiale;

        CouleursMastermind(int i, char c) {
            valeurFacialeDeLaCouleur = i;
            lettreInitiale = c;
        }

        public static int getValeurParLettre(char lettre) {
            int valret = -1;
            for (CouleursMastermind couleursMastermind : CouleursMastermind.values()) {
                char c = couleursMastermind.getLettreInitiale();
                if (c == lettre) {
                    valret = couleursMastermind.getValeurFacialeDeLaCouleur();
                    break;
                }
            }
            return valret;
        }

        public int getValeurFacialeDeLaCouleur() {
            return valeurFacialeDeLaCouleur;
        }

        public char getLettreInitiale() {
            return lettreInitiale;
        }
    }

    /**
     * Constantes pour les menus
     */
    public enum Libelles {
        ;

        /**
         * characters escape
         */
        public enum CharactersEscape {
            X, //quitter
            K  //retour menu principal

        }

        public enum CharacterExceptionPM {
            C, // abandon operation sur chiffre hors plage de tolerance.
            S  // abandon operation sur caractere su score  hors plage de tolerance.
        }

        /**
         * noms des jeux proposés
         */
        public enum LibellesJeux {
            MASTERMIND,
            PLUSMOINS
        }

        /**
         * libelle des lignes du menu principal
         * LIGNE_ETAT : contenu variable en fonction de l'action effectuée
         * par l'utilisateur
         */
        public enum LibellesMenuPrincipal {
            TITRE,
            LIGNE_ETAT,
            CHOISIR_MASTERMIND,
            CHOISIR_PLUS_MOINS,
            QUITTER,
            SAISIR_CHOIX
        }

        /**
         * libelle des lignes du menu secondaire
         * LIGNE_ETAT : contenu variable en fonction de l'action effectuée
         * par l'utilisateur .
         * TITRE  varie selon le choix effectué dans le menu principal (Mastemrind ou PlusMoins
         */
        public enum LibellesMenuSecondaire {

            TITRE,
            LIGNE_ETAT,
            JEUX,
            MODE_CHALLENGER,
            MODE_DEFENSEUR,
            MODE_DUEL,
            RETOUR,
            LOGGER_PARAMETRES,
            QUITTER,
            SAISIR_CHOIX
        }
    }

    /**
     * constantes pour validation/scoring d'une proposition
     */
    public interface ConstEvalPropale {
        //pour résultat evaluation proposition : Noir == pion bien place,  blanc == pion mal placé
        int NOIR_BIENPLACE = 0;
        int BLANC_MALPLACE = NOIR_BIENPLACE + 1;
        //libelle colonne eval
        char PIONS_BIENPLACES = 'N';
        char PIONS_MALPLACES = 'B';
    }

    /**
     * taille par default des StringBuilder
     */
    public interface ConstTailleStringBuilder {
        int TAIILE_INITIALE = 2048;
    }

    /**
     *pour table de jeu - les diffrents types de lignes de la table de jeu
     */
    public interface ConstTypeDeLigne {

        int TITRE = 0;
        int LIGNE_STATUS = TITRE + 1;
        int LIGNE_SECRETE = LIGNE_STATUS + 1;
        int LIGNE_BLANCH01 = LIGNE_SECRETE + 1;
        int LIGNE_ENTETE = LIGNE_BLANCH01 + 1;
        int LIGNE_PROPOSITION = LIGNE_ENTETE + 1;
        int LIGNE_BLANCH02 = LIGNE_PROPOSITION + (Integer) getParam(NOMBRE_D_ESSAIS);
        int LIGNE_TOUTES_COULEURS = LIGNE_BLANCH02 + 1;
        int LIGNE_BLANCH03 = LIGNE_TOUTES_COULEURS + 1;
        int LIGNE_DE_SAISIE = LIGNE_BLANCH03 + 1;

        //dimesion
        int NBRE_LIGNESTABLEMM = LIGNE_DE_SAISIE + 1;

    }
}
/*
 * ***************************************************************************************************************
 *  the end
 * ***************************************************************************************************************
 */
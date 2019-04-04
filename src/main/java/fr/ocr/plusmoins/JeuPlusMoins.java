package fr.ocr.plusmoins;

import fr.ocr.modeconsole.IOConsole;
import fr.ocr.utiles.AppExceptions;
import fr.ocr.utiles.Constantes;
import fr.ocr.utiles.Messages;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static fr.ocr.params.LireParametres.getParam;
import static fr.ocr.params.Parametres.*;
import static fr.ocr.utiles.Constantes.Libelles.LibellesMenuSecondaire;
import static fr.ocr.utiles.Logs.logger;
import static fr.ocr.utiles.Messages.ErreurMessages.ERREUR_GENERIC;
import static fr.ocr.utiles.Messages.InfosMessages.CTRL_C;
import static fr.ocr.utiles.Messages.InfosMessages.SORTIE_SUR_ESCAPECHAR;


/**
 * Classe du jeu Plus Moins
 */

public class JeuPlusMoins {

    private String strLibelleStatus;
    private String strLibelleInfos;
    private String strLibelleSaisie;
    private String strLibelleSecretHeader;
    private String strLibelleSecretTrailer;

    private char[] score;
    private char[] essai;
    private char[] secret;
    private char[][] tablePM;

    /**
     * Constructeur jeuPlusMoins
     */
    public JeuPlusMoins(LibellesMenuSecondaire modeJeu, Scanner sc) {

        scanner = sc;

        modeDeJeu = modeJeu;

        initLibellesLignes();

        initDesTables(nombreDePositions);

    }

    // les options du menu du jeu PlusMoins
    private LibellesMenuSecondaire modeDeJeu;

    // scanner pour saisie clavier
    private Scanner scanner;

    // nombre lignes de jeu de la table du jeu PlusMoins
    private Integer nombreDeEssaisMax = (Integer) getParam(NOMBRE_D_ESSAIS);

    //le caractère EscapeChar de retour au menu superieur
    private Character charactersEscape = Constantes.Libelles.CharactersEscape.K.toString().charAt(0);

    // paramètre du fichier properties - nombre de positions  par ligne de la table de jeu PlusMoins
    private Integer nombreDePositions = (Integer) getParam(NOMBRE_DE_POSITIONS);

    // paramètre du fichier properties - affiche (DEBUG=Vrai) ou pas la combinaison secrete
    private Boolean modeDebug = (Boolean) getParam(MODE_DEVELOPPEUR);

    //nombre maxi de boucle a la recherche d'une combinaison secrete qui ne contiennent pas de zéro
    private int nombreMaxDeBoucles = (int) getParam(NOMBRE_MAXI_DE_BOUCLES_RANDOMIZE);


    private void initDesTables(int nbPos) {
        //allocation en espérant que le garbage fait le job
        score = new char[nombreDePositions];
        essai = new char[nombreDePositions];
        secret = new char[nombreDePositions];
        tablePM = new char[nombreDeEssaisMax][2 * nbPos];

    }

    /**
     * chargement libelles des lignes du jeux
     */
    private void initLibellesLignes() {
        strLibelleStatus = "[     Status                ]";
        strLibelleInfos = "[     Informations          ] ";
        strLibelleSaisie = ". Saisie -> (K : retour)    ] ";
        strLibelleSecretHeader = "{ ";
        strLibelleSecretTrailer = " }";
    }

    /**
     * @param rang  entier qui est égal au numéro de ligne du tableau tabelPM dans laquelle ilfaut ajouter l'essai
     * @param essai le tableau de char  dont les colonnes contiennent les valeurs de l'essai
     */
    private void AjouterUnEssai(int rang, char[] essai, char[][] tablePM) {
        try {
            for (char c : essai) {
                //leve uen exception si valeur est <=0
                ChiffreEstIlValide(c);
            }

            System.arraycopy(essai, 0, tablePM[rang], 0, essai.length);

        } catch (IndexOutOfBoundsException | ArrayStoreException | NullPointerException e) {
            logger.error(Messages.ErreurMessages.SORTIE_PGM_SUR_ERREURNONGEREE);
            throw new AppExceptions(Messages.ErreurMessages.ERREUR_GENERIC);
        }
    }

    /**
     * @param rang  entier qui est égal au numéro de ligne du tableau tabelPM dans laquelle ilfaut ajouter le score
     * @param score le tableau de char dont les colonnes contiennent les valeurs du score
     */
    private void AjouterunScore(int rang, char[] score, char[][] tablePM) throws AppExceptions {
        try {
            for (char c : score) {
                //leve uen esception si valeur est <=0
                ScoreEstIlValide(c);
            }
            System.arraycopy(score, 0, tablePM[rang], nombreDePositions, score.length);

        } catch (IndexOutOfBoundsException | ArrayStoreException | NullPointerException e) {
            logger.error(Messages.ErreurMessages.SORTIE_PGM_SUR_ERREURNONGEREE);
            throw new AppExceptions(Messages.ErreurMessages.ERREUR_GENERIC);
        }
    }

    /**
     * les lignes du tableau tablePm, sous forme affichable
     * avec
     * * - en tête
     * * - zone d'affichage du secret
     * * - zone de saisie
     * * - zone d'information
     * modele primaire - pas de list pas de stream - non // (sauf ajout synchronized)
     * peut être aisément converti en assembleur 8086.
     *
     * @return String[] , les lignes du tableau tablePm, sous forme affichable
     */
    private StringBuilder[] getLignesJeu(char[] secret, char[][] tablePM) {

        //tableau de chaine de caractères pour affichage du jeu PlusMoins
        StringBuilder[] lignesJeu = new StringBuilder[256];

        //creation de chaque variable StringBuilder dans le tableau qui correspond aux lignes  affichablse, du jeu
        for (int n = 0; n < lignesJeu.length; n++) {
            lignesJeu[n] = new StringBuilder(48);
        }
        int rangLigne = 0;
        //titre
        lignesJeu[rangLigne++].append(modeDeJeu.toString());

        //espace
        lignesJeu[rangLigne++].append(" ");


        //zone de status
        if (modeDebug) {
            StringBuilder stmp = new StringBuilder(16);
            for (char c : secret) {
                stmp.append(String.format(" %c ", c));
            }
            lignesJeu[rangLigne++].append(strLibelleSecretHeader).append(stmp.toString()).append(strLibelleSecretTrailer);
        } else {
            lignesJeu[rangLigne++].append(strLibelleStatus);
        }

        //espace
        lignesJeu[rangLigne++].append(" ");

        //parcours des lignes
        for (char[] uneLigne : tablePM) {
            lignesJeu[rangLigne].append("{ ");
            int countCol = 0;
            //parcours des colonnes
            for (char uneValeur : uneLigne) {
                //separateur entre zone essai et zone score
                if (countCol == nombreDePositions) {
                    lignesJeu[rangLigne].append(" | ");
                }
                lignesJeu[rangLigne].append((uneValeur == 0) ? " x " : String.format(" %c ", uneValeur));
                countCol++;
            }
            lignesJeu[rangLigne].append("}");
            rangLigne++;
        }

        //espace
        lignesJeu[rangLigne++].append(" ");

        //zone d'information
        lignesJeu[rangLigne++].append(strLibelleInfos);

        //zone de saisie
        lignesJeu[rangLigne].append(strLibelleSaisie);

        StringBuilder[] strValRet = new StringBuilder[rangLigne + 1];

        System.arraycopy(lignesJeu, 0, strValRet, 0, rangLigne + 1);

        return strValRet;
    }

    /**
     * vérification que la caractère passé en paramétre correspond à un chiffre et s'il est dans la plage 1-9
     *
     * @param c caractere à évaluae : doit être dans le range 1-9
     * @throws AppExceptions avec CharacterExceptionPM.A comme caractere de sortie (ce qui permet de gérer car c'est
     *                       une exception qui n'est pas fatale - il faut refaire une saisie.
     */
    private void ChiffreEstIlValide(char c) throws AppExceptions {
        int i;
        try {
            i = Integer.valueOf(String.valueOf(c));
            if (i <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new AppExceptions(Messages.InfosMessages.PM_CHIFFRE_HORS_PLAGE_TOLERANCE, Constantes.Libelles.CharacterExceptionPM.C.toString().charAt(0));
        }
    }

    /**
     * vérification que la caractère passé en paramétre correspond à = ou - ou +, seuls caractère possible pour  un score
     *
     * @param c caractere à évaluae : doit être dans l'ensemble {=,-,+}
     * @throws AppExceptions avec CharacterExceptionPM.A comme caractere de sortie (ce qui permet de gérer car c'est
     *                       une exception qui n'est pas fatale - il faut refaire une saisie.
     */
    private void ScoreEstIlValide(char c) {
        if (c != '=' && c != '-' && c != '+') {
            throw new AppExceptions(Messages.InfosMessages.PM_CARSCORE_HORS_PLAGE_TOLERANCE, Constantes.Libelles.CharacterExceptionPM.S.toString().charAt(0));
        }
    }

    /**
     * Saisie clavier - supporte les méthodes de saisies suivantes
     * Si x et y sont des valeurs acceptables (donc pattern == [x y K k] avec k étant escapeChar
     * x return  y return   => donne x y
     * x y return           => donne x y
     * xyreturn             => donne x y
     * x{n'importe quoi sauf escapeChar}y  => x y
     * x{n'importe quoi sauf escapeChar}Ky => escapeChar donc break
     * *  EscapeChar : soit retour au niveau d'appel soit saisie par défaut (i.e score suggéré est saisi par défaut
     * et devient le score renvoyé)
     *
     * @param lesCharsAFournir char [] table de char renseigné par la saisie
     * @param secret           char []  sert à l'affichage
     * @param tablePM          char [][] sert à l'affichage
     * @param pattern_Menu     String  , pattern de saisie utilisé par méthode Next de la classe Scanner
     * @param escapeChar       : Character  - charactère qui s'il est présent dans la saisie, stop le process de saisie
     * @throws AppExceptions : déclenché sur erreur non géré ou sur lecture de EscapeChar dans ce cas l'escape est transmis
     *                       à l'exception et est récupérable par la méthode getCharacterSortie() de AppException
     */
    private void SaisirDesChars(char[] lesCharsAFournir, char[] secret,
                                char[][] tablePM, String pattern_Menu,
                                Character escapeChar) throws AppExceptions {

        Pattern patternChoix = Pattern.compile(pattern_Menu);

        StringBuilder saisieParChaine = new StringBuilder(nombreDePositions);

        Character cRet = '?';

        IOConsole.ClearScreen.cls();

        AfiicheJeuPM(secret, tablePM);
        // scanner en mode hasNext, next - avec pattern de carteres autorisés

        int localCount = 0;

        try {
            while (localCount < nombreDePositions && cRet != escapeChar && scanner.hasNext()) {

                try {
                    saisieParChaine.delete(0, saisieParChaine.length());

                    for (char c : scanner.next().toCharArray()) {
                        if (c != ' ')
                            saisieParChaine.append(c).append(' ');
                    }

                    //on tente une lecture sur cette chaine ave cloture auto sur ce scanner
                    try (Scanner monScan = new Scanner(saisieParChaine.toString())) {

                        while (localCount < nombreDePositions && monScan.hasNext()) {

                            try {
                                cRet = monScan.next(patternChoix).toUpperCase().charAt(0);

                                if (cRet != escapeChar) {

                                    lesCharsAFournir[localCount++] = cRet;

                                    strLibelleSaisie += " " + cRet;

                                } else {
                                    break;
                                }

                            } catch (InputMismatchException e) {

                                monScan.next();
                            } catch (NoSuchElementException e) {

                                cRet = escapeChar; //Ctrl-C

                                break;
                            }
                        }
                    }

                } catch (NoSuchElementException e) {
                    // one devrait pas arrive la ou alors pas compris donc : on sort proprement
                    cRet = escapeChar;

                    break;
                } catch (IllegalStateException e) {

                    logger.error(String.format("%s %s %s", ERREUR_GENERIC, e.getClass().getSimpleName(), " scanner fermé"));

                    throw new AppExceptions(ERREUR_GENERIC);
                }

            }

            IOConsole.ClearScreen.cls();

            AfiicheJeuPM(secret, tablePM);

            //soirtie par escapeChar ou CTRL-C
            if (cRet == '?' || cRet == escapeChar) {

                logger.info(CTRL_C.toString() + " ou " + escapeChar.toString());

                throw new AppExceptions(SORTIE_SUR_ESCAPECHAR, charactersEscape);

            }

        } catch (IllegalStateException e) {

            logger.error(String.format("%s %s %s", ERREUR_GENERIC, e.getClass().getSimpleName(), " scanner fermé"));

            throw new AppExceptions(ERREUR_GENERIC);
        }
    }
    /**
     * saisie d'un essai (acces clavier)
     *
     * @param nouvelEssai  char [] nouvel essai saisie
     * @param secret       char [] secret pour affichage en mode debug
     * @param tablePM      char [][] table des lignes d'essai et de score
     * @param pattern_Menu String pour pattern saisi
     * @param escapeChar   caractère escape
     * @throws AppExceptions sur CTRL-C ou saisie EscapeChar
     */
    private void SaisieUnEssaiJoueur(char[] nouvelEssai, char[] secret,
                                     char[][] tablePM, String pattern_Menu,
                                     Character escapeChar) throws AppExceptions {

        SaisirDesChars(nouvelEssai, secret, tablePM, pattern_Menu, escapeChar);

    }

    /**
     * renvoie un nouvel essai calculé
     *
     * @param rang        int  rang courant dans tablePM
     * @param nouvelEssai char [] contient le nouvel essai calculé
     * @param score       char [] contient le score de l'essai en cours
     * @param tablePM     char [][] table du jeu
     */
    private void CalculUnNouvelEssaiOrdi(int rang, char[] nouvelEssai, char[] score, char[][] tablePM) {
        int defautMin = 0, defautMax = 9;

        for (int i = 0; i < nombreDePositions; i++) {

            //selon le score obtenu, le calcul est différent car il faut tenir compte du sens + ou -
            switch (score[i]) {
                case '=': {
                    //le chiffres est le bon donc on reporte à l'identique
                    nouvelEssai[i] = tablePM[rang - 1][i];
                }
                break;

                case '-': {
                    //le chiffre est inférieur au secret
                    // essai[i] < nouvelEssai[i] < BorneSUPx
                    // BorneSupx = min(essai[y],score[y] == '+'
                    int borneSup = 9;

                    for (int m = rang - 1; m >= 0; m--) {
                        if (tablePM[m][i + nombreDePositions] == '+') {
                            int val2Tmp = tablePM[m][i] - '0';
                            borneSup = StrictMath.min(borneSup, val2Tmp);
                        }
                    }

                    if (borneSup < (tablePM[rang - 1][i] - '0')) {
                        logger.error(Messages.ErreurMessages.ERREUR_GENERIC.toString() + "calcul  borneSup erreur");
                        throw new AppExceptions(Messages.ErreurMessages.ERREUR_GENERIC);
                    }

                    // prend le milieu du segmznt essai[i] .. borneSup, valeur superieur car on "monte" vers la soluce
                    nouvelEssai[i] = (char) ((int) StrictMath.ceil(((double) borneSup + (double) (tablePM[rang - 1][i] - '0')) / 2.0) + '0');

                }
                break;

                case '+': {
                    //le chiffre est supérieur au secret
                    //  BorenInfx <  nouvelEssai[i] < essai[i]
                    // BorenInfx = max(essai[y],score[y] == '-'
                    int borneInf = 0;

                    for (int m = rang - 1; m >= 0; m--) {
                        if (tablePM[m][i + nombreDePositions] == '-') {
                            int valTmp = tablePM[m][i] - '0';
                            borneInf = StrictMath.max(borneInf, valTmp);
                        }
                    }

                    if (borneInf > (tablePM[rang - 1][i] - '0')) {
                        logger.error(Messages.ErreurMessages.ERREUR_GENERIC.toString() + "calcul  borneInf erreur");
                        throw new AppExceptions(Messages.ErreurMessages.ERREUR_GENERIC);
                    }

                    // prend le milieu du segmznt essai[i] .. borneSup, valeur inferieur car on "descend" vers la soluce
                    nouvelEssai[i] = (char) ((int) StrictMath.floor(((double) (tablePM[rang - 1][i] - '0') + (double) borneInf) / 2.0) + '0');
                }
                break;

                //section init car c'est le premier essai
                default: {
                    char[] essaiParDefaut = new char[nouvelEssai.length];
                    nouvelEssai[i] = (char) ('0' + (16 * (i * 3 + 1)) % 9);
                }
            }
        }
    }

    /**
     * calcul le score d'un essai proposé par le joueur
     *
     * @param essai  char [] essai à scorer
     * @param score  char []  score de l'essai q- renseigné en sortie de méthode
     * @param secret char [] pour calcul du score
     * @return boolean , vrai si l'essai est identique au secret
     */
    private boolean DonneScoreDuJoueur(char[] essai, char[] score, char[] secret) {
        return CalculScore(essai, score, secret);
    }

    /**
     * saisie le score de l'essai proposé par l'ordianteur
     *
     * @param essai        char [] essai à scorer
     * @param score        char []  score de l'essai q- renseigné en sortie de méthode
     * @param secret       char [] pour calcul du score
     * @param tablePM      char [][] table du jeu PlusMoins
     * @param pattern_Menu String , pattern de saisie pour la méthode Nexte de la classe Scanner
     * @param escapeChar   Character : escapeChar si saisie, le score est celui calculé automatiquement
     * @return boolean , vrai si l'essai est identique au secret
     * @throws AppExceptions : déclenché sur erreur non géré ou sur lecture de EscapeChar dans ce cas l'escape est transmis
     *                       à l'exception et est récupérable par la méthode getCharacterSortie() de AppException
     */
    private boolean DonneScoreDeLOrdi(char[] essai, char[] score, char[] secret,
                                      char[][] tablePM, String pattern_Menu,
                                      Character escapeChar) throws AppExceptions {

        //saisir score et presente resultat de CalculScore(essai, score, secret);
        //validation par escapeChar

        char[] scoretmp = new char[nombreDePositions];
        CalculScore(essai, scoretmp, secret);
        strLibelleInfos = "suggestion : " + String.valueOf(scoretmp);
        try {
            SaisirDesChars(score, secret, tablePM, pattern_Menu, escapeChar);
        } catch (AppExceptions e) {
            if (e.getCharacterSortie() == charactersEscape) {
                System.arraycopy(scoretmp, 0, score, 0, score.length);
            } else {
                logger.error(ERREUR_GENERIC + "sortie saisie score ordi anormale");
                throw new AppExceptions(ERREUR_GENERIC);
            }
        }

        int locaCount = 0;

        for (char c : score) {
            if (c == '=') locaCount++;
        }
        return locaCount == nombreDePositions;
    }

    /**
     * calcule le score de l'assai passé en parametre (sert dans tous les mode du jeu)
     * renvoi vrai si l'essai est égal au secret
     *
     * @param essai tableau de char qui contient l'essai - non modifié par la méthode
     * @param score tableau de char vide mais qui doit être alloué (renseigné avec le scorre en sortie de méthode
     * @return boolean - vrai si secret trouvé , sinon faux
     */
    private boolean CalculScore(char[] essai, char[] score, char[] secret) {
        int compteEgal = 0;
        for (int i = 0; i < essai.length; i++) {
            score[i] = (essai[i] == secret[i]) ? '=' : ((essai[i] < secret[i]) ? '-' : '+');
            compteEgal += (score[i] == '=' ? 1 : 0);
        }
        return compteEgal == nombreDePositions;
    }

    /**
     * renseigne le secret avec le nombre à trouver, sous forme de caractères
     * saisie par le Joueur     *
     *
     * @param secretaFaire char[] secret renseigné en sortie de méthode
     * @param tablePM  char [][] table du jeu pour affichage
     * @param pattern_Menu  String pattern de saisie pour la méthode Next de la classe Scanner
     * @param escapeChar  Character , caractère escape - si saisie retour au niveau d'appel ou saisie par défaut
     * @throws AppExceptions  : déclenché sur erreur non géré ou sur lecture de EscapeChar dans ce cas l'escape est transmis
     * à l'exception et est récupérable par la méthode getCharacterSortie() de AppException
     */
    private void FaitUnSecretParLeJoueur(char[] secretaFaire,
                                         char[][] tablePM, String pattern_Menu, Character escapeChar) throws AppExceptions {
        char[] vide = new char[secretaFaire.length];
        SaisirDesChars(secretaFaire, vide, tablePM, pattern_Menu, escapeChar);
    }

    /**
     * renseigne l'attibut secretJeuPM avec le nombre à trouver, sous forme de caractères
     * par calcul
     *
     * @param nbPositions  int nombre de chiffres dans la zone d'essai et dans la zone de score
     * @param boucleMax   int , nombre de tentaives de random avant abandon et choix par defaut 1,2,3,...
     * @param charsValRet   char []  tableau de chiffres tirés aléatoirement
     */
    private void FaitUnSecretParLOrdi(int nbPositions, int boucleMax, char[] charsValRet) {

        DecimalFormat df = new DecimalFormat("#");
        // "graine" pour le random.
        df.setRoundingMode(RoundingMode.HALF_UP);

        int valTmp;
        for (int limite = 0, k = 0; k < nbPositions && limite < boucleMax; limite++) {
            valTmp = ((Integer.parseInt(df.format(StrictMath.random() * 100)) % 10));
            if (valTmp > 0) {
                charsValRet[k++] = (char) (valTmp + '0');
            }
        }
        //on passe par la "case" assignation par défaut car pas réussi à fabriquer un secret
        if (charsValRet.length != nbPositions) {
            logger.info(Messages.InfosMessages.REMPLACEMENT_PAR_VALEUR_DEFAUT.toString() + " pas réussi à générer un secret ");
            for (int m = 0; m < nbPositions; m++)
                charsValRet[m] = (char) (m + '0');
        }
    }

    /**
     * affichage des lignes du jeu
     *
     * @param secret  char [] , le secret qui est affiché en mode debug
     * @param tablePM char [] [] tableau de lignes essai et score
     */
    private void AfiicheJeuPM(char[] secret, char[][] tablePM) {
        IOConsole.ClearScreen.cls();
        for (StringBuilder s : getLignesJeu(secret, tablePM)) {
            if (s.charAt(0) == '.') {
                s.replace(0, 1, "[");
                System.out.print(s);
            } else {
                System.out.println(s);
            }
        }
    }

    /**
     * joue  en mode Challengeur
     */
    public void runModeChallengeur() {

        boolean isTrouve;

        //recharge les libelles - cas ou on enchaine les mode de jeu du jeu PM
        initLibellesLignes();

        //creation du secret par calcul
        FaitUnSecretParLOrdi(nombreDePositions, nombreMaxDeBoucles, secret);

        for (int nbBoucle = 0; nbBoucle < nombreDeEssaisMax; nbBoucle++) {
            AfiicheJeuPM(secret, tablePM);

            try {
                SaisieUnEssaiJoueur(essai, secret, tablePM, "[1-9 K k]", charactersEscape);
                isTrouve = DonneScoreDuJoueur(essai, score, secret);

                strLibelleSaisie = ". Saisie -> (K : retour)    ] ";
                AjouterUnEssai(nbBoucle, essai, tablePM);
                AjouterunScore(nbBoucle, score, tablePM);

                if (isTrouve) {
                    strLibelleInfos = "[     Vous avez Gagné       ] ";
                    AfiicheJeuPM(secret, tablePM);
                    scanner.hasNext();
                    scanner.next();
                    break;
                }

            } catch (AppExceptions e) {
                if (e.getCharacterSortie() == charactersEscape)
                    return;
                logger.error(ERREUR_GENERIC);
                throw new AppExceptions(ERREUR_GENERIC);

            }
        }
    }

    /**
     * joue en mode defenseur
     */
    public void runModeDefenseur() {

        boolean isTrouve;

        modeDebug = true;
        //recharge les libelles - cas ou on enchaine les mode de jeu du jeu PM
        initLibellesLignes();

        try {
            strLibelleSaisie = ". Saisie du Scret -> (K : retour)    ] ";

            FaitUnSecretParLeJoueur(secret, tablePM, "[1-9 K k]", charactersEscape);

            strLibelleSaisie = ". Saisie -> (K : retour)    ] ";

        } catch (AppExceptions e) {
            if (e.getCharacterSortie() == charactersEscape)
                return;
            logger.error(ERREUR_GENERIC);
            throw new AppExceptions(ERREUR_GENERIC);
        }
        try {
            for (int i = 0; i < nombreDeEssaisMax; i++) {
                AfiicheJeuPM(secret, tablePM);

                CalculUnNouvelEssaiOrdi(i, essai, score, tablePM);
                //char[] essai, char[] score, char[] secret,
                //                                      char[][] tablePM, String pattern_Menu,
                //                                      Character escapeChar
                strLibelleSaisie = ". Saisie du Score  -> (K : automatique)    ] ";
                isTrouve = DonneScoreDeLOrdi(essai, score, secret, tablePM, "[+ \\- = K k]", charactersEscape);

                strLibelleSaisie = ". Saisie -> (K : retour)    ] ";
                AjouterUnEssai(i, essai, tablePM);
                AjouterunScore(i, score, tablePM);

                if (isTrouve) {
                    strLibelleInfos = "[    Ordinateur a  Gagné    ] ";
                    AfiicheJeuPM(secret, tablePM);
                    scanner.hasNext();
                    scanner.next();
                    break;
                }
            }

        } catch (AppExceptions e) {
            if (e.getCharacterSortie() == charactersEscape)
                return;
            logger.error(ERREUR_GENERIC);
            throw new AppExceptions(ERREUR_GENERIC);
        }
    }

    public void runModeDuel() {

        //recharge les libelles - cas ou on enchaine les mode de jeu du jeu PM
        initLibellesLignes();

        //creation du secret par calcul
        FaitUnSecretParLOrdi(nombreDePositions, nombreMaxDeBoucles, secret);

        for (int i = 0; i < nombreDeEssaisMax; i++) {
            CalculUnNouvelEssaiOrdi(i, essai, score, tablePM);
            strLibelleSaisie = ". Saisie du Score  -> (K : automatique)    ] ";
            if (DonneScoreDeLOrdi(essai, score, secret, tablePM, "[+ - = K k]", charactersEscape)) {
                break;
            }
            strLibelleSaisie = ". Saisie -> (K : retour)    ] ";

            SaisieUnEssaiJoueur(essai, secret, tablePM, "[1-9 K k]", charactersEscape);
            if (DonneScoreDuJoueur(essai, score, secret)) {
                break;
            }
            AjouterUnEssai(i, essai, tablePM);
            AjouterunScore(i, score, tablePM);
        }
    }
}

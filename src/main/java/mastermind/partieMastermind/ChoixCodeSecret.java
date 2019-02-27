package mastermind.partieMastermind;

import mastermind.App;
import mastermind.constantesMastermind.CouleursMastermind;
import mastermind.constantesMastermind.ValeursConstantes;
import mastermind.exceptionMastermind.ExceptionMastermind;

import mastermind.messagesTexteMastermind.ErreurMessages;
import mastermind.parametrageApplication.ParametresDuMasterMind;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static mastermind.logMastermind.logApplicatif.getInstance;
import static mastermind.parametrageApplication.GestionDesParametres.getParam;

/**
 * Création du Code Secret comprenant X positions , X étant égal au paramètre "nombreDePositions"
 * Chaque position contient une couleur représentée sous forme de nombre pris parmi le segment 0..nombreDeCouleurs-1
 * Le choix de couleur est aléatoire
 */
public class ChoixCodeSecret  {
    /**
     *
     */
    private int nombreDePositions, nombreDeCouleurs;
    private ArrayList<Byte> codeSecret ;
    private CouleursMastermind [] ligneSecrete ;


    /**
     *
     * @throws ExceptionMastermind
     */
    public ChoixCodeSecret() throws ExceptionMastermind {

        //renseigne le nom de la classe appelante , ici App.
        getInstance(ChoixCodeSecret.class.getName());

        Object tmpRetour;
        Byte valeurAleatoire;
        Boolean doublonAutorise;

        codeSecret = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        nombreDePositions = 0;
        tmpRetour=getParam(ParametresDuMasterMind.NbDePositions);
        if ( tmpRetour instanceof Short)
            nombreDePositions = (Short)tmpRetour;
        else
            throw new ExceptionMastermind(ErreurMessages.TypeParamIncorrect);


        nombreDeCouleurs =  0;
        tmpRetour=getParam(ParametresDuMasterMind.NbCouleurs);
        if ( tmpRetour instanceof Short)
            nombreDeCouleurs = (Short)tmpRetour;
        else
            throw new ExceptionMastermind(ErreurMessages.TypeParamIncorrect);

        tmpRetour = getParam(ParametresDuMasterMind.DoublonAutorise);
        if ( tmpRetour instanceof Boolean)
            doublonAutorise = (Boolean) tmpRetour;

        else
            throw new ExceptionMastermind(ErreurMessages.TypeParamIncorrect);

        for (int placeOccupee =0,  nbreDeBoucles=0; placeOccupee< this.nombreDePositions && nbreDeBoucles < ValeursConstantes.nombreMaxDeBoucleCherhceCodeSecret  ; nbreDeBoucles++) {
            valeurAleatoire = (byte)(Byte.parseByte(df.format(Math.random()*100)) % nombreDeCouleurs );

            if (!doublonAutorise) {
                if (!codeSecret.contains(valeurAleatoire)) {
                    codeSecret.add(valeurAleatoire);
                    placeOccupee++;
                }
            }
            else {
                codeSecret.add(valeurAleatoire);
                placeOccupee++;
            }
        }
        //pas assez de positions remplies - le random n'a pas marché
        if (codeSecret.size() < nombreDePositions) {

            CouleursMastermind[] couleursMastermind = CouleursMastermind.values();
            codeSecret.clear();
            for (int i =0; i<nombreDePositions;i++ ) {
                codeSecret.add(couleursMastermind[i].getValeurFacialeDeLaCouleur());
            }
        }

        ligneSecrete  = new CouleursMastermind[this.nombreDePositions ];
        int i=0;
        for (Byte v: codeSecret) {
            ligneSecrete[i++] = CouleursMastermind.values()[(int)v];
        }
    }

    /**
     *
     * @return ArrayList<Byte> Tableau des index des couleurs de la ligne secrète
     */
    public ArrayList<Byte> getCodeSecret() {

        return codeSecret;
    }

    /**
     *
     * @return  CouleursMastermind[] Tableau des couleurs de la ligne secrète
     */
    public CouleursMastermind[] getLigneSecrete() {

        return ligneSecrete;
    }
}


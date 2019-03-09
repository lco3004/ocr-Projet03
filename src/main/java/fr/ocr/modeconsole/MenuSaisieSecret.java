package fr.ocr.modeconsole;

import fr.ocr.params.CouleursMastermind;

import java.util.ArrayList;

import static fr.ocr.params.LireParametres.getParam;
import static fr.ocr.params.Parametres.NOMBRE_DE_POSITIONS;

/**
 * MOOK saisie clavier combinaisonSecrete secret MM
 */
public class MenuSaisieSecret {

    public ArrayList <Integer> saisirCombinaisonSecrete() {
        ArrayList<Integer> combinaisonSecrete = new ArrayList<>(256);
        int monCompteur =0;
        for (CouleursMastermind v: CouleursMastermind.values()) {
            combinaisonSecrete.add(v.getValeurFacialeDeLaCouleur());
            Integer nbPositions = (Integer) getParam(NOMBRE_DE_POSITIONS);
            if (monCompteur == nbPositions-1)
                break;
            monCompteur++;
        }
        return combinaisonSecrete;
    }
}

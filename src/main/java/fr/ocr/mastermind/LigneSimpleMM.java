package fr.ocr.mastermind;

import fr.ocr.utiles.Constantes;

public class LigneSimpleMM extends LignePlateauMM {


    private String libelleLigne;
    private String libelleLigneOriginal;

    LigneSimpleMM(boolean disponible,
                  boolean visible,
                  int rang,
                  int typeligne, String info) {

        super(disponible, visible, rang, typeligne);
        libelleLigne = info;
        libelleLigneOriginal = info;
    }

    public String getLibelleLigne() {
        return libelleLigne;
    }

    public LigneSimpleMM setLibelleLigne(String infos) {

        libelleLigne = infos;
        return this;
    }

    public String getLibelleLigneOriginal() {
        return libelleLigneOriginal;
    }

    LigneSimpleMM setLibelleLigne(Constantes.CouleursMastermind[] colMM) {
        return setLibelleLigne(colMM, colMM.length);
    }

    LigneSimpleMM setLibelleLigne(Constantes.CouleursMastermind[] colMM, int nbCouleurs) {
        return setLibelleLigne(colMM, nbCouleurs, "Les Couleurs -> ");
    }

    LigneSimpleMM setLibelleLigne(Constantes.CouleursMastermind[] colMM, int nbCouleurs, String enTete) {

        StringBuilder listeToutesCol = new StringBuilder(256);
        listeToutesCol.append(enTete);
        int couleursUtilisees = 0;
        for (Constantes.CouleursMastermind v : colMM) {
            if (couleursUtilisees < nbCouleurs) {
                listeToutesCol.append(v.getLettreInitiale());
                listeToutesCol.append(' ');
            }
            couleursUtilisees++;
        }
        libelleLigne = listeToutesCol.toString();
        return this;
    }

    public LigneSimpleMM Clear() {
        setLibelleLigne(libelleLigneOriginal);
        return this;
    }

    @Override
    public String toString() {
        return getLibelleLigne();
    }
}


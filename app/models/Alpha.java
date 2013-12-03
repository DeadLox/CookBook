package models;

/**
 * Created with IntelliJ IDEA.
 * User: cleborgne
 * Date: 01/12/13
 * Time: 14:23
 * To change this template use File | Settings | File Templates.
 */
public enum Alpha {
    A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z;

    /**
     * Compte le nombre de recette pour une lettre
     *
     * @return
     */
    public Long getNbRecette(){
        return Recette.count("byLettre", this);
    }
}

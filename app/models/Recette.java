package models;

import controllers.Secure;
import controllers.Security;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cleborgne
 * Date: 01/12/13
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Recette extends Model implements Comparable<Recette> {
    @Unique
    @Required
    public String titre;
    @Required
    public String adresse;
    @Required
    public Alpha lettre;
    @OneToOne
    public Utilisateur auteur;
    @ManyToMany
    public Set<Utilisateur> utilisateurs;
    @Required
    @Column(columnDefinition="Integer default '0'")
    public int note;
    public Date dateDeCreation;
    public Date dateDeModification;
    public int vues;

    public Recette() {
        this.vues = 0;
        this.note = 0;
        this.lettre = Alpha.A;
        this.utilisateurs = new TreeSet<Utilisateur>();
    }

    public String toString(){
        return this.titre;
    }

    /**
     * Met en surbrillance le texte recherché
     * @param recherche
     * @return
     */
    public String hightlightRecherche(String recherche){
        return this.titre.replaceAll("(?i)"+recherche, "<span class=\"label label-warning\">"+recherche+"</span>");
    }

    @Override
    public int compareTo(Recette recette) {
        return this.titre.compareTo(recette.titre);
    }
}

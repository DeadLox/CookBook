package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Comparator;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cleborgne
 * Date: 01/12/13
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Recette extends Model implements Comparator {
    @Required
    public String titre;
    @Required
    public String adresse;
    @Required
    public Alpha lettre;
    @Required
    @Column(columnDefinition="Integer default '0'")
    public int note;
    public Date dateDeCreation;
    public Date dateDeModification;

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
    public int compare(Object o, Object o2) {
        Recette recette1 = (Recette) o;
        Recette recette2 = (Recette) o2;
        if (recette1 != null && recette2 != null) {
            return compare(recette1, recette2);
        } else {
            return 0;
        }
    }
}

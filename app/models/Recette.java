package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cleborgne
 * Date: 01/12/13
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Recette extends Model {
    @Required
    public String titre;
    @Required
    public String adresse;
    @Required
    public Alpha lettre;
    @Required
    @CRUD.Hidden
    public int note;
    @CRUD.Hidden
    public Date date;

    public Recette(){
        this.note = 1;
        this.date = new Date();
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
}

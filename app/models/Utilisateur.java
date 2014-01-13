package models;

import controllers.CRUD;
import org.apache.commons.lang.RandomStringUtils;
import play.Logger;
import play.data.validation.Email;
import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by DeadLox on 05/01/2014.
 */
@Entity
public class Utilisateur extends Model {
    @Required
    @Email
    public String email;
    @Required
    @Min(6)
    public String password;
    @Required
    public String activationCode;
    @CRUD.Hidden
    public Integer etat;
    @CRUD.Hidden
    @ManyToMany
    public Set<Recette> recettes = new TreeSet<Recette>();
    @ManyToOne
    public Role role;

    /**
     * Constructeur
     * Fixe l'état à 0 soit désactivé
     * Génère un code d'activation unique
     */
    public Utilisateur(){
        this.etat = 0;
        this.activationCode = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
    }

    /**
     * Retourne une collection de recette correspondant à la lettre
     * @param selected
     * @return
     */
    public Set<Recette> recetteSelector(Alpha selected){
        Set<Recette> recettesSelected = new TreeSet<Recette>();
        for (Recette recette : this.recettes){
            if (recette.lettre.equals(selected)) {
                recettesSelected.add(recette);
            }
        }
        return recettesSelected;
    }

    /**
     * Retourne le nombre de recette en fonction d'une lettre
     * @param lettre
     * @return
     */
    public int getNbRecette(Alpha lettre){
        int nbRecette = 0;
        for (Recette recette : this.recettes) {
            if (recette.lettre == lettre) {
                nbRecette++;
            }
        }
        return nbRecette;
    }

    public String toString(){
        return this.email;
    }
}

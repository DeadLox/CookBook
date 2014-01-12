package models;

import controllers.CRUD;
import org.apache.commons.lang.RandomStringUtils;
import play.data.validation.Email;
import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Set;

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

    public String activationCode;
    @CRUD.Hidden
    public Integer etat;
    @CRUD.Hidden
    @ManyToMany
    public Set<Recette> recettes;
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

    public String toString(){
        return this.email;
    }
}

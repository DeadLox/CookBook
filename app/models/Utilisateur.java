package models;

import controllers.CRUD;
import play.data.validation.Email;
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
    public String password;
    @CRUD.Hidden
    @ManyToMany
    public Set<Recette> recettes;
    @ManyToOne
    public Role role;
}

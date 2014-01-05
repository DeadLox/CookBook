package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

/**
 * Created by DeadLox on 05/01/2014.
 */
@Entity
public class Role extends Model {
    @Required
    public String libelle;
    @Required
    public Integer level;
    @CRUD.Hidden
    @OneToMany(mappedBy="role")
    public Set<Utilisateur> utilisateurs;

    public Role(){
        this.level = 0;
    }

    public String toString(){
        return this.libelle;
    }
}

package controllers;

import models.Recette;
import models.Utilisateur;
import play.mvc.Controller;
import play.mvc.With;

import java.util.List;

/**
 * Created by DeadLox on 19/01/2014.
 */

@With(Secure.class)
@Check("Administrateur")
public class Administration extends Controller {

    public static void index(){
        render();
    }

    public static void generateList(){
        List<Recette> recettes = Recette.findAll();
        render(recettes);
    }

    public static void setPseudo(){
        List<Utilisateur> users = Utilisateur.findAll();
        for (Utilisateur user : users) {
            if (user.email.equals("dead.lox@hotmail.fr")) {
                user.pseudo = "DeadLox";
                user.save();
            }
            if (user.email.equals("mlebor@hotmail.fr")) {
                user.pseudo = "Maryse";
                user.save();
            }
            if (user.email.equals("laura-boutche9@hotmail.fr")) {
                user.pseudo = "Laura";
                user.save();
            }
        }
        flash.success("Set des Pseudo effectué");
        index();
    }
}

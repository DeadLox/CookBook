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
}

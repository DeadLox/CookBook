package controllers;

import org.apache.log4j.Logger;
import play.i18n.Messages;
import play.mvc.*;

import java.util.*;

import models.*;
import util.PaginationUtil;

@With(Secure.class)
public class Application extends Controller {
    private static Logger logger = Logger.getLogger(Application.class);
    public static Alpha alphaDefaut = Alpha.A;

    @Before
    static void updateDateLogin(){
        Utilisateur user = Security.getLoggedMember();
        if (user != null) {
            user.dateLastLogin = new Date();
            user.save();
        }
    }

    /**
     * Affiche toutes les recettes de l'utilisateur
     */
    public static void all() {
        Utilisateur user = getMembre();
        List<Recette> recettes = PaginationUtil.getPaginationAll(params);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("selected", null);
        map.put("recettes", recettes);
        map.put("total", user.recettes.size());
        renderTemplate("Application/lettre.html", map);
    }

    /**
     * Vérifie si une lettre est déjà sélectionnée en session, sinon retourne la lettre par défaut A
     * @return
     */
    private static Alpha getSelected() {
        if (session.get("selected") != null){
            return Alpha.valueOf(session.get("selected"));
        } else {
            return alphaDefaut;
        }
    }

    /**
     * Retourne l'utilisateur sélectionné
     * @return
     */
    public static Utilisateur getMembre(){
        if (session.get("user") != null) {
            return Utilisateur.findById(Long.parseLong(session.get("user")));
        } else {
            return Security.getLoggedMember();
        }
    }

    /**
     * Retourne TRUE si on est bien sur le CookBook du membre connecté
     * @return
     */
    public static boolean isMyCookBook(){
        return controllers.Security.getLoggedMember().equals(controllers.Application.getMembre());
    }

    /**
     * Affiche la liste des recettes correspondant à la lettre
     * @param lettreSelected
     */
    public static void lettre(Alpha lettreSelected){
        session.put("selected", lettreSelected);

        Alpha selected = getSelected();
        List<Recette> recettes = PaginationUtil.getPagination(params, selected);

        renderArgs.put("selected", selected);
        renderArgs.put("recettes", recettes);
        renderArgs.put("total", PaginationUtil.getPaginationCount(selected));
        render();
    }

    /**
     * Effectue une recherche
     *
     * @param recherche
     */
    public static void recherche(String recherche){
        List<Recette> recettes = PaginationUtil.getSearchPagination(params, recherche);
        long total = PaginationUtil.getSearchCount(recherche);

        Alpha selected = getSelected();

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("selected", selected);
        args.put("recettes", recettes);
        args.put("total", total);
        args.put("recherche", recherche);
        renderTemplate("Application/lettre.html", args);
    }

    /**
     * Permet d'incrémenter le compteur de vues puis de rediriger vers l'url de la recette
     * @param id
     */
    public static void showRecette(long id){
        Recette recette = Recette.findById(id);
        if (recette != null) {
            // Incrémente le compteur de vues
            recette.vues += 1;
            recette.save();
            // On redirige vers l'url de la recette
            String url = recette.adresse;
            redirect(url);
        } else {
            flash.error(Messages.get("error.redirect.recette"));
        }
    }

    /**
     * Affiche la liste des changements de versions statique
     * @return
     */
    public static void changelog(){
        render();
    }

    public static void showMembres(){
        renderArgs.put("selected", null);
        renderArgs.put("membres", PaginationUtil.getMembrePagination(params));
        // On retire 1 pour ne pas tenir compte de l'utilisateur loggé
        renderArgs.put("total", Utilisateur.count()-1);
        render();
    }

    public static void showMembre(long id){
        Utilisateur user = Utilisateur.findById(id);
        if (user != null){
            session.put("user", user.id);
            all();
        } else {
            flash.error("Cet utilisateur n'existe pas.");
        }
    }

    public static void myCookbook(){
        session.remove("user");
        all();
    }

    public static void addToMyCookBook(long id){
        Recette recette = Recette.findById(id);
        if (recette != null) {
            Utilisateur loggedMember = Security.getLoggedMember();
            if (!loggedMember.recettes.contains(recette)) {
                recette.utilisateurs.add(loggedMember);
                recette.reprise += 1;
                recette.save();
                flash.success(Messages.get("recette.cookbook.reprise.added", recette.titre, recette.auteur.email));
            } else {
                flash.error(Messages.get("recette.cookbook.already.exist"));
            }
        } else {
            flash.error(Messages.get("recette.not.exist"));
        }
        all();
    }
}
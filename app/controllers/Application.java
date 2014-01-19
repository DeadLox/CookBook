package controllers;

import play.Logger;
import play.i18n.Messages;
import play.mvc.*;

import java.util.*;

import models.*;
import util.PaginationUtil;

@With(Secure.class)
public class Application extends Controller {
    public static Alpha alphaDefaut = Alpha.A;

    /**
     * Affiche toutes les recettes de l'utilisateur
     */
    public static void all() {
        Utilisateur loggedMember = Security.getLoggedMember();
        List<Recette> recettes = PaginationUtil.getPaginationAll(params);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("selected", null);
        map.put("recettes", recettes);
        map.put("total", loggedMember.recettes.size());
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
     * Affiche la liste des recettes correspondant à la lettre
     * @param lettreSelected
     */
    public static void lettre(Alpha lettreSelected){
        session.put("selected", lettreSelected);

        Alpha selected = getSelected();
        Utilisateur loggedMember = Security.getLoggedMember();
        List<Recette> recettes = PaginationUtil.getPagination(params, selected);

        renderArgs.put("selected", selected);
        renderArgs.put("recettes", recettes);
        renderArgs.put("total", PaginationUtil.getPaginationCount(selected));
        render();
    }

    public static void supprimer(Long id){
        Recette recette = Recette.findById(id);
        for (Utilisateur user : recette.utilisateurs) {
            user.recettes.remove(recette);
            user.save();
        }
        flash.put("error", Messages.get("recette.supprimer.success", recette));
        recette.delete();
        all();
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

        renderArgs.put("selected", selected);
        renderArgs.put("recettes", recettes);
        renderArgs.put("total", total);
        renderArgs.put("recherche", recherche);

        render();
    }

    /**
     * Genère l'url avec les paramètres de pagination
     * @param page
     * @return
     */
    public static String generateUrl(int page){
        String params = request.querystring;
        if (params.equals("")) {
            params = "?page=" + page;
        } else {
            params = "?" + params + "&page=" + page;
        }
        return params;
    }

    /**
     * Affiche la liste des changements de versions statique
     * @return
     */
    public static void changelog(){
        render();
    }
}
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

    public static void index() {
        Alpha selected = getSelected();
        List<Recette> recettes = PaginationUtil.getPagination(params, selected);
        long total = PaginationUtil.getTotalPagination(params, selected);

        renderArgs.put("selected", selected);
        renderArgs.put("recettes", recettes);
        renderArgs.put("total", total);
        render();
    }

    public static void all() {
        List<Recette> recettes = PaginationUtil.getPaginationAll(params);
        long total = Recette.count();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("selected", null);
        map.put("recettes", recettes);
        map.put("total", total);
        renderTemplate("Application/index.html", map);
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
     * Sélectionne une lettre
     * @param lettreSelected
     */
    public static void lettre(Alpha lettreSelected){
        session.put("selected", lettreSelected);
        index();
    }

    public static void supprimer(Long id){
        Recette recette = Recette.findById(id);
        flash.put("success", Messages.get("recette.supprimer.success", recette));
        recette.delete();
        index();
    }

    /**
     * Effectue une recherche
     *
     * @param recherche
     */
    public static void recherche(String recherche){
        List<Recette> recettes = PaginationUtil.getSearchPagination(params, recherche);
        long total = PaginationUtil.getSearchCount(params, recherche);

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
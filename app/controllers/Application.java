package controllers;

import play.i18n.Messages;
import play.mvc.*;

import java.util.*;

import models.*;
import util.PaginationUtil;

@With(Secure.class)
public class Application extends Controller {
    public static Alpha selected = Alpha.A;

    public static void index() {
        List<Alpha> alphas = Arrays.asList(Alpha.values());
        List<Recette> recettes = PaginationUtil.getPagination(params, selected);
        long total = PaginationUtil.getTotalPagination(params, selected);

        renderArgs.put("alphas", alphas);
        renderArgs.put("selected", selected);
        renderArgs.put("recettes", recettes);
        renderArgs.put("total", total);
        render();
    }

    public static void all() {
        List<Alpha> alphas = Arrays.asList(Alpha.values());
        List<Recette> recettes = PaginationUtil.getPaginationAll(params);
        long total = Recette.count();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alphas", alphas);
        map.put("selected", null);
        map.put("recettes", recettes);
        map.put("total", total);
        renderTemplate("Application/index.html", map);
    }

    /**
     * Sélectionne une lettre
     * @param lettreSelected
     */
    public static void lettre(Alpha lettreSelected){
        selected = lettreSelected;
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
        Recette object = new Recette();

        List<Alpha> alphas = Arrays.asList(Alpha.values());

        renderArgs.put("alphas", alphas);
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
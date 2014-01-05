package util;

import models.Alpha;
import models.Recette;
import play.mvc.Controller;
import play.mvc.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cleborgne on 19/12/13.
 */
public class PaginationUtil extends Controller {
    public static int MAXPERPAGE = 10;
    public static int PAGE = 1;
    public static List<Integer> listMaxPerPage = Arrays.asList(10,20,50);

    private static int getPage(Scope.Params params){
        return (params.get("page") != null)? Integer.parseInt(params.get("page")) : PAGE;
    }

    public static int getMaxPerPage(Scope.Params params){
        int max =  MAXPERPAGE;
        int maxParam = 0;
        if (params.get("max") != null) {
            maxParam = Integer.parseInt(params.get("max"));
            session.put("max", maxParam);
        }
        if (session.get("max") != null) {
            max = Integer.parseInt(session.get("max"));
            if (maxParam > 0 && maxParam != max) {
                session.put("max", maxParam);
            }
        }
        return max;
    }

    /**
     * Retourne une liste de recette avec la pagination
     * @return
     */
    public static List<Recette> getPagination(Scope.Params params, Alpha selected){
        int page = getPage(params);
        int max = getMaxPerPage(params);
        return Recette.find("lettre = ? ORDER BY titre ASC", selected).fetch(page, max);
    }

    /**
     * Retourne la liste des recettes recherchées avec pagination
     * @param params
     * @param recherche
     * @return
     */
    public static List<Recette> getSearchPagination(Scope.Params params, String recherche){
        int page = getPage(params);
        int max = getMaxPerPage(params);
        return Recette.find("titre = ? ORDER BY titre ASC", "%"+recherche+"%").fetch(page, max);
    }

    /**
     * Retourne le nombre total de recettes trouvées lors d'une recherche
     * @param params
     * @param recherche
     * @return
     */
    public static long getSearchCount(Scope.Params params, String recherche){
        int page = getPage(params);
        int max = getMaxPerPage(params);
        return Recette.count("byTitreLike", "%" + recherche + "%");
    }

    /**
     * Retourne le nombre total d'élément pour la pagination
     * @return
     */
    public static long getTotalPagination(Scope.Params params, Alpha selected){
        return Recette.count("byLettre", selected);
    }

    /**
     * Retourne la liste de toutes les recettes avec la pagination
     * @return
     */
    public static List<Recette> getPaginationAll(Scope.Params params){
        int page = getPage(params);
        int max = getMaxPerPage(params);
        return Recette.find("ORDER BY titre ASC").fetch(page, max);
    }
}

package util;

import models.Alpha;
import models.Recette;
import play.mvc.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cleborgne on 19/12/13.
 */
public class PaginationUtil {
    public static int MAXPERPAGE = 2;
    public static int PAGE = 1;
    public static List<Integer> listMaxPerPage = Arrays.asList(5,10,20,50);

    private static int getPage(Scope.Params params){
        return (params.get("page") != null)? Integer.parseInt(params.get("page")) : PAGE;
    }

    private static int getMaxPerPage(Scope.Params params){
        return (params.get("max") != null)? Integer.parseInt(params.get("max")) : MAXPERPAGE;
    }

    /**
     * Retourne une liste de recette avec la pagination
     * @return
     */
    public static List<Recette> getPagination(Scope.Params params, Alpha selected){
        int page = getPage(params);
        int max = getMaxPerPage(params);
        System.out.println(params.get("page"));
        return Recette.find("byLettre", selected).fetch(page, max);
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
        return Recette.find("").fetch(page, max);
    }
}

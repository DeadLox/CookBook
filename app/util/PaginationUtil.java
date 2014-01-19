package util;

import controllers.Security;
import models.Alpha;
import models.Recette;
import models.Utilisateur;
import org.apache.log4j.Logger;
import play.mvc.Controller;
import play.mvc.Scope;

import java.util.*;

/**
 * Created by cleborgne on 19/12/13.
 */
public class PaginationUtil extends Controller {
    private static Logger logger = Logger.getLogger(PaginationUtil.class);
    public static int MAXPERPAGE = 5;
    public static int PAGE = 1;
    public static List<Integer> listMaxPerPage = Arrays.asList(5,10,20,50);

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
        Utilisateur loggedMember = Security.getLoggedMember();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE r.lettre = ? AND ? IN u ORDER BY titre ASC", selected, loggedMember).fetch(page, max);
    }

    /**
     * Retourne une liste de recette avec la pagination
     * @return
     */
    public static long getPaginationCount(Alpha selected){
        Utilisateur loggedMember = Security.getLoggedMember();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE r.lettre = ? AND ? IN u", selected, loggedMember).fetch().size();
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
        Utilisateur loggedMember = Security.getLoggedMember();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE lower(r.titre) LIKE ? AND ? IN u ORDER BY titre ASC", '%'+recherche.toLowerCase()+'%', loggedMember).fetch(page, max);
    }

    /**
     * Retourne le nombre total de recettes trouvées lors d'une recherche
     * @param recherche
     * @return
     */
    public static long getSearchCount(String recherche){
        Utilisateur loggedMember = Security.getLoggedMember();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE lower(r.titre) LIKE ? AND ? IN u ORDER BY titre ASC", '%'+recherche.toLowerCase()+'%', loggedMember).fetch().size();
    }

    /**
     * Retourne le nombre total d'élément pour la pagination
     * @return
     */
    public static long getTotalPagination(Scope.Params params, Alpha selected){
        Utilisateur loggedMember = Security.getLoggedMember();
        return Recette.count("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE lettre = ? AND ? IN u ORDER BY titre ASC", selected, loggedMember);
    }

    /**
     * Retourne la liste de toutes les recettes avec la pagination
     * @return
     */
    public static List<Recette> getPaginationAll(Scope.Params params){
        int page = getPage(params);
        int max = getMaxPerPage(params);
        Utilisateur loggedMember = Security.getLoggedMember();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE ? IN u ORDER BY titre ASC", loggedMember).fetch(page, max);
    }
}

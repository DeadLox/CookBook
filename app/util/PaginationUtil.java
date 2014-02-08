package util;

import controllers.Application;
import controllers.Security;
import models.Alpha;
import models.Recette;
import models.Utilisateur;
import org.apache.log4j.Logger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Scope;

import java.util.*;

/**
 * Created by cleborgne on 19/12/13.
 */
public class PaginationUtil extends Controller {
    private static Logger logger = Logger.getLogger(PaginationUtil.class);
    public static int PROXIMITE = 2;
    public static int PROXIMITE_SELECTED = 1;
    public static int MAX_DEFAUT = 5;
    public static int PAGE = 1;
    public static List<Integer> listMaxPerPage = Arrays.asList(1,5,10,20,50);
    public static String TRI_DEFAUT = "titre";
    public static List<String> listTri = Arrays.asList("titre", "dateDeCreation", "dateDeModification", "vues", "auteur");
    public static String ORDER_ASC = "ASC";
    public static String ORDER_DESC = "DESC";
    public static List<String> listOrder = Arrays.asList(ORDER_ASC, ORDER_DESC);
    public static String TRI_MEMBRE_DEFAUT = "pseudo";
    public static List<String> listTriMembre = Arrays.asList("pseudo", "role");

    private static int getPage(Scope.Params params){
        return (params.get("page") != null)? Integer.parseInt(params.get("page")) : PAGE;
    }

    /**
     * Retourne le maximum par page de sélectionné ou retourne la valeur par défaut
     * @param params
     * @return
     */
    public static int getMaxPerPage(Scope.Params params){
        int max =  MAX_DEFAUT;
        int maxParam = 0;
        if (params.get("max") != null) {
            try {
            maxParam = Integer.parseInt(params.get("max"));
            session.put("max", maxParam);
            } catch (NumberFormatException e) {
                flash.error(Messages.get("pagination.max.format.exception", params.get("max")));
                max = MAX_DEFAUT;
            }
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
     * Récupère le tri sélectionné ou retourne le tri par défaut
     * @param params
     * @return
     */
    public static String getTri(Scope.Params params){
        String tri = TRI_DEFAUT;
        if (params.get("tri") != null) {
            tri = params.get("tri");
            session.put("tri", tri);
        }
        if (session.get("tri") != null) {
            tri = session.get("tri");
            if (listTriMembre.contains(tri)) {
                tri = TRI_DEFAUT;
            }
        }
        if (!listTri.contains(tri)) {
            flash.error(Messages.get("pagination.tri.not.found", tri));
            tri = TRI_DEFAUT;
        }
        return tri;
    }

    /**
     * Récupère le tri sélectionné ou retourne le tri par défaut
     * @param params
     * @return
     */
    public static String getTriMembre(Scope.Params params){
        String tri = TRI_MEMBRE_DEFAUT;
        if (params.get("tri") != null) {
            tri = params.get("tri");
            session.put("tri", tri);
        }
        if (session.get("tri") != null) {
            tri = session.get("tri");
            if (listTri.contains(tri)) {
                tri = TRI_MEMBRE_DEFAUT;
            }
        }
        if (!listTriMembre.contains(tri)) {
            flash.error(Messages.get("pagination.tri.not.found", tri));
            tri = TRI_MEMBRE_DEFAUT;
        }
        return tri;
    }

    public static String getOrder(Scope.Params params){
        String order = ORDER_ASC;
        if (params.get("ordre") != null) {
            order = params.get("ordre");
            session.put("ordre", order);
        }
        if (session.get("ordre") != null) {
            order = session.get("ordre");
        }
        if (!order.equals(ORDER_ASC) && !order.equals(ORDER_DESC)) {
            flash.error(Messages.get("pagination.order.not.found", order));
            order = ORDER_ASC;
        }
        return order;
    }

    /**
     * Retourne une liste de recette avec la pagination
     * @return
     */
    public static List<Recette> getPagination(Scope.Params params, Alpha selected){
        int page = getPage(params);
        int max = getMaxPerPage(params);
        String tri = getTri(params);
        String order = getOrder(params);
        Utilisateur user = Application.getMembre();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE r.lettre = ? AND ? IN u ORDER BY " + tri + " " + order, selected, user).fetch(page, max);
    }

    /**
     * Retourne une liste de recette avec la pagination
     * @return
     */
    public static long getPaginationCount(Alpha selected){
        Utilisateur user = Application.getMembre();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE r.lettre = ? AND ? IN u", selected, user).fetch().size();
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
        String tri = "r."+getTri(params);
        String order = getOrder(params);
        Utilisateur user = Application.getMembre();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE lower(r.titre) LIKE ? AND ? IN u ORDER BY " + tri + " " + order, '%'+recherche.toLowerCase()+'%', user).fetch(page, max);
    }

    /**
     * Retourne le nombre total de recettes trouvées lors d'une recherche
     * @param recherche
     * @return
     */
    public static long getSearchCount(String recherche){
        Utilisateur user = Application.getMembre();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE lower(r.titre) LIKE ? AND ? IN u", '%'+recherche.toLowerCase()+'%', user).fetch().size();
    }

    /**
     * Retourne le nombre total d'élément pour la pagination
     * @return
     */
    public static long getTotalPagination(Scope.Params params, Alpha selected){
        Utilisateur user = Application.getMembre();
        return Recette.count("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE lettre = ? AND ? IN u", selected, user);
    }

    /**
     * Retourne la liste de toutes les recettes avec la pagination
     * @return
     */
    public static List<Recette> getPaginationAll(Scope.Params params){
        int page = getPage(params);
        int max = getMaxPerPage(params);
        String tri = getTri(params);
        String order = getOrder(params);
        Utilisateur user = Application.getMembre();
        return Recette.find("SELECT r FROM Recette AS r INNER JOIN r.utilisateurs AS u WHERE ? IN u ORDER BY " + tri + " " + order, user).fetch(page, max);
    }

    /**
     * Retourne la liste des utilisateurs avec la pagination
     * @param params
     * @return
     */
    public static List<Utilisateur> getMembrePagination(Scope.Params params){
        int page = getPage(params);
        int max = getMaxPerPage(params);
        String tri = getTriMembre(params);
        String order = getOrder(params);
        Utilisateur user = Security.getLoggedMember();
        return Utilisateur.find("email != ? ORDER BY " + tri + " " + order, user.email).fetch(page, max);
    }
}

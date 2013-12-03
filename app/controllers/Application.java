package controllers;

import play.*;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.*;

import java.lang.reflect.Constructor;
import java.util.*;

import models.*;

public class Application extends Controller {
    private static Alpha selected = Alpha.A;
    private static CRUD.ObjectType type = CRUD.ObjectType.get(Recettes.class);

    public static void index() {
        Recette object = new Recette();

        List<Alpha> alphas = Arrays.asList(Alpha.values());
        List<Recette> recettes = Recette.find("byLettre", selected).fetch();

        renderArgs.put("alphas", alphas);
        renderArgs.put("selected", selected);
        renderArgs.put("recettes", recettes);
        renderArgs.put("type", type);
        renderArgs.put("object", object);
        render();
    }

    public static void all() {
        Recette object = new Recette();

        List<Alpha> alphas = Arrays.asList(Alpha.values());
        List<Recette> recettes = Recette.findAll();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alphas", alphas);
        map.put("selected", null);
        map.put("recettes", recettes);
        map.put("type", type);
        map.put("object", object);
        map.put("all", true);
        renderTemplate("Application/index.html", map);
    }

    public static void lettre(Alpha lettreSelected){
        selected = lettreSelected;
        index();
    }

    public static void ajouter() throws Exception {
        List<Alpha> alphas = Arrays.asList(Alpha.values());
        List<Recette> recettes = Recette.find("byLettre", selected).fetch();

        notFoundIfNull(type);
        Recette object = new Recette();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            flash.put("error", play.i18n.Messages.get("crud.hasErrors"));

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("alphas", alphas);
            map.put("selected", selected);
            map.put("recettes", recettes);
            map.put("type", type);
            map.put("object", object);
            renderTemplate("Application/index.html", map);
        }
        object._save();
        flash.success(play.i18n.Messages.get("recette.add", type.modelName));
        redirect("Application.index");
    }

    public static void modifier(Long id){
        Recette recette = Recette.findById(id);
        List<Alpha> alphas = Arrays.asList(Alpha.values());
        List<Recette> recettes = Recette.find("byLettre", selected).fetch();

        Recette object = recette;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alphas", alphas);
        map.put("selected", selected);
        map.put("recettes", recettes);
        map.put("type", type);
        map.put("object", object);
        renderTemplate("Application/index.html", map);
    }

    public static void save(Long id){
        List<Alpha> alphas = Arrays.asList(Alpha.values());
        List<Recette> recettes = Recette.find("byLettre", selected).fetch();

        Recette object = Recette.findById(id);
        Binder.bindBean(params.getRootParamNode(), "object", object);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alphas", alphas);
        map.put("selected", selected);
        map.put("recettes", recettes);
        map.put("type", type);
        map.put("object", object);

        validation.valid(object);
        if (validation.hasErrors()) {
            flash.put("error", play.i18n.Messages.get("crud.hasErrors"));
        }
        object._save();
        flash.success(play.i18n.Messages.get("recette.edit", type.modelName));
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
        List<Recette> recettes = Recette.find("byTitreLike", "%"+recherche+"%").fetch();
        Recette object = new Recette();

        List<Alpha> alphas = Arrays.asList(Alpha.values());

        renderArgs.put("alphas", alphas);
        renderArgs.put("selected", selected);
        renderArgs.put("recettes", recettes);
        renderArgs.put("type", type);
        renderArgs.put("object", object);
        renderArgs.put("recherche", recherche);

        render();
    }
}
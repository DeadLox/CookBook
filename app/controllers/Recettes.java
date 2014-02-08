package controllers;

import models.Alpha;
import models.Recette;
import models.Utilisateur;
import play.Logger;
import play.data.binding.Binder;
import play.data.validation.*;
import play.data.validation.Error;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.With;
import sun.print.resources.serviceui_zh_CN;
import util.PaginationUtil;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cleborgne
 * Date: 01/12/13
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
@With(Secure.class)
public class Recettes extends CRUD {
    private static CRUD.ObjectType type = CRUD.ObjectType.get(Recettes.class);

    public static void blank() throws Exception {
        notFoundIfNull(type);
        Recette object = new Recette();
        render("Recettes/blank.html", type, object);
    }

    public static void create() throws Exception {
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Recette object = (Recette) constructor.newInstance();
        Utilisateur loggedMember = Security.getLoggedMember();
        object.auteur = loggedMember;
        object.utilisateurs.add(loggedMember);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            flash.error(play.i18n.Messages.get("crud.hasErrors"));
            render("Recettes/blank.html", type, object);
        }
        object.dateDeCreation = new Date();
        object.dateDeModification = object.dateDeCreation;
        object.save();
        flash.success(play.i18n.Messages.get("recette.add", object.titre));
        Application.all();
    }

    public static void show(long id){
        Recette object = Recette.findById(id);
        render("Recettes/show.html", type, object);
    }

    public static void save(long id){
        Recette object = Recette.findById(id);
        Binder.bindBean(params.getRootParamNode(), "object", object);

        validation.valid(object);
        if (validation.hasErrors()) {
            flash.error(play.i18n.Messages.get("crud.hasErrors"));
            render("Recettes/show.html", type, object);
        }
        object.dateDeModification = new Date();
        object._save();
        flash.success(play.i18n.Messages.get("recette.edit", type.modelName));
        Application.all();
    }

    public static void supprimer(long id) {
        Recette recette = Recette.findById(id);
        if (recette != null) {
            Utilisateur user = Security.getLoggedMember();
            if (recette.utilisateurs.contains(user)) {
                // Si aucun n'autre utilisateur possède la recette, on la supprime
                if (recette.utilisateurs.size() == 1) {
                    recette._delete();
                // Sinon on retire juste cette utilisateur
                } else {
                    recette.utilisateurs.remove(user);
                    recette.save();
                }
                flash.success(Messages.get("crud.deleted", type.modelName));
            } else {
                flash.error(Messages.get("crud.recette.delete.non.possede"));
            }
        } else {
            flash.error(Messages.get("crud.recette.not.exist"));
        }
        Application.all();
    }

    @Check("Administrateur")
    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }
}
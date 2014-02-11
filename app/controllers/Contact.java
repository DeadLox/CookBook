package controllers;

import models.Utilisateur;
import notifiers.Mails;
import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Controller;

/**
 * Created by DeadLox on 11/02/2014.
 */
public class Contact extends Controller {
    private static int minLength = 3;

    public static void contact(){
        render();
    }

    /**
     * Méthode pour la soumission du formulaire de contact
     */
    public static void send(String email, String sujet, String message){
        if (email == null || email.equals("")) {
            flash.put("error", Messages.get("contact.email.empty"));
            contact();
        } else {
            validation.email(email);
            if (validation.hasErrors()) {
                flash.put("error", Messages.get("contact.email.not.valid"));
                contact();
            }
        }
        if (sujet == null || sujet.equals("")) {
            flash.put("error", Messages.get("contact.sujet.empty"));
            contact();
        } else {
            if (sujet.length() < minLength) {
                flash.put("error", Messages.get("contact.sujet.too.small", minLength));
                contact();
            }
        }
        if (message == null || message.equals("")) {
            flash.put("error", Messages.get("contact.message.empty"));
            contact();
        } else {
            if (message.length() < minLength) {
                flash.put("error", Messages.get("contact.message.too.small", minLength));
                contact();
            }
        }
        Utilisateur user = Security.getLoggedMember();
        // Envoie du mail de contact
        Mails.contact(user, email, sujet, message);
        flash.put("success", Messages.get("contact.email.success"));
        contact();
    }
}

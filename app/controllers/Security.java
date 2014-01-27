package controllers;

import models.Role;
import models.Utilisateur;
import notifiers.Mails;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Mail;
import play.mvc.Router;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: DeadLox
 * Date: 14/10/13
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
public class Security extends Secure.Security {
    private static Logger logger = Logger.getLogger(Security.class);
    private static String emailFrom = "dead.lox@hotmail.fr";

    /**
     * Inscription
     * @param email
     * @param password
     * @throws Throwable
     */
    public static void register(@Required String email, @Required String password) throws Throwable {
        if (email != null && password != null) {
            boolean error = false;
            // Vérifie l'email et le password
            if (!email.equals("")) {
                // Si l'email existe déjà
                if (Utilisateur.count("byEmail", email) > 0) {
                    error = true;
                    flash.put("error", Messages.get("register.email.exist"));
                } else {
                    Validation.ValidationResult result = validation.email(email);
                    if (result.ok) {
                        if (password.equals("") || password.length() < 6) {
                            error = true;
                            flash.put("error", Messages.get("register.password.valid"));
                        }
                    } else {
                        error = true;
                        flash.put("error", Messages.get("register.email.valid"));
                    }
                }
            } else {
                error = true;
                flash.put("error", Messages.get("register.email.empty"));
            }
            if (error) {
                render();
            }
            // Récupère le rôle Membre
            Role membreRole = Role.find("byLibelle", "Membre").first();

            // On créé l'utilisateur
            Utilisateur user = new Utilisateur();
            user.email = email;
            user.password = Codec.hexMD5(password);
            user.role = membreRole;
            user.save();

            // Envoi l'email d'activation du compte
            sendActivationMail(user);
            Mails.activation(user);

            flash.put("success", Messages.get("register.create"));
            Secure.login();
        }
        render();
    }

    /**
     * Envoi du mail d'activation
     * @param user
     */
    private static void sendActivationMail(Utilisateur user){
        try {
            SimpleEmail email = new SimpleEmail();
            email.setFrom(emailFrom);
            email.addTo(user.email);
            email.setSubject(Messages.get("register.email.subject"));
            email.setMsg(Messages.get("register.email.message", generateActivationLink(user)));
            Mail.send(email);
            logger.info(generateActivationLink(user));
        } catch (EmailException e) {
            logger.warn("L'email n'a pas pu être envoyé");
        }
    }

    /**
     * Retourne le lien vers la page d'activation
     * @param user
     * @return
     */
    public static String generateActivationLink(Utilisateur user){
        Map<String, Object> routeArgs = new HashMap<String, Object>();
        routeArgs.put("email", user.email);
        routeArgs.put("code", user.activationCode);
        return Router.getFullUrl("Security.activation", routeArgs);
    }

    /**
     * Login
     *
     * @param email
     * @param password
     * @return Boolean
     */
    static boolean authenticate(String email, String password) {
        // Encode le mot de passe en MD5
        password = Codec.hexMD5(password);
        Utilisateur user = Utilisateur.find("email = ? AND password = ?", email, password).first();
        if (user != null) {
            if (user.etat == 0) {
                flash.put("alert", Messages.get("application.account.not.activate"));
                return false;
            } else {
                return user.password.equals(password);
            }
        } else {
            return false;
        }
    }

    /**
     * Vérifie si l'utilisateur possède le rôle
     *
     * @param profile
     * @return Boolean
     */
    public static boolean check(String profile) {
        Utilisateur user = Utilisateur.find("byEmail", connected()).first();
        if (user.role.libelle.equalsIgnoreCase(profile)) {
            return true;
        }
        return false;
    }

    /**
     * Retourne l'utilisateur connecté
     * @return Utilisateur
     */
    public static Utilisateur getLoggedMember(){
        return Utilisateur.find("byEmail", connected()).first();
    }

    /**
     * Si le code d'activation est le bon, on active le compte
     */
    public static void activation(String email, String code){
        Utilisateur user = Utilisateur.find("byEmail", email).first();
        if (user != null) {
            if (user.etat == 0) {
                if (code.equals(user.activationCode)) {
                    // On active le compte
                    user.etat = 1;
                    user.dateActivation = new Date();
                    user.save();
                    flash.put("success", Messages.get("application.activation.success"));
                } else {
                    flash.put("error", Messages.get("application.activation.wrong.code"));
                }
            } else {
                flash.put("error", Messages.get("application.activation.account.already.activate"));
            }
        } else {
            flash.put("error", Messages.get("application.activation.no.account", email));
        }
        render();
    }
}

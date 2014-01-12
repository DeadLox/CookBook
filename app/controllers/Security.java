package controllers;

import models.Utilisateur;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Codec;

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
        logger.info(email +" "+ password);
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
            // On créé l'utilisateur
            Utilisateur user = new Utilisateur();
            user.email = email;
            user.password = password;
            user.save();

            sendActivationMail(user);

            flash.put("success", Messages.get("register.create"));
            Secure.login();
        }
        render();
    }

    public static void sendActivationMail(Utilisateur user){
        try {
            SimpleEmail email = new SimpleEmail();
            email.setFrom(emailFrom);
            email.addTo(user.email);
            email.setSubject(Messages.get("register.email.subject"));
            email.setMsg(Messages.get("register.email.message"));
            //Mail.send(email);
        } catch (EmailException e) {
            logger.warn("L'email n'a pas pu être envoyé");
        }
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
    static boolean check(String profile) {
        Utilisateur user = Utilisateur.find("byEmail", connected()).first();
        if (user.role.equals(profile)) {
            return true;
        }
        return false;
    }

    /**
     * Retourne l'utilisateur connecté
     * @return Utilisateur
     */
    static Utilisateur getLoggedMember(){
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
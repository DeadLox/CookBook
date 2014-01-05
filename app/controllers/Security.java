package controllers;

import models.Utilisateur;
import org.apache.log4j.Logger;
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
        return user != null && user.password.equals(password);
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
}

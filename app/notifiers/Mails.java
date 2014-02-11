package notifiers;

import controllers.Security;
import models.Utilisateur;
import play.i18n.Messages;
import play.mvc.Mailer;

/**
 * Created by DeadLox on 23/01/2014.
 */
public class Mails extends Mailer {
    private static String emailFrom = "dead.lox@hotmail.fr";
    private static String emailAdmin = "dead.lox@hotmail.fr";

    /**
     * Mail d'activation du compte
     * @param user
     */
    public static void activation(Utilisateur user){
        setFrom(emailFrom);
        addRecipient(user.email);
        setSubject(Messages.get("register.email.subject"));
        String activationLink = Security.generateActivationLink(user);
        send(user, activationLink);
    }

    /**
     * Mail de contact
     * @param user
     * @param email
     * @param sujet
     * @param message
     */
    public static void contact(Utilisateur user, String email, String sujet, String message){
        setFrom(email);
        addRecipient(emailAdmin);
        setSubject(sujet);
        send(user, message);
    }
}

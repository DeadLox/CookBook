package fixtures;

import models.Recette;
import models.Role;
import models.Utilisateur;
import org.apache.log4j.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: DeadLox
 * Date: 16/10/13
 * Time: 20:17
 * To change this template use File | Settings | File Templates.
 */
@OnApplicationStart
public class Bootstrap extends Job {
    private static Logger logger = Logger.getLogger(Bootstrap.class);

    public void doJob() {
        if ("".equals(Play.id) && Recette.count() == 0) {
            logger.warn("Load recetttes");
            Fixtures.loadModels("init-recette.yml");
        }
        if (Role.count() == 0) {
            logger.warn("Load Rôles");
            //Fixtures.loadModels("init-role.yml");
        }
        if (Utilisateur.count() == 0) {
            logger.warn("Load Utilisateurs");
            Fixtures.loadModels("init-user.yml");
            //Fixtures.deleteDatabase();
            Utilisateur maryse = Utilisateur.find("byEmail", "mlebor@hotmail.fr").first();
            if (maryse != null) {
                List<Recette> recettes = Recette.findAll();
                for (Recette recette : recettes) {
                    // On met maryse en Auteur
                    recette.auteur = maryse;
                    recette.utilisateurs.add(maryse);
                    recette.save();
                }
            }
        }
    }
}

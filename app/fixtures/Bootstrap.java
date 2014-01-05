package fixtures;

import models.Role;
import models.Utilisateur;
import org.apache.log4j.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

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
        if (Role.count() == 0) {
            logger.warn("Load Rôles");
            Fixtures.loadModels("init-role.yml");
        }
        if (Utilisateur.count() == 0) {
            logger.warn("Load Utilisateurs");
            Fixtures.loadModels("init-user.yml");
        }
    }
}

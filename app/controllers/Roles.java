package controllers;

import models.Role;
import play.mvc.With;

/**
 * Created by DeadLox on 19/01/2014.
 */

@With(Secure.class)
@Check("Administrateur")
public class Roles extends MyCRUD {
}

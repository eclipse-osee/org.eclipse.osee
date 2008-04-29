/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.user;

import org.eclipse.osee.framework.skynet.core.OseeCoreException;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class UserNotInDatabase extends OseeCoreException {
   private static final long serialVersionUID = 1L;

   /**
    * @param message
    */
   public UserNotInDatabase(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public UserNotInDatabase(String message, Throwable cause) {
      super(message, cause);
   }
}
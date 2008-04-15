/*
 * Created on Apr 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.user;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class UserNotInDatabase extends Exception {
   private static final long serialVersionUID = 1L;

   /**
    * @param message
    */
   public UserNotInDatabase(String message) {
      super(message);
   }
}
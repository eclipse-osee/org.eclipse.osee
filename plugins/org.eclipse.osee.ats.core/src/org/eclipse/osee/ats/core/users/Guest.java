/*
 * Created on Mar 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.users;

/**
 * @author Donald G. Dunne
 */
public class Guest extends AbstractAtsUser {

   public static Guest instance = new Guest();

   private Guest() {
      super("99999998");
   }

   @Override
   public String getName() {
      return "Guest";
   }

   @Override
   public String getGuid() {
      return "AAABDi35uzwAxJLISLBZdA";
   }

   @Override
   public String getHumanReadableId() {
      return "TBRQV";
   }

   @Override
   public boolean isActive() {
      return true;
   }

}

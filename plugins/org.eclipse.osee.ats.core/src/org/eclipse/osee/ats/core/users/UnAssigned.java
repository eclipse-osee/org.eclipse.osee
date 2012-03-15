/*
 * Created on Mar 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.users;

/**
 * @author Donald G. Dunne
 */
public class UnAssigned extends AbstractAtsUser {

   public static UnAssigned instance = new UnAssigned();

   private UnAssigned() {
      super("99999997");
   }

   @Override
   public String getName() {
      return "UnAssigned";
   }

   @Override
   public String getGuid() {
      return "AAABDi1tMx8Al92YWMjeRw";
   }

   @Override
   public String getHumanReadableId() {
      return "7G020";
   }

   @Override
   public boolean isActive() {
      return true;
   }

}

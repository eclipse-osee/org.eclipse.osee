/*
 * Created on Mar 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.users;

/**
 * @author Donald G. Dunne
 */
public class SystemUser extends AbstractAtsUser {

   public static SystemUser instance = new SystemUser();

   private SystemUser() {
      super("99999999");
   }

   @Override
   public String getName() {
      return "OSEE System";
   }

   @Override
   public String getGuid() {
      return "AAABDBYPet4AGJyrc9dY1w";
   }

   @Override
   public String getDescription() {
      return "System User";
   }

   @Override
   public String getHumanReadableId() {
      return "FTNT9";
   }

   @Override
   public boolean isActive() {
      return true;
   }

}

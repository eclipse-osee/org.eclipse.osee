/*
 * Created on Dec 19, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

/**
 * @author Donald G. Dunne
 */
public class RemoteRenameBranchEvent extends RemoteBranchEvent {

   private final String branchName;
   private final String shortName;

   /**
    * @param sender
    * @param branchId
    */
   public RemoteRenameBranchEvent(Object sender, int branchId, String branchName, String shortName) {
      super(sender, branchId);
      this.branchName = branchName;
      this.shortName = shortName;
   }

   /**
    * @return the branchName
    */
   public String getBranchName() {
      return branchName;
   }

   /**
    * @return the shortName
    */
   public String getShortName() {
      return shortName;
   }

}

/*
 * Created on Dec 19, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

/**
 * @author Donald G. Dunne
 */
public class LocalCommitBranchEvent extends LocalBranchEvent {

   /**
    * @param sender
    * @param branchId
    */
   public LocalCommitBranchEvent(Object sender, int branchId) {
      super(sender, branchId);
   }

}

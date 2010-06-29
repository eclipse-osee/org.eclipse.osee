/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2.filter;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author Donald G. Dunne
 */
public class BranchGuidEventFilter implements IEventFilter {

   private final IOseeBranch branchToken;

   /**
    * Provide branch of events that should be passed through. All other branches will be ignored.
    */
   public BranchGuidEventFilter(IOseeBranch branchToken) {
      this.branchToken = branchToken;
   }

   public boolean isMatch(String branchGuid) {
      if (branchToken != null) {
         if (branchToken.getGuid().equals(branchGuid)) return true;
      }
      return false;
   }

   @Override
   public boolean isMatch(IBasicGuidArtifact guidArt) {
      return branchToken.getGuid().equals(guidArt.getBranchGuid());
   }

   @Override
   public boolean isMatch(IBasicGuidRelation relArt) {
      return branchToken.getGuid().equals(relArt.getArtA().getBranchGuid()) ||
      //
      branchToken.getGuid().equals(relArt.getArtB().getBranchGuid());
   }

}

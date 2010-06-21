/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2.filter;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Donald G. Dunne
 */
public class BranchGuidEventFilter implements IEventFilter {

   private Collection<String> branchGuids;
   private final IOseeBranch branchToken;

   public BranchGuidEventFilter(String branchGuid) {
      this.branchGuids = Arrays.asList(branchGuid);
      this.branchToken = null;
   }

   public BranchGuidEventFilter(Collection<String> branchGuids) {
      this.branchGuids = branchGuids;
      this.branchToken = null;
   }

   public BranchGuidEventFilter(IOseeBranch branchToken) {
      this.branchToken = branchToken;
   }

   public BranchGuidEventFilter(Branch branch) {
      this(branch.getGuid());
   }

   public boolean isFiltered(String branchGuid) {
      if (branchGuids != null) {
         if (this.branchGuids.contains(branchGuid)) {
            return true;
         }
      }
      if (branchToken != null) {
         if (branchToken.getGuid().equals(branchGuid)) return true;
      }
      return false;
   }
}

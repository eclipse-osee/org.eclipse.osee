/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2.filter;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Donald G. Dunne
 */
public class BranchGuidEventFilter implements IEventFilter {

   private final Collection<String> branchGuids;

   public BranchGuidEventFilter(String branchGuid) {
      this.branchGuids = Arrays.asList(branchGuid);
   }

   public BranchGuidEventFilter(Collection<String> branchGuids) {
      this.branchGuids = branchGuids;
   }

   public BranchGuidEventFilter(Branch branch) {
      this(branch.getGuid());
   }

   public boolean isFiltered(String branchGuid) {
      return this.branchGuids.contains(branchGuid);
   }

}

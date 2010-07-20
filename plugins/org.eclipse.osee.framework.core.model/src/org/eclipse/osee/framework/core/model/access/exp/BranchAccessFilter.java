/*
 * Created on Jul 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import org.eclipse.osee.framework.core.enums.PermissionEnum;

public class BranchAccessFilter extends AbstractAccessFilter {
   private final PermissionEnum branchPermission;

   public BranchAccessFilter(PermissionEnum branchPermission) {
      super();
      this.branchPermission = branchPermission;
   }

   @Override
   public int getRank() {
      return 40;
   }

   @Override
   public PermissionEnum filter(Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain) {
      return branchPermission;
   }

   @Override
   public boolean acceptToObject(Object object) {
      return true;
   }
}

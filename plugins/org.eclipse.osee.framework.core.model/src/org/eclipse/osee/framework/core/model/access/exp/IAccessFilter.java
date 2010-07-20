/*
 * Created on Jul 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import org.eclipse.osee.framework.core.enums.PermissionEnum;

public interface IAccessFilter extends Comparable<IAccessFilter> {
   int getRank();

   boolean acceptToObject(Object object);

   PermissionEnum filter(Object object, PermissionEnum toPermission, PermissionEnum agrPermission, AccessFilterChain filterChain);
}

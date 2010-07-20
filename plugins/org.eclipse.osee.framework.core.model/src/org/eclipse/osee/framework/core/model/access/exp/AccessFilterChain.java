/*
 * Created on Jul 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access.exp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.PermissionEnum;

public class AccessFilterChain {
   private final List<IAccessFilter> filters;

   //input user, toCheck
   //input user, branch

   //output collection artifactTypes
   //attribute types

   public AccessFilterChain() {
      this.filters = new LinkedList<IAccessFilter>();
   }

   public void add(IAccessFilter filter) {
      filters.add(filter);
   }

   public boolean doFilter(Object object, PermissionEnum toPermission, PermissionEnum agrPermission) {
      Collections.sort(filters);

      for (IAccessFilter filter : filters) {
         if (filter.acceptToObject(object)) {
            agrPermission = filter.filter(object, toPermission, agrPermission, this);
         } else {
            break;
         }
      }
      return agrPermission.matches(toPermission);
   }
}

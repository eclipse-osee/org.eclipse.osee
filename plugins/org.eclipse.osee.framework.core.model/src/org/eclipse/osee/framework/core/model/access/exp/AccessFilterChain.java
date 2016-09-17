/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.access.exp;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;

public class AccessFilterChain {
   private final List<IAccessFilter> filters;

   //input user, toCheck
   //input user, branch

   //output collection artifactTypes
   //attribute types

   public AccessFilterChain() {
      this.filters = new LinkedList<>();
   }

   public void add(IAccessFilter filter) {
      filters.add(filter);
   }

   public boolean doFilter(ArtifactToken artifact, Object object, PermissionEnum toPermission, PermissionEnum agrPermission) {

      Collections.sort(filters, new Comparator<IAccessFilter>() {

         @Override
         public int compare(IAccessFilter o1, IAccessFilter o2) {
            return o1.getPriority() - o2.getPriority();
         }

      });

      for (IAccessFilter filter : filters) {
         if (filter.acceptToObject(object)) {
            agrPermission = filter.filter(artifact, object, toPermission, agrPermission, this);
         } else {
            break;
         }
      }

      boolean toReturn = false;
      if (agrPermission != null) {
         toReturn = agrPermission.matches(toPermission);
      }
      return toReturn;
   }

   public void addAll(Collection<IAccessFilter> filtersToAdd) {
      filters.addAll(filtersToAdd);
   }
}

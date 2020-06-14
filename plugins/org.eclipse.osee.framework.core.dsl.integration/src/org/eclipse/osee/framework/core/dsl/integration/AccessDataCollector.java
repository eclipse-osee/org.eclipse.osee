/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.dsl.integration;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.access.Scope;

/**
 * @author Ryan D. Brooks
 */
public final class AccessDataCollector implements AccessDetailCollector {
   private final List<AccessDetail<?>> accessDetails;

   public AccessDataCollector() {
      this.accessDetails = new LinkedList<>();
   }

   @Override
   public void collect(AccessDetail<?> accessDetail) {
      if (accessDetail != null) {
         accessDetails.add(accessDetail);
      }
   }

   @Override
   public List<AccessDetail<?>> getAccessDetails() {
      return accessDetails;
   }

   @Override
   public boolean contains(Object expectedAccessObject, PermissionEnum expectedPermission, Scope expectedScopeLevel) {
      for (AccessDetail<?> accessDetail : accessDetails) {
         if (expectedPermission.equals(accessDetail.getPermission()) && expectedAccessObject.equals(
            accessDetail.getAccessObject()) && expectedScopeLevel.equals(accessDetail.getScope())) {
            return true;
         }
      }
      return false;
   }
}
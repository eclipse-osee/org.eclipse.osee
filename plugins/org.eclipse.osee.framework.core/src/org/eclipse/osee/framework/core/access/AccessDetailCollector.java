/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.access;

import java.util.List;
import org.eclipse.osee.framework.core.enums.PermissionEnum;

/**
 * @author Roberto E. Escobar
 */
public interface AccessDetailCollector {
   void collect(AccessDetail<?> accessDetail);

   default List<AccessDetail<?>> getAccessDetails() {
      return null;
   }

   default boolean contains(Object expectedAccessObject, PermissionEnum expectedPermission, Scope expectedScopeLevel) {
      return false;
   }
}
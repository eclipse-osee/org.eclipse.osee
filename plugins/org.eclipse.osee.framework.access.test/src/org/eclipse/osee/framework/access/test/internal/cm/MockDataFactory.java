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

package org.eclipse.osee.framework.access.test.internal.cm;

import org.eclipse.osee.framework.core.access.AccessDetail;
import org.eclipse.osee.framework.core.access.Scope;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class MockDataFactory {

   private MockDataFactory() {
      // Utility Class
   }

   public static AccessContextToken createAccessContextId(Long id, String name) {
      AccessContextToken cxt = AccessContextToken.valueOf(id, name);
      Assert.assertEquals(id, cxt.getId());
      Assert.assertEquals(name, cxt.getName());
      return cxt;
   }

   public static <T> AccessDetail<T> createAccessDetails(T expAccessObject, PermissionEnum expPermission, String expReason, Scope scope) {
      AccessDetail<T> target;
      if (expReason != null) {
         target = new AccessDetail<>(expAccessObject, expPermission, scope, expReason);
      } else {
         target = new AccessDetail<>(expAccessObject, expPermission, scope);
      }
      return target;
   }
}
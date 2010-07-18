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
package org.eclipse.osee.framework.access.test.internal;

import org.eclipse.osee.framework.core.enums.PermissionEnum;

public class PermissionTest {

   public void main(String[] args) {
      PermissionEnum[] a = {PermissionEnum.DENY, PermissionEnum.FULLACCESS, null};
      PermissionEnum[] b = {PermissionEnum.LOCK, PermissionEnum.FULLACCESS, null};
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            PermissionEnum branchPermission = a[i];
            PermissionEnum userPermission = b[j];
            if (branchPermission == PermissionEnum.DENY || userPermission == null) {
               System.out.print("T");
            } else {
               System.out.print("F");
            }
         }
         System.out.println();
      }
   }
}

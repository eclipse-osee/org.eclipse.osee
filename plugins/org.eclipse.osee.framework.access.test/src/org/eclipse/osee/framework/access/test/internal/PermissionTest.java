/*
 * Created on Jun 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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

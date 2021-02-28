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

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.PermissionEnum.DENY;
import static org.eclipse.osee.framework.core.enums.PermissionEnum.FULLACCESS;
import static org.eclipse.osee.framework.core.enums.PermissionEnum.USER_LOCK;
import static org.eclipse.osee.framework.core.enums.PermissionEnum.NONE;
import static org.eclipse.osee.framework.core.enums.PermissionEnum.READ;
import static org.eclipse.osee.framework.core.enums.PermissionEnum.WRITE;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link PermissionEnum}
 *
 * @author Roberto E. Escobar
 */
public class PermissionEnumTest {

   private static class PermissionTestData {
      PermissionEnum permissionEnum1;
      PermissionEnum permissionEnum2;
      boolean expectedMatches;
      PermissionEnum expectedMostRestrictive;

      public PermissionTestData(PermissionEnum permissionEnum1, PermissionEnum permissionEnum2, boolean expectedMatches, PermissionEnum expectedMostRestrictive) {
         super();
         this.permissionEnum1 = permissionEnum1;
         this.permissionEnum2 = permissionEnum2;
         this.expectedMatches = expectedMatches;
         this.expectedMostRestrictive = expectedMostRestrictive;
      }
   }

   private static void add(Collection<PermissionTestData> tests, PermissionEnum permissionEnum1, PermissionEnum permissionEnum2, boolean expectedMatches, PermissionEnum expectedMostRestrictive) {
      tests.add(new PermissionTestData(permissionEnum1, permissionEnum2, expectedMatches, expectedMostRestrictive));
   }

   private static Collection<PermissionTestData> getTestData() {
      Collection<PermissionTestData> data = new ArrayList<>();
      add(data, DENY, DENY, false, DENY);
      add(data, DENY, FULLACCESS, false, DENY);
      add(data, DENY, USER_LOCK, false, DENY);
      add(data, DENY, NONE, false, DENY);
      add(data, DENY, READ, false, DENY);
      add(data, DENY, WRITE, false, DENY);

      add(data, FULLACCESS, DENY, false, DENY);
      add(data, FULLACCESS, FULLACCESS, true, FULLACCESS);
      add(data, FULLACCESS, USER_LOCK, true, USER_LOCK);
      add(data, FULLACCESS, NONE, true, NONE);
      add(data, FULLACCESS, READ, true, READ);
      add(data, FULLACCESS, WRITE, true, WRITE);

      add(data, USER_LOCK, DENY, false, DENY);
      add(data, USER_LOCK, FULLACCESS, false, USER_LOCK);
      add(data, USER_LOCK, USER_LOCK, false, USER_LOCK);
      add(data, USER_LOCK, NONE, false, USER_LOCK);
      add(data, USER_LOCK, READ, true, USER_LOCK);
      add(data, USER_LOCK, WRITE, false, USER_LOCK);

      add(data, NONE, DENY, false, DENY);
      add(data, NONE, FULLACCESS, false, NONE);
      add(data, NONE, USER_LOCK, false, USER_LOCK);
      add(data, NONE, NONE, true, NONE);
      add(data, NONE, READ, false, NONE);
      add(data, NONE, WRITE, false, NONE);

      add(data, READ, DENY, false, DENY);
      add(data, READ, FULLACCESS, false, READ);
      add(data, READ, USER_LOCK, false, USER_LOCK);
      add(data, READ, NONE, true, NONE);
      add(data, READ, READ, true, READ);
      add(data, READ, WRITE, false, READ);

      add(data, WRITE, DENY, false, DENY);
      add(data, WRITE, FULLACCESS, false, WRITE);
      add(data, WRITE, USER_LOCK, false, USER_LOCK);
      add(data, WRITE, NONE, true, NONE);
      add(data, WRITE, READ, true, READ);
      add(data, WRITE, WRITE, true, WRITE);
      return data;
   }

   @Test
   public void testGetMostRestrictive() {
      int test = 0;
      for (PermissionTestData testData : getTestData()) {
         PermissionEnum actualNet =
            PermissionEnum.getMostRestrictive(testData.permissionEnum1, testData.permissionEnum2);
         String message = String.format("Test[%s] [%s:%s] expected:[%s] actual:[%s]", test, testData.permissionEnum1,
            testData.permissionEnum2, testData.expectedMostRestrictive, actualNet);
         Assert.assertEquals(message, testData.expectedMostRestrictive, actualNet);
         test++;
      }
   }

   @Test
   public void testMatches() {
      int test = 0;
      for (PermissionTestData testData : getTestData()) {
         boolean actualMatch = testData.permissionEnum1.matches(testData.permissionEnum2);
         String message = String.format("Test[%s] [%s matches %s] expected:[%s] actual:[%s]", test,
            testData.permissionEnum1, testData.permissionEnum2, testData.expectedMatches, actualMatch);
         Assert.assertEquals(message, testData.expectedMatches, actualMatch);
         test++;
      }
   }

   @Test
   public void testGetPermissionFromId() {
      for (PermissionEnum permission : PermissionEnum.values()) {
         int permissionId = permission.getRank();
         PermissionEnum enumFromId = PermissionEnum.getPermission(permissionId);
         Assert.assertEquals(permission, enumFromId);
      }
   }

   @Test
   public void testGetPermissionId() {
      int[] expectedIds = new int[] {5, 10, 20, 25, 30, 65535};
      PermissionEnum[] permissions = PermissionEnum.values();
      Assert.assertEquals(expectedIds.length, permissions.length);
      for (int index = 0; index < expectedIds.length; index++) {
         PermissionEnum permission = permissions[index];
         int actualId = permission.getRank();
         Assert.assertEquals(expectedIds[index], actualId);
      }
   }

   @Test
   public void testGetPermissionNames() {
      String[] expectedNames = new String[] {"None", "Read", "Write", "Lock", "Full Access", "Deny"};
      String[] actualNames = PermissionEnum.getPermissionNames();
      PermissionEnum[] enums = PermissionEnum.values();
      Assert.assertEquals(expectedNames.length, actualNames.length);
      Assert.assertEquals(expectedNames.length, enums.length);
      for (int index = 0; index < expectedNames.length; index++) {
         Assert.assertEquals(expectedNames[index], enums[index].getName());
         Assert.assertEquals(expectedNames[index], actualNames[index]);
      }
   }
}

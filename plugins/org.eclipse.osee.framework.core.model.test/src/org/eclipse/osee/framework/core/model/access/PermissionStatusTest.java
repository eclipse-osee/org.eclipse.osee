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

package org.eclipse.osee.framework.core.model.access;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link PermissionStatus}
 * 
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class PermissionStatusTest {

   @Test
   public void testDefaultConstruction() {
      PermissionStatus permissionStatus = new PermissionStatus();
      Assert.assertTrue(permissionStatus.matched());
      Assert.assertEquals("", permissionStatus.getReason());
   }

   @Test
   public void testConstruction() {
      PermissionStatus permissionStatus = new PermissionStatus(false, "Hello");
      Assert.assertFalse(permissionStatus.matched());
      Assert.assertEquals(permissionStatus.getReason(), "Hello");
   }

   @Test
   public void testToString() {
      PermissionStatus permissionStatus = new PermissionStatus(false, "Hello");
      Assert.assertEquals("PermissionStatus [reason=Hello, matchedPermission=false]", permissionStatus.toString());
   }
}

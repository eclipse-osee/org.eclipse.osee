/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.test.access;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
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

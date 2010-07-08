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
package org.eclipse.osee.framework.access.test.internal;

import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link PermissionEnum}
 * 
 * @author Jeff C. Phillips
 */
public class PermissionEnumTest {

   @Test
   public void testPermissions() {
      PermissionEnum read = PermissionEnum.READ;
      PermissionEnum write = PermissionEnum.WRITE;
      PermissionEnum deny = PermissionEnum.DENY;
      PermissionEnum full = PermissionEnum.FULLACCESS;

      Assert.assertTrue(full.matches(full));
      Assert.assertTrue(read.matches(read));
      Assert.assertTrue(write.matches(read));
      Assert.assertTrue(full.matches(write));

      //deny always returns false for a permission
      Assert.assertFalse(deny.matches(deny));
      Assert.assertFalse(deny.matches(write));
      Assert.assertFalse(read.matches(write));
      Assert.assertFalse(write.matches(full));
   }
}

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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link DeletionFlag}
 * 
 * @author Roberto E. Escobar
 */
public class DeletionFlagTest {

   @Test
   public void testIncludeDeletion() {
      DeletionFlag flag = DeletionFlag.INCLUDE_DELETED;
      Assert.assertTrue(flag.areDeletedAllowed());
   }

   @Test
   public void testExcludeDeletion() {
      DeletionFlag flag = DeletionFlag.EXCLUDE_DELETED;
      Assert.assertFalse(flag.areDeletedAllowed());

   }

   @Test
   public void testAllowDeleted() {
      DeletionFlag actual = DeletionFlag.allowDeleted(true);
      Assert.assertEquals(DeletionFlag.INCLUDE_DELETED, actual);

      actual = DeletionFlag.allowDeleted(false);
      Assert.assertEquals(DeletionFlag.EXCLUDE_DELETED, actual);
   }
}

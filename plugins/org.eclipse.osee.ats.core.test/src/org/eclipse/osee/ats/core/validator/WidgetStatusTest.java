/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class WidgetStatusTest {

   @Test
   public void test() {
      Assert.assertTrue(WidgetStatus.Success.isSuccess());
      Assert.assertFalse(WidgetStatus.Empty.isSuccess());

      Assert.assertTrue(WidgetStatus.Empty.isEmpty());
      Assert.assertFalse(WidgetStatus.Success.isEmpty());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(7, WidgetStatus.instance.values().size());
   }

   @Test
   public void testOrdinals() {
      Assert.assertEquals(new Long(0), WidgetStatus.None.getId());
      Assert.assertEquals(new Long(1), WidgetStatus.Success.getId());
   }

}

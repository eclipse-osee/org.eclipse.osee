/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

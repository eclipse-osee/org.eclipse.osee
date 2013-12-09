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
      Assert.assertTrue(WidgetStatus.Valid.isValid());
      Assert.assertFalse(WidgetStatus.Empty.isValid());

      Assert.assertTrue(WidgetStatus.Empty.isEmpty());
      Assert.assertFalse(WidgetStatus.Valid.isEmpty());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(6, WidgetStatus.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(WidgetStatus.Empty, WidgetStatus.valueOf(WidgetStatus.Empty.name()));
   }

}

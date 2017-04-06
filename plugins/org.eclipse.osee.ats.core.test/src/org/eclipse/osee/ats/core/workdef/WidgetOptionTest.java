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
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WidgetOption}
 *
 * @author Donald G. Dunne
 */
public class WidgetOptionTest {

   @Test
   public void testValues() {
      Assert.assertEquals(31, WidgetOption.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(WidgetOption.ADD_DEFAULT_VALUE, WidgetOption.valueOf(WidgetOption.ADD_DEFAULT_VALUE.name()));
   }

}

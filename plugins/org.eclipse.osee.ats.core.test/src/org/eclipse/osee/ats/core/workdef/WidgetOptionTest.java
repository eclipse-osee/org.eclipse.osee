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
      Assert.assertEquals(37, WidgetOption.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(WidgetOption.ADD_DEFAULT_VALUE, WidgetOption.valueOf(WidgetOption.ADD_DEFAULT_VALUE.name()));
   }

}

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

import org.eclipse.osee.ats.api.workdef.StateColor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link StateColor}
 *
 * @author Donald G. Dunne
 */
public class StateColorTest {

   @Test
   public void testValues() {
      Assert.assertEquals(16, StateColor.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(StateColor.BLACK, StateColor.valueOf(StateColor.BLACK.name()));
   }

}

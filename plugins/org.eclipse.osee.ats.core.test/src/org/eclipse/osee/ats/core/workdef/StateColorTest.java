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

import org.junit.Assert;
import org.eclipse.osee.ats.api.workdef.StateColor;
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

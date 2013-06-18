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
package org.eclipse.osee.ats.core.workdef.provider;

import org.junit.Assert;
import org.eclipse.osee.ats.dsl.BooleanDefUtil;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.junit.Test;

/**
 * Test case for {@link BooleanDefUtil}
 * 
 * @author Donald G. Dunne
 */
public class BooleanDefUtilTest {

   @Test
   public void testConstructor() {
      new BooleanDefUtil();
   }

   @Test
   public void testGet() {
      Assert.assertFalse(BooleanDefUtil.get(BooleanDef.NONE, false));
      Assert.assertTrue(BooleanDefUtil.get(BooleanDef.NONE, true));

      Assert.assertFalse(BooleanDefUtil.get(BooleanDef.FALSE, false));
      Assert.assertFalse(BooleanDefUtil.get(BooleanDef.FALSE, true));

      Assert.assertTrue(BooleanDefUtil.get(BooleanDef.TRUE, false));
      Assert.assertTrue(BooleanDefUtil.get(BooleanDef.TRUE, true));

      Assert.assertTrue(BooleanDefUtil.get(null, true));

   }

}

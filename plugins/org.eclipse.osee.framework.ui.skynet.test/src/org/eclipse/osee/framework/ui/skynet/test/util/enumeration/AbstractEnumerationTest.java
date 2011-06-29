/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.test.util.enumeration;

import org.junit.Assert;
import org.junit.Test;

/**
 * @Tests AbstractEnumeration
 * @author Donald G. Dunne
 */
public class AbstractEnumerationTest {

   @Test
   public void ordered() {
      Assert.assertEquals(5, OrderedEnum.Five.ordinal());
      Assert.assertEquals(1, OneEnum.Endorse.ordinal());
      Assert.assertEquals(8, OrderedEnum.Completed.ordinal());
      Assert.assertEquals(3, OneEnum.Completed.ordinal());
   }

   @Test
   public void testValueOf() {
      Assert.assertNotNull(OrderedEnum.valueOf("Five"));
      Assert.assertEquals("Five", OrderedEnum.valueOf("Five").name());
   }

   @Test
   public void testValues() {
      Assert.assertEquals(8, OrderedEnum.values().size());
   }

}

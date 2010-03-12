/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.test.util;

import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class GUIDTest {

   @org.junit.Test
   public void testInvalidGuids() {
      final String[] invalidHrids =
            {"short", "AAABDBYPet4AGJyrc_LONG_", "AAABGumk_InvalidChar!", "AAABGumk_InvalidChar#",
                  "AAABGumk_InvalidChar@", "AAABGumk_InvalidChar^"};
      for (String invalid : invalidHrids) {
         Assert.assertFalse("Invalid HRID " + invalid + " passes validity test", GUID.isValid(invalid));
      }
   }

   @org.junit.Test
   public void testValidGeneration() {
      for (int i = 0; i < 50000; i++) {
         String guid = GUID.create();
         Assert.assertTrue("Generated GUID " + guid + " fails validity test", GUID.isValid(guid));
      }
   }

   @org.junit.Test
   public void testValidGuids() {
      final String[] validGuids =
            {"AAABDBYPet4AGJyrc9dY1w", "AAABGumk_y8AFBnQMxZ58g", "AAABGyedHw8AphA8L4XexQ", "AAABDBYtCDsAVk2xBsTGzQ",
                  "AAABF_Ss2Q0AR8+yEx51WQ", "AAABHUWIY34B7y_TEmEslg", "AAABHZzkLCgBebkFSon4wA",
                  "AAABGuAX6cAB0Si2OIQ+YQ", "AAABGassf08BOe_BF6k6vA", "AAABHGv_u1gBGrilXclyUA",
                  "AAABDBYsngcAVk2xSlMcyw", "AAABGm+v1uEA389GY8zirw", "AAABDBYrxw4AVk2xHBRmhQ",
                  "AAABHbBGWuQA_ArKWeHLrg", "AAABDBYs7Z8AVk2xTZPPSg", "AAABGixOs5kA2KG7aom9Pw",
                  "AAABFjQ_MYIBEQV7BF3j7Q", "AAABDBYs9AcAVk2xTOjhPQ", "AAABDBYr7KcAVk2xDNCqew",
                  "AAABHHEPXpYBzfAhPukxiQ", "AAABDBYsYBMAVk2xN5jSVA", "AAABDBYsS3IAVk2xSRpaS="};
      for (String guid : validGuids) {
         Assert.assertTrue(GUID.isValid(guid));
      }
   }
}

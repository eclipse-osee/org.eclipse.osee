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
package org.eclipse.osee.ote.message.test.recordMap;

import junit.framework.Assert;
import org.eclipse.osee.ote.message.test.mock.TestMessage;
import org.junit.Test;

public class RecordMapTest {

   @Test
   public void testGetInt() {
      TestMessage msg = new TestMessage();

      // Testing below the boundary.
      try {
         msg.RECORD_MAP_1.get(1);
         Assert.assertTrue(true);
      } catch (IllegalArgumentException ex) {
         Assert.fail("We shouldn't get an exception for this get!");
      }

      // Testing on the boundary.
      try {
         msg.RECORD_MAP_1.get(2);
         Assert.fail("We should get an exception for this get on the boundary!");
      } catch (IllegalArgumentException ex) {
         Assert.assertTrue("We should get an exception for this index", true);
      }
      // Testing above the boundary.
      try {
         msg.RECORD_MAP_1.get(3);
         Assert.fail("We should get an exception for this get above the boundary!");
      } catch (IllegalArgumentException ex) {
         Assert.assertTrue("We should get an exception for this index", true);
      }

   }
}

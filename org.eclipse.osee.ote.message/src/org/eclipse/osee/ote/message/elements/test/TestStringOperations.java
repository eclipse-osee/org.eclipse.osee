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
package org.eclipse.osee.ote.message.elements.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStringOperations extends TestCase {

   private UnitTestSupport support;

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Before
   public void setUp() throws Exception {
      support = new UnitTestSupport();
   }

   @After
   public void tearDown() throws Exception {
      support.cleanup();
   }

   @Test
   public void testCheckWaitForValue() throws InterruptedException {
      TestMessage msg = new TestMessage();
      support.activateMsg(msg);
      support.genericTestCheckWaitForValue(msg.STRING_ELEMENT_1, new String[] {"Expected 1", " Expected", "Expected ",
            "", "expected", "EXPECTED", "Expected", "Expected", "01234", "x"}, "abc");
   }

   @Test
   public void testCheckNot() throws InterruptedException {
      TestMessage msg = new TestMessage();
      support.activateMsg(msg);
      String values[] = new String[] {" ", "a", "  ", "hi", "by", "123456789ABCDEF", "1"};
      support.genericTestCheckNot(msg.STRING_ELEMENT_1, values);
   }

   @Test
   public void testCheckList() throws InterruptedException {
      TestMessage msg = new TestMessage();
      support.activateMsg(msg);
      String[] good = new String[] {"a b c d e f g", "b", "_", "hello", "test"};
      String[] bad = new String[] {"a b c d e f g e", "c", " ", "\t", "hellO", "t\0est"};
      support.genericTestCheckList(msg.STRING_ELEMENT_1, good, bad);
   }

   @Test
   public void testStringEmpty() throws InterruptedException {
      TestMessage msg = new TestMessage();
      msg.STRING_ELEMENT_1.zeroize();
      Assert.assertTrue("string is not empty", msg.STRING_ELEMENT_1.isEmpty());
      msg.STRING_ELEMENT_1.setValue("hi");
      Assert.assertFalse("string is empty", msg.STRING_ELEMENT_1.isEmpty());
      msg.STRING_ELEMENT_1.setValue("\0");
      Assert.assertTrue("string is not empty", msg.STRING_ELEMENT_1.isEmpty());
      msg.STRING_ELEMENT_1.setValue("0123456789");
      Assert.assertFalse("string is empty", msg.STRING_ELEMENT_1.isEmpty());

   }

}

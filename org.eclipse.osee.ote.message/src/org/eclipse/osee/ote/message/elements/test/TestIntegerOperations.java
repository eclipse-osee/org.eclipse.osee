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


import junit.framework.TestCase;

import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestIntegerOperations extends TestCase{

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
    public void testCheckWaitForValue() throws InterruptedException{
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);
	int maxElementValue =getMaxIntValue(msg.INT_ELEMENT_1);
	support.genericTestCheckWaitForValue(msg.INT_ELEMENT_1, 
		new Integer[]{10, 30, 31, 30, 50, 75, maxElementValue, maxElementValue, 400, 1, maxElementValue, 0, maxElementValue, 1, 0, 1, 2}, 49);
    }
    
    @Test
    public void testCheckNot() throws InterruptedException{
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);
	int maxElementValue =getMaxIntValue(msg.INT_ELEMENT_1);
	support.genericTestCheckNot(msg.INT_ELEMENT_1, new Integer[]{0, maxElementValue, 10, 100, 1000, 5});
    }
  
    @Test
    public void testCheckMaintain() throws InterruptedException{
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);
	int maxElementValue =getMaxIntValue(msg.INT_ELEMENT_1);
	support.genericCheckMaintain(msg.INT_ELEMENT_1, 
		new Integer[]{0, maxElementValue, 0, 0, maxElementValue, maxElementValue, 1000, 5, 1, maxElementValue-1});
    }
    
    @Test
    public void testCheckRange() throws InterruptedException{
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);
	
	msg.INT_ELEMENT_1.setValue(50);
	support.checkRange(msg.INT_ELEMENT_1, 0, true, 100, true, 100);
	
	// test lower bound, inclusive
	msg.INT_ELEMENT_1.setValue(0);
	support.checkRangeFail(msg.INT_ELEMENT_1, 1, true, 100, true, 100);
	support.setAfter(msg.INT_ELEMENT_1, 1, 50);
	support.checkRange(msg.INT_ELEMENT_1, 1, true, 100, true, 100);
	
	// test upper bound, inclusive
	msg.INT_ELEMENT_1.setValue(101);
	support.checkRangeFail(msg.INT_ELEMENT_1, 1, true, 100, true, 100);
	support.setAfter(msg.INT_ELEMENT_1, 100, 50);
	support.checkRange(msg.INT_ELEMENT_1, 1, true, 100, true, 100);
	
	// test middle
	msg.INT_ELEMENT_1.setValue(1000);
	support.setAfter(msg.INT_ELEMENT_1, 50, 50); 
	support.checkRange(msg.INT_ELEMENT_1, 1, true, 100, true, 100);
	
	// test lower bound, exclusive
	msg.INT_ELEMENT_1.setValue(1);
	support.checkRangeFail(msg.INT_ELEMENT_1, 1, false, 100, false, 100); // make sure lower bound is exclusive
	support.setAfter(msg.INT_ELEMENT_1, 2, 50);
	support.checkRange(msg.INT_ELEMENT_1, 1, false, 100, false, 100);
	
	// test upper bound, exclusive
	msg.INT_ELEMENT_1.setValue(100);
	support.checkRangeFail(msg.INT_ELEMENT_1, 1, false, 100, false, 100); // make sure upper bound is exclusive
	support.setAfter(msg.INT_ELEMENT_1, 99, 50);
	support.checkRange(msg.INT_ELEMENT_1, 1, false, 100, false, 100);
	
	// check multiple out of range values
	msg.INT_ELEMENT_1.setValue(10);
	support.setAfter(msg.INT_ELEMENT_1, 100, 20);
	support.setAfter(msg.INT_ELEMENT_1, 9, 40);
	support.setAfter(msg.INT_ELEMENT_1, 101, 60);
	support.checkRangeFail(msg.INT_ELEMENT_1, 10, false, 100, false, 160);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 101, 0); // make sure we did not fail before we should have
    }
    
    @Test
    public void testCheckMaintainRange() throws InterruptedException{
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);
	
	msg.INT_ELEMENT_1.setValue(50);
	support.checkMaintainRange(msg.INT_ELEMENT_1, 0, true, 100, true, 100);
	
	msg.INT_ELEMENT_1.setValue(0);
	support.checkMaintainRange(msg.INT_ELEMENT_1, 0, true, 100, true, 100);
	
	msg.INT_ELEMENT_1.setValue(100);
	support.checkMaintainRange(msg.INT_ELEMENT_1, 0, true, 100, true, 100);

	// check lower bound, inclusive
	msg.INT_ELEMENT_1.setValue(10);
	support.setAfter(msg.INT_ELEMENT_1, 9, 50);
	support.checkMaintainRangeFail(msg.INT_ELEMENT_1, 10, true, 100, true, 100);
	
	// check upper bound, inclusive
	msg.INT_ELEMENT_1.setValue(100);
	support.setAfter(msg.INT_ELEMENT_1, 101, 50);
	support.checkMaintainRangeFail(msg.INT_ELEMENT_1, 10, true, 100, true, 100);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 101, 0);
	
	// check lower bound, exclusive
	msg.INT_ELEMENT_1.setValue(1);
	support.setAfter(msg.INT_ELEMENT_1, 0, 50);
	support.checkMaintainRangeFail(msg.INT_ELEMENT_1, 0, false, 100, false, 100);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 0, 0);
	
	// check upper bound, exclusive
	msg.INT_ELEMENT_1.setValue(99);
	support.setAfter(msg.INT_ELEMENT_1, 100, 50);
	support.checkMaintainRangeFail(msg.INT_ELEMENT_1, 0, false, 100, false, 100);
	
	// check bouncing between upper and lower inclusive
	msg.INT_ELEMENT_1.setValue(0);
	support.setAfter(msg.INT_ELEMENT_1, 100, 20);
	support.setAfter(msg.INT_ELEMENT_1, 50, 40);
	support.setAfter(msg.INT_ELEMENT_1, 1, 60);
	support.checkMaintainRange(msg.INT_ELEMENT_1, 0, true, 100, true, 100);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 1, 0);

	// check approaching upper fail boundary
	msg.INT_ELEMENT_1.setValue(97);
	support.setAfter(msg.INT_ELEMENT_1, 98, 20);
	support.setAfter(msg.INT_ELEMENT_1, 99, 40);
	support.setAfter(msg.INT_ELEMENT_1, 100, 60);
	support.checkMaintainRangeFail(msg.INT_ELEMENT_1, 0, false, 100, false, 100);
	
	// check approaching lower fail boundary
	msg.INT_ELEMENT_1.setValue(3);
	support.setAfter(msg.INT_ELEMENT_1, 2, 20);
	support.setAfter(msg.INT_ELEMENT_1, 1, 40);
	support.setAfter(msg.INT_ELEMENT_1, 0, 60);
	support.checkMaintainRangeFail(msg.INT_ELEMENT_1, 0, false, 100, false, 100);
	
	// check mixed inclusive, exclusive
	msg.INT_ELEMENT_1.setValue(1);
	support.setAfter(msg.INT_ELEMENT_1, 100, 60);
	support.checkMaintainRange(msg.INT_ELEMENT_1, 0, false, 100, true, 100);
	
	// check mixed inclusive, exclusive
	msg.INT_ELEMENT_1.setValue(99);
	support.setAfter(msg.INT_ELEMENT_1, 0, 60);
	support.checkMaintainRange(msg.INT_ELEMENT_1, 0, true, 100, false, 100);
	
	// check bouncing between upper and lower exclusive
	support.setSequence(msg.INT_ELEMENT_1, new Integer[]{1, 99, 50, 2, 2, 2, 1, 2, 1, 2, 44});
	support.checkMaintainRange(msg.INT_ELEMENT_1, 0, false, 100, false, 240);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 44, 0); 
	
	// check fail fast behavior
	msg.INT_ELEMENT_1.setValue(1);
	support.setAfter(msg.INT_ELEMENT_1, 100, 30);
	support.setAfter(msg.INT_ELEMENT_1, 50, 60);
	support.checkMaintainRangeFail(msg.INT_ELEMENT_1, 0, false, 100, false, 100);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 100, 0); // should immediately check value since it should have caused the above to fail
	support.checkWaitForValue(msg.INT_ELEMENT_1, 50, 100);
    }
    
    @Test
    public void testCheckMaintainNotRange() throws InterruptedException{
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);
	
	// bounce around boundaries, make sure it doesn't fail, inclusive
	support.setSequence(msg.INT_ELEMENT_1, new Integer[]{0, 101, 9, 102, 8, 102, 101, 101, 101, 9, 9,101, 9});
	support.checkMaintainNotRange(msg.INT_ELEMENT_1, 10, true, 100, true, 400);
	
	// bounce around boundaries, make sure it doesn't fail, exclusive
	msg.INT_ELEMENT_1.setValue(0);
	support.setAfter(msg.INT_ELEMENT_1, 100, 20);
	support.setAfter(msg.INT_ELEMENT_1, 10, 40);
	support.setAfter(msg.INT_ELEMENT_1, 101, 60);
	support.setAfter(msg.INT_ELEMENT_1, 9, 80);
	support.checkMaintainNotRange(msg.INT_ELEMENT_1, 10, false, 100, false, 200);

	// explore boundary then cause failure, exclusive
	msg.INT_ELEMENT_1.setValue(10);
	support.setAfter(msg.INT_ELEMENT_1, 100, 20);
	support.setAfter(msg.INT_ELEMENT_1, 55, 80);
	support.checkMaintainNotRangeFail(msg.INT_ELEMENT_1, 10, false, 100, false, 200);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 55, 0); // make sure our plan failure actually caused it to fail
	
	// explore boundary then cause failure, inclusive
	support.setSequence(msg.INT_ELEMENT_1, new Integer[]{9, 9, 101, 101, 9, 101, 55});
	support.checkMaintainNotRangeFail(msg.INT_ELEMENT_1, 10, true, 100, true, 200);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 55, 0); // make sure our planned failure actually caused it to fail
	
	// check transition to upper boundary causes failure, inclusive
	support.maintain(msg.INT_ELEMENT_1, 105, 100, 50);
	support.checkMaintainNotRangeFail(msg.INT_ELEMENT_1, 10, true, 100, true, 200);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 100, 0); // make sure our planned failure actually caused it to fail
	
	// check transition to upper boundary causes failure, exclusive
	support.maintain(msg.INT_ELEMENT_1, 100, 99, 50);
	support.checkMaintainNotRangeFail(msg.INT_ELEMENT_1, 10, false, 100, false, 200);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 99, 0); // make sure our planned failure actually caused it to fail
	
	// check transition to lower boundary causes failure, inclusive
	support.maintain(msg.INT_ELEMENT_1, 9, 10, 50);
	support.checkMaintainNotRangeFail(msg.INT_ELEMENT_1, 10, true, 100, true, 200);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 10, 0); // make sure our planned failure actually caused it to fail
	
	// check transition to lower boundary causes failure, exclusive
	support.maintain(msg.INT_ELEMENT_1, 10, 11, 50);
	support.checkMaintainNotRangeFail(msg.INT_ELEMENT_1, 10, false, 100, false, 200);
	support.checkWaitForValue(msg.INT_ELEMENT_1, 11, 0); // make sure our planned failure actually caused it to fail
    }
    
    @Test
    public void testCheckList() throws InterruptedException{
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);

	support.genericTestCheckList(
		msg.INT_ELEMENT_1, 
		new Integer[]{20, 40, 50, 60, 80, 100}, 
		new Integer[]{200, 300, 400, 500, 600});
    }

    @Test
    public void testCheckNotInList() throws InterruptedException{
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);

	support.genericTestCheckNotList(
		msg.INT_ELEMENT_1, 
		new Integer[]{20, 40, 50, 60, 80, 100}, 
		new Integer[]{200, 300, 400, 500, 600});
    }
    
    @Test
    public void testCheckMaintainList() throws InterruptedException{
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);
	
	Integer[] values = {20, 40, 50, 60, 80};
	support.genericTestCheckMaintainList(msg.INT_ELEMENT_1, values, 999);
    }
    
    @Test
    public void testCheckPulse() throws InterruptedException {
	TestMessage msg = new TestMessage();
	support.activateMsg(msg);
	
	support.setSequence(msg.INT_ELEMENT_1, new Integer[]{2, 100, 33, 100, 2, 99});
	support.checkPulse(msg.INT_ELEMENT_1, 100, 99);
	
	support.setSequence(msg.INT_ELEMENT_1, new Integer[]{100, 100, 99});
	support.checkPulse(msg.INT_ELEMENT_1, 100, 99);
    }
    
    private int getMaxIntValue(IntegerElement e) {
	return  (1 << e.getBitLength()) - 1;
    }
}

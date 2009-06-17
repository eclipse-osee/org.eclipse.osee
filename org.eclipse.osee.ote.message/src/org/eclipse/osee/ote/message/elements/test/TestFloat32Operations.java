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


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFloat32Operations {
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

	support.genericTestCheckWaitForValue(msg.FLOAT32_ELEMENT_1, 
		new Double[]{new Double(0.0f), 
					new Double(0.2f), 
					new Double(100.0f), new Double(999.00075f)}, new Double(33.4001f));
	
    }
}

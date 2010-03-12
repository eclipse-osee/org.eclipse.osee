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
package org.eclipse.osee.ote.core.log;

import java.util.logging.Level;

/**
 * @author Michael A. Winston
 */
public class TestLevel extends Level {
   
   /**
	 * 
	 */
	private static final long serialVersionUID = -3898810954576373823L;

   public final static TestLevel TEST_POINT = new TestLevel("TestPoint", Level.SEVERE.intValue() + 10);
   public final static TestLevel ATTENTION = new TestLevel("Attention", Level.WARNING.intValue() + 30); 
   public final static TestLevel REQUIREMENT = new TestLevel("Requirement", Level.WARNING.intValue() + 20);
   public final static TestLevel SUPPORT = new TestLevel("Support", Level.INFO.intValue() + 30);
   public final static TestLevel MESSAGING = new TestLevel("Messaging", Level.INFO.intValue() + 20);
   public final static TestLevel TRACE = new TestLevel("Trace", Level.INFO.intValue() + 10);
   public final static TestLevel DEBUG = new TestLevel("Debug", Level.FINE.intValue());

   /**
    * TestLevel Constructor.  Sets a level name and value.
    * 
    * @param name The level name.
    * @param value The level value.
    */
   private TestLevel(String name, int value) {
      super(name, value);
   }
}
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
package org.eclipse.osee.ats.test.testDb;

import junit.framework.TestCase;
import org.eclipse.osee.framework.database.initialize.DatabaseInitializationOperation;

/**
 * @author Donald G. Dunne
 */
public class DemoDbInitTest extends TestCase {

   public void testDemoDbInit() throws Exception {
      System.out.println("Begin Database Initialization...");
      DatabaseInitializationOperation.executeWithoutPrompting("OSEE Demo Database");
      System.out.println("Database Initialization Complete.");
   }

}

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
package org.eclipse.osee.ats.client.integration.tests.ats.util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsUsersTest.class,
   AtsUserTest.class,
   CopyActionDetailsTest.class,
   AtsProgramServiceTest.class,
   AtsNotifyEndpointImplTest.class,
   AtsChangeSetTest.class,
   AtsDeleteManagerTest.class,
   AtsImageTest.class,
   AtsXWidgetsExampleBlamTest.class,
   CreateActionUsingAllActionableItemsTest.class,
   ImportActionsViaSpreadsheetTest.class,
   ImportTasksFromSpreadsheetTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_Util_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + AtsTest_Util_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Util_Suite.class.getSimpleName());
   }
}

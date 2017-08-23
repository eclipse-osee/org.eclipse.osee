/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.dialog;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ //
   ActionableItemListDialogTest.class,
   NewActionWizardTest.class,
   AICheckedTreeDialogTest.class,
   ActionActionableItemListDialogTest.class,
   ActionableItemTreeWithChildrenDialogTest.class,
   TeamDefinitionCheckTreeDialogTest.class,
   TeamDefinitionTreeWithChildrenDialogTest.class //
})
public class AtsTest_Dialog_Suite {

   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + AtsTest_Dialog_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Dialog_Suite.class.getSimpleName());
   }
}

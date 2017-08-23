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
package org.eclipse.osee.ats.client.integration.tests.ats.editor.stateItem;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsDecisionReviewDecisionStateItemTest.class,
   AtsDecisionReviewPrepareStateItemTest.class,
   AtsForceAssigneesToTeamLeadsStateItemTest.class,
   AtsPeerToPeerReviewPrepareStateItemTest.class,
   AtsPeerToPeerReviewReviewStateItemTest.class})
/**
 * This test suite contains tests that must be run against demo database
 *
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_StateItem_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + AtsTest_Demo_StateItem_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Demo_StateItem_Suite.class.getSimpleName());
   }
}

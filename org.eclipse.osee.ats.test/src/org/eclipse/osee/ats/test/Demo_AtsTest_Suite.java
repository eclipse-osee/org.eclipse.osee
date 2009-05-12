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
package org.eclipse.osee.ats.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.osee.ats.test.AtsActionableItemToTeamDefinitionTest;
import org.eclipse.osee.ats.test.AtsTeamDefintionToWorkflowTest;
import org.eclipse.osee.ats.test.AtsWorkItemDefinitionTest;

/**
 * @author Donald G. Dunne
 */
public class Demo_AtsTest_Suite {

   public static Test suite() {
      TestSuite suite = new TestSuite("Test for org.eclipse.osee.ats.test.testDb - All ATS Tests");
      //$JUnit-BEGIN$
      suite.addTestSuite(AtsWorkItemDefinitionTest.class); // Can be run production or testDb
      suite.addTestSuite(AtsActionableItemToTeamDefinitionTest.class); // Can be run production or testDb
      suite.addTestSuite(AtsTeamDefintionToWorkflowTest.class); // Can be run production or testDb
      suite.addTestSuite(AtsPurgeTest.class);
      suite.addTestSuite(AtsNavigateItemsToWorldViewTest.class);
      suite.addTestSuite(AtsNavigateItemsToTaskEditorTest.class);
      suite.addTestSuite(AtsNavigateItemsToMassEditorTest.class);
      suite.addTestSuite(AtsBranchConfigurationTest.class);
      //$JUnit-END$
      return suite;
   }
}

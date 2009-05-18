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
import org.eclipse.osee.ats.test.cases.AtsActionableItemToTeamDefinitionTest;
import org.eclipse.osee.ats.test.cases.AtsQueryPerformanceTests;
import org.eclipse.osee.ats.test.cases.AtsTeamDefintionToWorkflowTest;
import org.eclipse.osee.ats.test.cases.AtsWorkItemDefinitionTest;

/**
 * This test suite contains test that can be run against any production db
 * 
 * @author Donald G. Dunne
 */
public class AtsTest_Config_Suite extends TestSuite {

   public static Test suite() {
      TestSuite suite = new TestSuite("AtsTest_Config_Suite");
      //$JUnit-BEGIN$
      suite.addTestSuite(AtsWorkItemDefinitionTest.class); // Can be run production or testDb
      suite.addTestSuite(AtsActionableItemToTeamDefinitionTest.class); // Can be run production or testDb
      suite.addTestSuite(AtsTeamDefintionToWorkflowTest.class); // Can be run production or testDb
      suite.addTestSuite(AtsQueryPerformanceTests.class); // Can be run production or testDb
      //$JUnit-END$
      return suite;
   }

}

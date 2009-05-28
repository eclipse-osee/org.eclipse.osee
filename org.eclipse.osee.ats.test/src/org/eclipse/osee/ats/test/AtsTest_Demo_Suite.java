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

import junit.framework.TestSuite;
import org.eclipse.osee.ats.test.cases.AtsBranchConfigurationTest;
import org.eclipse.osee.ats.test.cases.AtsDeleteManagerTest;
import org.eclipse.osee.ats.test.cases.AtsPurgeTest;
import org.eclipse.osee.ats.test.cases.AtsValidateAtsDatabaseTest;
import org.eclipse.osee.ats.test.cases.SMAPromptChangeStatusTest;

/**
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_Suite extends TestSuite {

   public static TestSuite suite() {
      TestSuite suite = new TestSuite("AtsTest_Demo_Suite");
      //$JUnit-BEGIN$
      suite.addTest(AtsTest_TestDb_Suite.suite());
      suite.addTestSuite(SMAPromptChangeStatusTest.class);
      suite.addTestSuite(AtsDeleteManagerTest.class);
      suite.addTestSuite(AtsPurgeTest.class);
      suite.addTestSuite(AtsBranchConfigurationTest.class);
      suite.addTestSuite(AtsValidateAtsDatabaseTest.class);
      //$JUnit-END$
      return suite;
   }

}

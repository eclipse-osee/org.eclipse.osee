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

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.test.cases.AtsBranchConfigurationTest;
import org.eclipse.osee.ats.test.cases.AtsDeleteManagerTest;
import org.eclipse.osee.ats.test.cases.AtsImageTest;
import org.eclipse.osee.ats.test.cases.AtsNavigateItemsToMassEditorTest;
import org.eclipse.osee.ats.test.cases.AtsNavigateItemsToTaskEditorTest;
import org.eclipse.osee.ats.test.cases.AtsNavigateItemsToWorldViewTest;
import org.eclipse.osee.ats.test.cases.AtsPurgeTest;
import org.eclipse.osee.ats.test.cases.AtsValidateAtsDatabaseTest;
import org.eclipse.osee.ats.test.cases.SMAPromptChangeStatusTest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {AtsImageTest.class, SMAPromptChangeStatusTest.class, AtsDeleteManagerTest.class,
      AtsPurgeTest.class, AtsBranchConfigurationTest.class, AtsValidateAtsDatabaseTest.class,
      AtsNavigateItemsToMassEditorTest.class, AtsNavigateItemsToTaskEditorTest.class,
      AtsNavigateItemsToWorldViewTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      assertTrue("Demo Application Server must be running.",
            ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
            ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
   }
}

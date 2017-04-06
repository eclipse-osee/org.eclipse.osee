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
package org.eclipse.osee.ats.client.integration.tests.ats.core.client;

import org.eclipse.osee.ats.client.integration.tests.ats.core.client.action.AtsCoreClient_Action_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.artifact.AtsCoreClient_Artifact_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.branch.AtsCoreClient_Branch_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.config.AtsCoreClient_Config_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.operation.AtsCoreClient_Operation_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.review.AtsCoreClient_Review_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.util.AtsCoreClient_Util_Suite;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.workflow.AtsCoreClient_Workflow_Suite;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsCoreClient_Action_Suite.class,
   AtsCoreClient_Artifact_Suite.class,
   AtsCoreClient_Branch_Suite.class,
   AtsCoreClient_Config_Suite.class,
   AtsCoreClient_Operation_Suite.class,
   AtsCoreClient_Review_Suite.class,
   AtsCoreClient_Util_Suite.class,
   AtsCoreClient_Workflow_Suite.class})
public class AtsCoreClient_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AtsCoreClient_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsCoreClient_Suite.class.getSimpleName());
   }
}

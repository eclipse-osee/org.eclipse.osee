/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.workflow;

import org.eclipse.osee.ats.client.integration.tests.ats.workflow.util.AtsTest_WorkflowUtil_Suite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   TeamWorkflowTest.class,
   AtsTest_WorkflowUtil_Suite.class,
   TaskRelatedToChangedArtifactTest.class,
   AtsTaskEndpointImplTest.class,
   WfePromptChangeStatusTest.class,
   TeamWorkflowProvidersTest.class,
   AtsRelationResolverServiceTest.class //
})
public class AtsTest_Workflow_Suite {

   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + AtsTest_Workflow_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Workflow_Suite.class.getSimpleName());
   }
}

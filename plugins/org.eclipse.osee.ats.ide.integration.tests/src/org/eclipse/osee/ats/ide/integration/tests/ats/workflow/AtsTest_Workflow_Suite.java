/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.action.AtsTest_Workflow_Action_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.cr.AtsTest_Workflow_CR_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.AtsTest_Workflow_Review_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.task.AtsTest_Workflow_Task_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.transition.AtsTest_Workflow_Transition_Suite;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.util.AtsTest_Workflow_Util_Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsTest_Workflow_CR_Suite.class,
   TeamWorkflowTest.class,
   WfePromptChangeStatusTest.class,
   TeamWorkflowProvidersTest.class,
   AtsRelationResolverServiceTest.class,
   AtsArtifactFactoryTest.class,
   AtsTeamWfEndpointTest.class,
   WorkItemsJsonReaderWriterTest.class,
   ConvertWorkflowStatesOperationTest.class,
   // suites
   AtsTest_Workflow_Action_Suite.class,
   AtsTest_Workflow_Review_Suite.class,
   AtsTest_Workflow_Task_Suite.class,
   AtsTest_Workflow_Transition_Suite.class,
   AtsTest_Workflow_Util_Suite.class
   //
})
public class AtsTest_Workflow_Suite {
   // do nothing
}

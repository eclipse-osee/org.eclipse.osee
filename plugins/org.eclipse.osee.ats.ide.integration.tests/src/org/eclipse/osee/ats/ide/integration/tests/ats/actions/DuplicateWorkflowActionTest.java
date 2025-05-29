/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.actions;

import java.util.Collections;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.duplicate.DuplicateWorkflowAction;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.junit.After;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowActionTest extends AbstractAtsActionRunTest {

   private IAtsTeamWorkflow newTeamWf;

   @Override
   public DuplicateWorkflowAction createAction() {
      return new DuplicateWorkflowAction(Collections.singleton(AtsTestUtil.getTeamWf()));
   }

   @After
   public void tearDown() throws Exception {
      if (newTeamWf != null) {
         ((TeamWorkFlowArtifact) newTeamWf.getStoreObject()).deleteAndPersist(getClass().getSimpleName());
      }
   }

}

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

import org.eclipse.osee.ats.ide.actions.OpenInAtsWorkflowEditorAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.junit.After;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorkflowEditorActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenInAtsWorkflowEditorAction createAction() {
      return new OpenInAtsWorkflowEditorAction(AtsTestUtil.getSelectedAtsArtifactsForTeamWf());
   }

   @After
   public void closeEditors() {
      WorkflowEditor.closeAll();
   }
}

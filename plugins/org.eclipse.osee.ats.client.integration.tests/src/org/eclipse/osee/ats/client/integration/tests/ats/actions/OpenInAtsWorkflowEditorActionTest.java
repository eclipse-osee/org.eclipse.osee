/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import org.eclipse.osee.ats.actions.OpenInAtsWorkflowEditorAction;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.editor.WorkflowEditor;
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

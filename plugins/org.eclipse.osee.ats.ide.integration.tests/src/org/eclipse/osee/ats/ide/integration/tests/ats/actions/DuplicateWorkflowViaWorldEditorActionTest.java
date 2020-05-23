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

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.ide.actions.SelectedAtsArtifactsAdapter;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.duplicate.DuplicateWorkflowViaWorldEditorAction;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowViaWorldEditorActionTest extends AbstractAtsActionRunTest {

   @Override
   public Action createAction() {
      return new DuplicateWorkflowViaWorldEditorAction(AtsTestUtil.getSelectedAtsArtifactsForTeamWf());
   }

   @Test(expected = OseeArgumentException.class)
   public void testException() {
      DuplicateWorkflowViaWorldEditorAction action =
         new DuplicateWorkflowViaWorldEditorAction(new SelectedAtsArtifactsAdapter());
      action.runWithException();
   }
}

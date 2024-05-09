/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.section.goal;

import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class OpenWebExportAction extends AbstractWebExportAction {

   public OpenWebExportAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Open Export View - Saved", goalArt, editor, AtsImage.RIGHT_ARROW_SM);
   }

   @Override
   public void runWithException() {
      String server = AtsApiService.get().getApplicationServerBase();

      String url = String.format("%s/ats/world/coll/%s/export", server, goalArt.getIdString());
      Program.launch(url);
   }

}

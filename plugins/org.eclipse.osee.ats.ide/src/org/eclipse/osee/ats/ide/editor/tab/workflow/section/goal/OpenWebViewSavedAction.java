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
public class OpenWebViewSavedAction extends AbstractWebExportAction {

   public OpenWebViewSavedAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Open Web View - Saved", goalArt, editor, AtsImage.GLOBE);
   }

   @Override
   public void runWithException() {
      String server = AtsApiService.get().getApplicationServerBase();

      server = server.replaceFirst(":[0-9]+$", ":4200");
      String url = String.format("%s/world?collId=%s", server, goalArt.getIdString());
      Program.launch(url);
   }

}

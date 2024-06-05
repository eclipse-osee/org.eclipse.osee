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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class OpenDifferencesWebViewAction extends AbstractWebExportAction {

   public OpenDifferencesWebViewAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Open Differences Web View", goalArt, editor, AtsImage.REPORT);
   }

   @Override
   public void runWithException() {

      String custGuid = validateAndGetCustomizeDataGuid();
      if (Strings.isInvalid(custGuid)) {
         return;
      }

      String webServer = AtsApiService.get().getWebBasepath();
      String url = String.format("%s/world?collId=%s&custId=%s&diff=true", webServer, goalArt.getIdString(), custGuid);
      Program.launch(url);
   }

}

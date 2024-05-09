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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.world.WorldResults;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class PublishWebViewJsonAction extends AbstractWebExportAction {

   public PublishWebViewJsonAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Publish Web Json Data", goalArt, editor, AtsImage.JSON);
   }

   @Override
   public void runWithException() {

      String custGuid = validateAndGetCustomizeDataGuid();
      if (Strings.isInvalid(custGuid)) {
         return;
      }

      if (MessageDialog.openConfirm(Displays.getActiveShell(), getText(),
         "Are you sure you want to publish the current view/data?")) {

         WorldResults wr =
            AtsApiService.get().getServerEndpoints().getWorldEndpoint().getCollectionJsonCustomizedPublish(
               goalArt.getArtifactToken(), custGuid);
         ResultsEditor.open("Results", getText(), AHTML.simpleJsonPage(JsonUtil.toJson(wr)));
      }
   }

}

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
import org.eclipse.osee.ats.api.workflow.world.WorldResults;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;

/**
 * @author Donald G. Dunne
 */
public class OpenWebViewJsonPublishedAction extends AbstractWebExportAction {

   public OpenWebViewJsonPublishedAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Open Web Json Data - Published (admin)", goalArt, editor, AtsImage.GLOBE);
   }

   @Override
   public void runWithException() {
      if (AtsApiService.get().getStoreService().isProductionDb()) {
         WorldResults wr =
            AtsApiService.get().getServerEndpoints().getWorldEndpoint().getCollectionJsonCustomizedPublished(
               goalArt.getArtifactId());
         ResultsEditor.open("Results", getText(), AHTML.simpleJsonPage(JsonUtil.toJson(wr)));
      }
   }

}

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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;

/**
 * @author Donald G. Dunne
 */
public class OpenDifferencesIdeViewAction extends AbstractWebExportAction {

   public OpenDifferencesIdeViewAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Open Differences IDE View", goalArt, editor, AtsImage.REPORT);
   }

   @Override
   public void runWithException() {

      String custGuid = validateAndGetCustomizeDataGuid();
      if (Strings.isInvalid(custGuid)) {
         return;
      }

      WorldResults liveWr = AtsApiService.get().getServerEndpoints().getWorldEndpoint().getCollectionJsonCustomized(
         goalArt.getToken(), custGuid);

      WorldResults savedWr =
         AtsApiService.get().getServerEndpoints().getWorldEndpoint().getCollectionJsonCustomizedPublished(
            goalArt.getArtifactToken());

      CompareHandler compareHandler = new CompareHandler(getText(), //
         new CompareItem("Live", JsonUtil.toJson(liveWr), 0L, "live.json"), //
         new CompareItem("Saved", JsonUtil.toJson(savedWr), 0L, "saved.json"), //
         null);
      compareHandler.compare();
   }

}

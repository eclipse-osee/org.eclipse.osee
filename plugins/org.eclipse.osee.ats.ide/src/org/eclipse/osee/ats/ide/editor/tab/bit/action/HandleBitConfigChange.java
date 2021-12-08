/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.bit.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.ide.editor.tab.bit.XBitViewer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeChildrenContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeArtifactDialog;

/**
 * @author Donald G. Dunne
 */
public class HandleBitConfigChange {
   protected final IAtsTeamWorkflow crTeamWf;
   protected final AtsApi atsApi;
   private final XBitViewer xBitViewer;

   public HandleBitConfigChange(XBitViewer xBitViewer, AtsApi atsApi) {
      this.xBitViewer = xBitViewer;
      this.crTeamWf = this.xBitViewer.getTeamWf();
      this.atsApi = atsApi;
   }

   public Collection<ArtifactToken> getStoredConfigs(BuildImpactData bid) {
      Artifact bidArt = (Artifact) atsApi.getQueryService().getArtifact(bid.getBidArt());
      return atsApi.getAttributeResolver().getAttributeValues(bidArt, AtsAttributeTypes.BitConfig);
   }

   public boolean handleChangeConfig(BuildImpactData bid) {

      IAtsProgram program = atsApi.getProgramService().getProgram(crTeamWf);
      if (program == null) {
         AWorkbench.popup("Can't retrieve Program from workflow");
         return false;
      }

      BranchToken branch = atsApi.getProgramService().getProductLineBranch(program);
      if (branch.isInvalid()) {
         AWorkbench.popup("Can't retrieve PL Branch from Program");
         return false;
      }

      List<Artifact> initialSelection = new ArrayList<>();
      initialSelection.addAll(Collections.castAll(getStoredConfigs(bid)));

      List<ArtifactToken> views = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch).getViews();
      List<Artifact> configs = Collections.castAll(views);

      FilteredCheckboxTreeArtifactDialog dialog =
         new FilteredCheckboxTreeArtifactDialog("Select Configuration(s)", "Select Configuration(s)", configs,
            new ArtifactTreeChildrenContentProvider(Artifact.class), new ArtifactLabelProvider());
      dialog.setInitialSelections(initialSelection);

      if (dialog.open() == Window.OK) {
         List<ArtifactId> ids = new ArrayList<ArtifactId>();
         for (Object obj : dialog.getChecked()) {
            ArtifactId config = (ArtifactId) obj;
            ids.add(ArtifactId.valueOf(config.getId()));
         }

         Artifact bidArt = (Artifact) atsApi.getQueryService().getArtifact(bid.getBidArt());
         bidArt.setAttributeFromValues(AtsAttributeTypes.BitConfig, ids);
         TransactionId transaction = bidArt.persist("Update BID Configs");

         ((Artifact) crTeamWf).reloadAttributesAndRelations();
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED, Arrays.asList(crTeamWf),
            transaction);

         return true;
      }

      return false;
   }
}

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
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactState;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ProgramVersion;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ProgramVersionTreeDialog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class NewProgramVersionAction extends Action {

   private final IAtsTeamWorkflow teamWf;
   private final AtsApi atsApi;

   public NewProgramVersionAction(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
      atsApi = AtsApiService.get();
   }

   @Override
   public void run() {
      List<ProgramVersion> pvers = new ArrayList<>();
      for (ArtifactToken progArt : ArtifactQuery.getArtifactListFromTypeWithInheritence(AtsArtifactTypes.Program,
         CoreBranches.COMMON, DeletionFlag.EXCLUDE_DELETED)) {
         IAtsProgram program = atsApi.getProgramService().getProgramById(progArt);
         for (ArtifactToken verArt : atsApi.getProgramService().getVersionsForProgram(program.getArtifactToken(),
            false).getVersions()) {
            IAtsVersion version = atsApi.getVersionService().getVersionById(verArt);
            pvers.add(new ProgramVersion(program, version));
         }
      }
      ProgramVersionTreeDialog dialog = new ProgramVersionTreeDialog(pvers);
      if (dialog.open() == Window.OK) {
         BuildImpactDatas bids = new BuildImpactDatas();
         bids.setTeamWf(teamWf.getStoreObject());
         for (ProgramVersion pVer : dialog.getChecked()) {
            BuildImpactData bid = new BuildImpactData();
            bid.setBids(bids);
            bid.setBidArt(ArtifactToken.valueOf(ArtifactId.SENTINEL, pVer.getVersion().getName()));
            bid.setBuild(pVer.getVersion().getArtifactToken());
            bid.setConfig("USG");
            bid.setProgram(pVer.getProgram().getArtifactToken());
            bid.setState(BuildImpactState.Open.getName());
            bids.addBuildImpactData(bid);
         }
         bids = atsApi.getServerEndpoints().getActionEndpoint().updateBids(teamWf.getAtsId(), bids);
         if (bids.getResults().isErrors()) {
            XResultDataUI.report(bids.getResults(), "Error creating BIDs");
         } else {
            ((Artifact) teamWf).reloadAttributesAndRelations();
            atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED, Arrays.asList(teamWf),
               bids.getTransaction());
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.GREEN_PLUS);
   }

   @Override
   public String getText() {
      return "Add Impacted Program / Build";
   }

   @Override
   public boolean isEnabled() {
      return teamWf.isInWork();
   }

}

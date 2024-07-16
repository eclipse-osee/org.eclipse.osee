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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ProgramVersion;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ProgramVersionTreeDialog;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DeleteProgramVersionAction extends Action {

   private final IAtsTeamWorkflow teamWf;
   private final AtsApi atsApi;

   public DeleteProgramVersionAction(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
      atsApi = AtsApiService.get();
   }

   @Override
   public void run() {
      List<ProgramVersion> pvers = new ArrayList<>();

      BuildImpactDatas currBids = atsApi.getServerEndpoints().getActionEndpoint().getBids(teamWf.getAtsId());
      for (BuildImpactData bid : currBids.getBuildImpacts()) {
         if (bid.getTeamWfs().isEmpty()) {
            IAtsProgram program = atsApi.getProgramService().getProgramById(bid.getProgram());
            IAtsVersion version =
               atsApi.getConfigService().getConfigurations().getIdToVersion().get(bid.getBuild().getId());
            pvers.add(new ProgramVersion(program.getArtifactToken(), version, bid.getBidArt()));
         }
      }
      BuildImpactDatas newBids = new BuildImpactDatas();

      ProgramVersionTreeDialog dialog = new ProgramVersionTreeDialog("Select Program/Version",
         "Select Program/Version\n\nNOTE: Items with workflows can not be deleted and are not shown.", pvers);
      if (dialog.open() == Window.OK) {
         newBids.setTeamWf(teamWf.getStoreObject());
         for (ProgramVersion pVer : dialog.getChecked()) {
            BuildImpactData bid = new BuildImpactData();
            bid.setBids(newBids);
            bid.setBidArt(pVer.getProgVerArt());
            bid.setBuild(pVer.getVersion().getArtifactToken());
            bid.setProgram(pVer.getProgramTok());
            newBids.addBuildImpactData(bid);
         }
         Job deleteBidsJob = new Job("Delete BID(s)") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  BuildImpactDatas resultBids =
                     atsApi.getServerEndpoints().getActionEndpoint().deleteBids(teamWf.getAtsId(), newBids);
                  if (resultBids.getResults().isErrors()) {
                     XResultDataUI.report(currBids.getResults(), "Error deleting BIDs");
                  } else {
                     ((Artifact) teamWf).reloadAttributesAndRelations();
                     atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED,
                        Arrays.asList(teamWf), currBids.getTransaction());
                  }
               } catch (Exception ex) {
                  XResultData rd = new XResultData();
                  rd.log(getName());
                  rd.logf("Exception Deleting BID(s) %s", Lib.exceptionToString(ex));
                  ResultsEditor.open("Error " + getName(), rd);
               }
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(deleteBidsJob);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.X_RED);
   }

   @Override
   public String getText() {
      return "Delete Impacted Program / Build";
   }

   @Override
   public boolean isEnabled() {
      return teamWf.isInWork();
   }

}

/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowBranchChangeDataAction extends AbstractAtsAction {

   private final AbstractWorkflowArtifact awa;

   public ShowBranchChangeDataAction(AbstractWorkflowArtifact awa) {
      super("Show Branch Change Data Report");
      this.awa = awa;
      setToolTipText("Show computed change data from Branch; should be same as what's shown in change report");
   }

   @Override
   public void runWithException() {
      if (!awa.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         AWorkbench.popup("Only valid for Team Workflow artifacts");
         return;
      }
      TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) awa;
      if (!AtsApiService.get().getBranchService().isWorkingBranchInWork(
         teamArt) && !AtsApiService.get().getBranchService().isWorkingBranchEverCommitted(teamArt)) {
         AWorkbench.popup("Working branch never created or committed.");
         return;
      }
      XResultData result = new XResultData();
      ChangeData changeData = AtsApiService.get().getBranchServiceIde().getChangeDataFromEarliestTransactionId(teamArt);
      result.log("Number of changes " + changeData.getChanges().size() + "\n");
      for (Change change : changeData.getChanges()) {
         result.log(String.format("Change [%s]", change));
      }
      XResultDataUI.report(result, String.format("Branch Change Data Report [%s]", awa));
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.WORKFLOW);
   }

   public static boolean isApplicable(AbstractWorkflowArtifact awa) {
      return awa.isOfType(AtsArtifactTypes.TeamWorkflow);
   }
}

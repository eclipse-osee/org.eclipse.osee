/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.framework.core.util.result.XResultData;
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
      if (!AtsClientService.get().getBranchService().isWorkingBranchInWork(
         teamArt) && !AtsClientService.get().getBranchService().isWorkingBranchEverCommitted(teamArt)) {
         AWorkbench.popup("Working branch never created or committed.");
         return;
      }
      XResultData result = new XResultData();
      ChangeData changeData = AtsBranchManager.getChangeDataFromEarliestTransactionId(teamArt);
      result.log("Number of changes " + changeData.getChanges().size() + "\n");
      for (Change change : changeData.getChanges()) {
         result.log(String.format("Change [%s]", change));
      }
      XResultDataUI.report(result, String.format("Branch Change Data Report [%s]", awa));
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.WORKFLOW_CONFIG);
   }

   public static boolean isApplicable(AbstractWorkflowArtifact awa) {
      return awa.isOfType(AtsArtifactTypes.TeamWorkflow);
   }
}

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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowBranchChangeDataAction extends Action {

   private final AbstractWorkflowArtifact sma;

   public ShowBranchChangeDataAction(AbstractWorkflowArtifact sma) {
      super("Show Branch Change Data Report");
      this.sma = sma;
      setToolTipText("Show computed change data from Branch; should be same as what's shown in change report");
   }

   @Override
   public void run() {
      try {
         if (!(sma instanceof TeamWorkFlowArtifact)) {
            AWorkbench.popup("Only valid for Team Workflow artifacts");
            return;
         }
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) sma;
         if (!teamArt.getBranchMgr().isWorkingBranchInWork() && !teamArt.getBranchMgr().isWorkingBranchEverCommitted()) {
            AWorkbench.popup("Working branch never created or committed.");
            return;
         }
         XResultData result = new XResultData();
         ChangeData changeData = teamArt.getBranchMgr().getChangeDataFromEarliestTransactionId();
         result.log("Number of changes " + changeData.getChanges().size() + "\n");
         for (Change change : changeData.getChanges()) {
            result.log(String.format("Change [%s]", change));
         }
         result.report(String.format("Branch Change Data Report [%s]", sma));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.WORKFLOW_CONFIG);
   }

   public static boolean isApplicable(AbstractWorkflowArtifact sma) {
      return sma instanceof TeamWorkFlowArtifact;
   }
}

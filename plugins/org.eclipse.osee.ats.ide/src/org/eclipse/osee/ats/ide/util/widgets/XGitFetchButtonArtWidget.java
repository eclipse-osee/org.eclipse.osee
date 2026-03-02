/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitBranchName;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitRepoName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.define.rest.api.git.GitEndpoint;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.component.annotations.Component;

/**
 * @author Stephen J. Molaro
 */
@Component(service = XWidget.class, immediate = true)
public class XGitFetchButtonArtWidget extends XButtonWidget {

   public static WidgetId ID = WidgetIdAts.XGitFetchButtonArtWidget;
   private TeamWorkFlowArtifact teamArt;

   public XGitFetchButtonArtWidget() {
      super(ID, "Import Code Files from Branch");
      setOseeImage(AtsImage.PLAY_GREEN);
      setToolTip("Fetch Git Data");
      addXModifiedListener(listener);
      this.teamArt = null;
   }

   private final XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         try {
            final BranchId workingBranch = AtsApiService.get().getBranchService().getBranch(teamArt);
            if (workingBranch.isValid()) {
               String workingBranchName = AtsApiService.get().getBranchService().getBranchName(teamArt);
               String repositoryName = teamArt.getSoleAttributeValue(GitRepoName);
               String branchName = teamArt.getSoleAttributeValue(GitBranchName);
               if (MessageDialog.openConfirm(Displays.getActiveShell(), "Fetch Git Data", String.format(
                  "Fetch git code files for:\n\"%s\"\n\n On git branch:\n\"%s\"\n\nfor OSEE branch: \n\"%s\"\n\nIs that correct?\n\n---\nNOTE: Only need to do when git branch not yet merged for traceability.",
                  repositoryName, branchName, workingBranchName))) {
                  GitEndpoint proxy = AtsApiService.get().getOseeClient().getGitEndpoint();
                  if (proxy.getRemoteBranches(workingBranch, repositoryName).contains(branchName)) {
                     proxy.updateGitTrackingBranch(workingBranch, repositoryName, true, true, branchName);
                  } else {
                     MessageDialog.openError(Displays.getActiveShell(), "Branch does not exist",
                        String.format("The branch [%s] does not exist or is invalid.", branchName));
                  }
               }
               button.redraw();
               button.getParent().layout();
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      };
   };

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return teamArt;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (getArtifact().isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.teamArt = (TeamWorkFlowArtifact) artifact;
      }
   }

   @Override
   public boolean isEditable() {
      return teamArt != null;
   }

}
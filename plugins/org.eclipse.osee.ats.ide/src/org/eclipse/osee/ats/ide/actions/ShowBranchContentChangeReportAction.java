/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 * @author Branden W. Phillips
 */
public class ShowBranchContentChangeReportAction extends Action {

   private final TeamWorkFlowArtifact teamArt;

   /**
    * Since this accessControlService is only going to be added for the Implement state, Location. AllState will work
    */
   public ShowBranchContentChangeReportAction(TeamWorkFlowArtifact teamArt) {
      super();
      this.teamArt = teamArt;
      setText("Generate Branch Content Change Report");
      setToolTipText(getText());
      boolean enabled = false;
      try {
         if (AtsApiService.get().getBranchService().isWorkingBranchInWork(teamArt)) {
            enabled = true;
         } else {
            enabled = AtsApiService.get().getBranchService().isCommittedBranchExists(teamArt);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      setEnabled(enabled);
   }

   @Override
   public void run() {
      AtsApiService.get().getBranchServiceIde().generateBranchContentChangeReport(teamArt);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.BRANCH_CONTENT_CHANGE_REPORT);
   }

}

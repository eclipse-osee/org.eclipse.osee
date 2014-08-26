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
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowChangeReportAction extends Action {

   private final TeamWorkFlowArtifact teamArt;

   // Since this service is only going to be added for the Implement state, Location.AllState will
   // work
   public ShowChangeReportAction(TeamWorkFlowArtifact teamArt) {
      super();
      this.teamArt = teamArt;
      setText("Show Change Report");
      setToolTipText(getText());
      boolean enabled = false;
      try {
         if (AtsClientService.get().getBranchService().isWorkingBranchInWork(teamArt)) {
            enabled = true;
         } else {
            enabled = AtsClientService.get().getBranchService().isCommittedBranchExists(teamArt);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      setEnabled(enabled);
   }

   @Override
   public void run() {
      AtsBranchManager.showChangeReport(teamArt);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE);
   }

}

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
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowChangeReportAction extends Action {

   private final TeamWorkFlowArtifact teamArt;

   // Since this service is only going to be added for the Implement state, Location.AllState will
   // work
   public ShowChangeReportAction(TeamWorkFlowArtifact teamArt) {
      this.teamArt = teamArt;
      setText("Show Change Report");
      setToolTipText(getText());
      boolean enabled = false;
      try {
         if (teamArt.getBranchMgr().isWorkingBranchInWork()) {
            enabled = true;
         } else {
            enabled = teamArt.getBranchMgr().isCommittedBranchExists();
         }
      } catch (Exception ex) {
         // do nothing
      }
      setEnabled(enabled);
   }

   @Override
   public void run() {
      teamArt.getBranchMgr().showChangeReport();
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.action.Action#getImageDescriptor()
    */
   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE);
   }

}

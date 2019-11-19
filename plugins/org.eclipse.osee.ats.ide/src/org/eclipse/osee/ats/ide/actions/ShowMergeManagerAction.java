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
package org.eclipse.osee.ats.ide.actions;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.branch.AtsBranchManager;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowMergeManagerAction extends Action {

   private final TeamWorkFlowArtifact teamArt;

   // Since this accessControlService is only going to be added for the Implement state, Location.AllState will
   // work
   public ShowMergeManagerAction(TeamWorkFlowArtifact teamArt) {
      super();
      this.teamArt = teamArt;
      setText("Show Merge Manager");
      setToolTipText(getText());
      try {
         setEnabled(AtsClientService.get().getBranchService().isWorkingBranchInWork(
            teamArt) || AtsClientService.get().getBranchService().isCommittedBranchExists(teamArt));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void run() {
      AtsBranchManager.showMergeManager(teamArt);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.OUTGOING_MERGED);
   }

}

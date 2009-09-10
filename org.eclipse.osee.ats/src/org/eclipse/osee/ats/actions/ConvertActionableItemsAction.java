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

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ConvertActionableItemsAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public ConvertActionableItemsAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Convert to Actionable Item/Team");
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText(getText());
   }

   @Override
   public void run() {
      try {
         Collection<TeamWorkFlowArtifact> teamArts =
               Collections.castMatching(TeamWorkFlowArtifact.class, selectedAtsArtifacts.getSelectedSMAArtifacts());
         if (teamArts.size() == 0) throw new OseeStateException("No TeamWorkflows selected");

         TeamWorkFlowArtifact teamArt = teamArts.iterator().next();
         Result result = teamArt.convertActionableItems();
         if (result.isFalse() && !result.getText().equals("")) {
            result.popup();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TEAM_DEFINITION);
   }

   public void updateEnablement() {
      try {
         Collection<TeamWorkFlowArtifact> teamArts =
               Collections.castMatching(TeamWorkFlowArtifact.class, selectedAtsArtifacts.getSelectedSMAArtifacts());
         setEnabled(teamArts.size() == 1);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         setEnabled(false);
      }
   }
}

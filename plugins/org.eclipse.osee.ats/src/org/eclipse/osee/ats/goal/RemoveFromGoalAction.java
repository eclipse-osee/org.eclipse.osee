/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.goal;

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class RemoveFromGoalAction extends Action {

   private final GoalArtifact goalArt;
   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public RemoveFromGoalAction(GoalArtifact goalArt, ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Remove from Goal");
      this.goalArt = goalArt;
      this.selectedAtsArtifacts = selectedAtsArtifacts;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.REMOVE);
   }

   @Override
   public void run() {
      try {
         Collection<? extends Artifact> selected = selectedAtsArtifacts.getSelectedAtsArtifacts();
         if (selected.size() == 0) {
            AWorkbench.popup("No items selected");
            return;
         }
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Remove from Goal",
            String.format("Remove [%s] from Goal [%s]?", selected, goalArt))) {
            for (Artifact art : selected) {
               goalArt.deleteRelation(AtsRelationTypes.Goal_Member, art);
            }
            goalArt.persist("Remove from Goal");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}

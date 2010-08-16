/*
 * Created on Aug 13, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.goal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class SetGoalOrderAction extends Action {

   private final GoalArtifact goalArt;
   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public SetGoalOrderAction(GoalArtifact goalArt, ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Set Goal Order");
      this.goalArt = goalArt;
      this.selectedAtsArtifacts = selectedAtsArtifacts;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.GOAL);
   }

   @Override
   public void run() {
      try {
         GoalArtifact.promptChangeGoalOrder(goalArt, this.selectedAtsArtifacts.getSelectedAtsArtifacts());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}

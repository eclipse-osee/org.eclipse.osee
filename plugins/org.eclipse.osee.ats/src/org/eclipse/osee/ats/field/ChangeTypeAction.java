/*
 * Created on Nov 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ats.world.WorldXViewer;

public class ChangeTypeAction extends Action {

   private final WorldXViewer worldXViewer;

   public ChangeTypeAction(WorldXViewer worldXViewer) {
      super("Edit Change Type", IAction.AS_PUSH_BUTTON);
      this.worldXViewer = worldXViewer;
   };

   @Override
   public void run() {
      if (ChangeTypeColumn.promptChangeType(worldXViewer.getSelectedTeamWorkflowArtifacts(), true)) {
         worldXViewer.update(worldXViewer.getSelectedArtifactItems().toArray(), null);
      }
   }
}

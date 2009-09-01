/*
 * Created on Aug 31, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.operation.DuplicateWorkflowBlam;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowAction extends Action {

   private final Collection<TeamWorkFlowArtifact> teams;

   public DuplicateWorkflowAction(Collection<TeamWorkFlowArtifact> teams) {
      super(
            teams.size() == 1 ? "Duplicate this \"" + teams.iterator().next().getArtifactTypeName() + "\" Workflow" : "Duplicate Workflows");
      this.teams = teams;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE));
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.action.Action#run()
    */
   @Override
   public void run() {
      try {
         DuplicateWorkflowBlam blamOperation = new DuplicateWorkflowBlam();
         blamOperation.setDefaultTeamWorkflows(teams);
         BlamEditor.edit(blamOperation);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}

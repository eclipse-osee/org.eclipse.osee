/*
 * Created on Sep 8, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.StateListDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

public class RelatedToStateColumn {

   public static boolean promptChangeRelatedToState(AbstractWorkflowArtifact sma, boolean persist) {
      if (sma.isTask()) {
         return promptChangeRelatedToState(Arrays.asList((TaskArtifact) sma), persist);
      } else {
         AWorkbench.popup("Select Tasks to change Related-to-State");
      }
      return false;
   }

   public static boolean promptChangeRelatedToState(final Collection<? extends TaskArtifact> tasks, boolean persist) {
      if (tasks.size() == 0) {
         AWorkbench.popup("Select Tasks to change Related-to-State");
         return false;
      }
      try {
         final StateListDialog dialog =
            new StateListDialog("Change Related-to-State", "Select new state for task to be worked in.",
               getValidStates(tasks.iterator().next().getParentTeamWorkflow()));
         if (tasks.size() == 1) {
            dialog.setInitialSelections(new Object[] {tasks.iterator().next().getWorldViewRelatedToState()});
         }
         if (dialog.open() == 0) {
            if (dialog.getSelectedState().isEmpty()) {
               AWorkbench.popup("No Related-to-State selected");
               return false;
            }
            SkynetTransaction transaction = null;
            if (persist) {
               transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Prompt Change Related-to-State");
            }
            for (TaskArtifact task : tasks) {
               if (!task.getWorldViewRelatedToState().equals(dialog.getSelectedState())) {
                  task.setSoleAttributeFromString(AtsAttributeTypes.RelatedToState, dialog.getSelectedState());
                  if (persist) {
                     task.saveSMA(transaction);
                  }
               }
            }
            if (persist) {
               transaction.execute();
            }
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't change Related-to-State", ex);
         return false;
      }
   }

   private static List<String> getValidStates(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      List<String> names = new ArrayList<String>();
      names.addAll(teamArt.getWorkFlowDefinition().getPageNames());
      Collections.sort(names);
      return names;
   }
}

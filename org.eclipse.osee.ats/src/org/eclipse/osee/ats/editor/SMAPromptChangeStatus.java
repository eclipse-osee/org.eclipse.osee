/*
 * Created on May 25, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.TaskOptionStatusDialog;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class SMAPromptChangeStatus {

   public static boolean promptChangeStatus(StateMachineArtifact sma, boolean persist) throws OseeCoreException {
      return promptChangeStatus(Arrays.asList(sma), persist);
   }

   public static void popupTaskNotInRelatedToState(TaskArtifact taskArt) throws OseeCoreException {
      AWorkbench.popup(
            "ERROR",
            String.format(
                  "Task work must be done in \"Related to State\" of parent workflow for Task titled: \"%s\".\n\n" +
                  //
                  "Task work configured to be done in parent's \"%s\" state.\nParent workflow is currently in \"%s\" state.\n\n" +
                  //
                  "Either transition parent workflow or change Task's \"Related to State\" to perform task work.",
                  taskArt.getDescriptiveName(), taskArt.getWorldViewRelatedToState(),
                  taskArt.getParentSMA().getSmaMgr().getStateMgr().getCurrentStateName()));
   }

   public static boolean promptChangeStatus(final Collection<? extends StateMachineArtifact> smas, boolean persist) throws OseeCoreException {
      try {
         // If task status is being changed, make sure tasks belong to current state
         for (StateMachineArtifact sma : smas) {
            if (sma instanceof TaskArtifact) {
               if (!((TaskArtifact) sma).isRelatedToParentWorkflowCurrentState()) {
                  popupTaskNotInRelatedToState((TaskArtifact) sma);
                  return false;
               }
            }
         }
         // Access resolution options if object is task
         List<TaskResOptionDefinition> options = null;
         if (smas.iterator().next() instanceof TaskArtifact) {
            if (((TaskArtifact) smas.iterator().next()).isUsingTaskResolutionOptions()) {
               options = ((TaskArtifact) smas.iterator().next()).getTaskResolutionOptionDefintions();
            }
         }
         for (StateMachineArtifact sma : smas) {
            SMAManager smaMgr = new SMAManager(sma);
            if (smaMgr.isReleased()) {
               AWorkbench.popup("ERROR",
                     sma.getArtifactTypeName() + " \"" + sma.getDescriptiveName() + "\"\n is already released.");
               return false;
            }
         }
         TaskOptionStatusDialog tsd =
               new TaskOptionStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Enter State Status",
                     "Select resolution, enter percent complete and number of hours you spent since last status.",
                     true, options, smas);
         int result = tsd.open();
         if (result == 0) {
            double hours = tsd.getHours().getFloat();
            if (tsd.isSplitHours()) {
               hours = hours / smas.size();
            }
            SkynetTransaction transaction = null;
            if (persist) {
               transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            }
            for (StateMachineArtifact sma : smas) {
               if (sma.getSmaMgr().getStateMgr().isUnAssigned()) {
                  sma.getSmaMgr().getStateMgr().removeAssignee(UserManager.getUser(SystemUser.UnAssigned));
                  sma.getSmaMgr().getStateMgr().addAssignee(UserManager.getUser());
               }
               if (options != null) {
                  sma.setSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(),
                        tsd.getSelectedOptionDef().getName());
               }
               if (sma instanceof TaskArtifact) {
                  ((TaskArtifact) sma).statusPercentChanged(hours, tsd.getPercent().getInt(), transaction);
               } else {
                  sma.getSmaMgr().getStateMgr().updateMetrics(hours, tsd.getPercent().getInt(), true);
               }
               if (persist) {
                  sma.persistAttributesAndRelations(transaction);
               }
            }
            if (persist) {
               transaction.execute();
            }
            return true;
         }
      } catch (OseeStateException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

}

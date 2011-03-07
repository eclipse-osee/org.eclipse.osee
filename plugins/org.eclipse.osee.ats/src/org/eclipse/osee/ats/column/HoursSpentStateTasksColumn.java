/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.swt.SWT;

public class HoursSpentStateTasksColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static HoursSpentStateTasksColumn instance = new HoursSpentStateTasksColumn();

   public static HoursSpentStateTasksColumn getInstance() {
      return instance;
   }

   private HoursSpentStateTasksColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".stateTaskHoursSpent", "State Task Hours Spent", 40, SWT.CENTER,
         false, SortDataType.Float, false,
         "Hours spent in performing the changes for the tasks related to the current state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HoursSpentStateTasksColumn copy() {
      HoursSpentStateTasksColumn newXCol = new HoursSpentStateTasksColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return AtsUtil.doubleToI18nString(getHoursSpentFromStateTasks((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return hours spent working ONLY on tasks related to stateName
    */
   public static double getHoursSpentFromStateTasks(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         double hours = 0;
         for (TeamWorkFlowArtifact team : ActionManager.getTeamWorkFlowArtifacts(artifact)) {
            if (!team.isCancelled()) {
               hours += getHoursSpentFromStateTasks(team);
            }
         }
         return hours;
      }
      if (artifact instanceof AbstractTaskableArtifact) {
         return getHoursSpentFromStateTasks(artifact,
            ((AbstractWorkflowArtifact) artifact).getStateMgr().getCurrentState());
      }
      return 0;
   }

   /**
    * Return Hours Spent for Tasks of "Related to State" stateName
    * 
    * @param relatedToState state name of parent workflow's state
    * @return Returns the Hours Spent
    */
   public static double getHoursSpentFromStateTasks(Artifact artifact, IWorkPage relatedToState) throws OseeCoreException {
      double spent = 0;
      if (artifact instanceof AbstractTaskableArtifact) {
         for (TaskArtifact taskArt : ((AbstractTaskableArtifact) artifact).getTaskArtifacts(relatedToState)) {
            spent += HoursSpentTotalColumn.getHoursSpentTotal(taskArt);
         }
      }
      return spent;
   }

}

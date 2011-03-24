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
import org.eclipse.osee.ats.artifact.ActionManager;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.swt.SWT;

public class PercentCompleteStateTasksColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteStateTasksColumn instance = new PercentCompleteStateTasksColumn();

   public static PercentCompleteStateTasksColumn getInstance() {
      return instance;
   }

   private PercentCompleteStateTasksColumn() {
      super(
         WorldXViewerFactory.COLUMN_NAMESPACE + ".stateTaskPercentComplete",
         "State Task Percent Complete",
         40,
         SWT.CENTER,
         false,
         SortDataType.Percent,
         false,
         "Percent Complete for the tasks related to the current state.\n\nCalculation: total percent of all tasks related to state / number of tasks related to state");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteStateTasksColumn copy() {
      PercentCompleteStateTasksColumn newXCol = new PercentCompleteStateTasksColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return String.valueOf(getPercentCompleteFromStateTasks((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    */
   public static int getPercentCompleteFromStateTasks(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         double percent = 0;
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(artifact)) {
            if (!team.isCancelled()) {
               percent += getPercentCompleteFromStateTasks(team);
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / ActionManager.getTeams(artifact).size();
         return rollPercent.intValue();
      }
      if (artifact instanceof AbstractTaskableArtifact) {
         return getPercentCompleteFromStateTasks(artifact,
            ((AbstractTaskableArtifact) artifact).getStateMgr().getCurrentState());
      }
      return 0;
   }

   /**
    * Return Percent Complete ONLY on tasks related to stateName. Total Percent / # Tasks
    */
   public static int getPercentCompleteFromStateTasks(Artifact artifact, IWorkPage relatedToState) throws OseeCoreException {
      if (!(artifact instanceof AbstractTaskableArtifact)) {
         return 0;
      }
      return ((AbstractTaskableArtifact) artifact).getPercentCompleteFromTasks(relatedToState);
   }

}

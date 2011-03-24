/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionManager;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.WorkflowManager;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.SimpleTeamState;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;
import org.eclipse.swt.SWT;

public class HoursSpentTotalColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static HoursSpentTotalColumn instance = new HoursSpentTotalColumn();

   public static HoursSpentTotalColumn getInstance() {
      return instance;
   }

   private HoursSpentTotalColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".totalHoursSpent", "Total Hours Spent", 40, SWT.CENTER, false,
         SortDataType.Float, false, "Hours spent for all work related to all states.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HoursSpentTotalColumn copy() {
      HoursSpentTotalColumn newXCol = new HoursSpentTotalColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return AtsUtil.doubleToI18nString(getHoursSpentTotal((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return hours spent working states, reviews and tasks (not children SMAs)
    */
   public static double getHoursSpentTotal(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         double hours = 0;
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(artifact)) {
            if (!team.isCancelled()) {
               hours += getHoursSpentTotal(team);
            }
         }
         return hours;
      }
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return getHoursSpentTotal(artifact, WorkflowManager.getStateManager(artifact).getCurrentState());
      }
      return 0;
   }

   /**
    * Return hours spent working all states, reviews and tasks (not children SMAs)
    */
   public static double getHoursSpentTotal(Artifact artifact, IWorkPage state) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         AbstractWorkflowArtifact awa = WorkflowManager.cast(artifact);
         double hours = 0.0;
         for (String stateName : awa.getStateMgr().getVisitedStateNames()) {
            hours +=
               HoursSpentStateTotalColumn.getHoursSpentStateTotal(awa, new SimpleTeamState(stateName,
                  WorkPageType.Working));
         }
         return hours;
      }
      return 0;
   }

}

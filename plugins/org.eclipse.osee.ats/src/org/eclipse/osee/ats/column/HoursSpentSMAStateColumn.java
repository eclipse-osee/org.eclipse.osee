/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.ActionManager;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.WorkflowManager;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;
import org.eclipse.swt.SWT;

public class HoursSpentSMAStateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static HoursSpentSMAStateColumn instance = new HoursSpentSMAStateColumn();

   public static HoursSpentSMAStateColumn getInstance() {
      return instance;
   }

   private HoursSpentSMAStateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".stateHoursSpent", "State Hours Spent", 40, SWT.CENTER, false,
         SortDataType.Float, false, "Hours spent in performing the changes to the current state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HoursSpentSMAStateColumn copy() {
      HoursSpentSMAStateColumn newXCol = new HoursSpentSMAStateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return AtsUtil.doubleToI18nString(getHoursSpentSMAState((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentSMAState(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         double hours = 0;
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(artifact)) {
            if (!team.isCancelled()) {
               hours += getHoursSpentSMAState(team);
            }
         }
         return hours;
      }
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return getHoursSpentSMAState(artifact, WorkflowManager.getStateManager(artifact).getCurrentState());
      }
      return 0;
   }

   /**
    * Return hours spent working ONLY the SMA stateName (not children SMAs)
    */
   public static double getHoursSpentSMAState(Artifact artifact, IWorkPage state) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return WorkflowManager.getStateManager(artifact).getHoursSpent(state);
      }
      return 0;
   }

}

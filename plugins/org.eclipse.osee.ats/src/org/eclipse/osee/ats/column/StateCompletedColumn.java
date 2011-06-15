/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.ActionManager;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.StateManager;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

public class StateCompletedColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   private final String stateName;

   public StateCompletedColumn(String stateName) {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + "." + stateName + ".stateCompleted", String.format(
         "State [%s] Completed", stateName), 80, SWT.LEFT, false, SortDataType.String, false, String.format(
         "Date state [%s] was completed", stateName));
      this.stateName = stateName;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public StateCompletedColumn copy() {
      StateCompletedColumn newXCol = new StateCompletedColumn(this.getStateName());
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) element;
            StateDefinition state = awa.getStateDefinitionByName(stateName);
            if (state != null) {
               String date = StateManager.getCompletedDateByState(awa, state);
               return date;
            }
         } else if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> dates = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ActionManager.getTeams(element)) {
               StateDefinition state = team.getStateDefinitionByName(stateName);
               if (state != null) {
                  String date = StateManager.getCompletedDateByState(team, state);
                  if (Strings.isValid(date)) {
                     dates.add(date);
                  }
               }
            }
            return Artifacts.toString(";", dates);

         }
      } catch (OseeCoreException ex) {
         XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   public String getStateName() {
      return stateName;
   }
}

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
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

public class CompletedCancelledByColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static CompletedCancelledByColumn instance = new CompletedCancelledByColumn();

   public static CompletedCancelledByColumn getInstance() {
      return instance;
   }

   private CompletedCancelledByColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".cmpCnclBy", "Completed or Cancelled By", 80, SWT.LEFT, false,
         SortDataType.Date, false, "User transitioning action to completed or cancelled state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public CompletedCancelledByColumn copy() {
      CompletedCancelledByColumn newXCol = new CompletedCancelledByColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            if (((AbstractWorkflowArtifact) element).isCompleted()) {
               return CompletedByColumn.getInstance().getColumnText(element, column, columnIndex);
            } else if (((AbstractWorkflowArtifact) element).isCancelled()) {
               return CancelledByColumn.getInstance().getColumnText(element, column, columnIndex);
            }
         } else if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<User> users = new HashSet<User>();
            for (TeamWorkFlowArtifact team : ActionManager.getTeams(element)) {
               User user = team.getCompletedBy();
               if (((AbstractWorkflowArtifact) element).isCompleted()) {
                  user = team.getCompletedBy();
               } else if (((AbstractWorkflowArtifact) element).isCancelled()) {
                  user = team.getCancelledBy();
               }
               if (user != null) {
                  users.add(user);
               }
            }
            return Artifacts.toString(";", users);

         }
      } catch (OseeCoreException ex) {
         XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }
}

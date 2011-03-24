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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionManager;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

public class CancelledByColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static CancelledByColumn instance = new CancelledByColumn();

   public static CancelledByColumn getInstance() {
      return instance;
   }

   private CancelledByColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".cancelledBy", "Cancelled By", 80, SWT.LEFT, false,
         SortDataType.Date, false, "User transitioning action to cancelled state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public CancelledByColumn copy() {
      CancelledByColumn newXCol = new CancelledByColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            User user = ((AbstractWorkflowArtifact) element).getCancelledBy();
            if (user != null) {
               return user.getName();
            }
         } else if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<User> users = new HashSet<User>();
            for (TeamWorkFlowArtifact team : ActionManager.getTeams(element)) {
               User user = team.getCancelledBy();
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

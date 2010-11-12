/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.swt.SWT;

public class TeamColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static TeamColumn instance = new TeamColumn();

   public static TeamColumn getInstance() {
      return instance;
   }

   private TeamColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".team", "Team", 50, SWT.LEFT, true, SortDataType.String, false,
         "Team that has been assigned to work this Action.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TeamColumn copy() {
      TeamColumn newXCol = new TeamColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      return getName(element);
   }

   public static String getName(Object element) {
      try {
         if (element instanceof TeamWorkFlowArtifact) {
            return ((TeamWorkFlowArtifact) element).getTeamName();
         } else if (element instanceof AbstractWorkflowArtifact) {
            return getName(((AbstractWorkflowArtifact) element).getParentTeamWorkflow());
         }
      } catch (OseeCoreException ex) {
         XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }
}

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

public class ParentIdColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static ParentIdColumn instance = new ParentIdColumn();

   public static ParentIdColumn getInstance() {
      return instance;
   }

   private ParentIdColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".parenthrid", "Parent HRID", 75, SWT.LEFT, false,
         SortDataType.String, false, "Human Readable ID of Parent Action or Team Workflow");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ParentIdColumn copy() {
      ParentIdColumn newXCol = new ParentIdColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof TeamWorkFlowArtifact) {
            return ((TeamWorkFlowArtifact) element).getParentActionArtifact().getHumanReadableId();
         } else if (element instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) element).getParentSMA() != null) {
            return ((AbstractWorkflowArtifact) element).getParentSMA().getHumanReadableId();
         }

      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}

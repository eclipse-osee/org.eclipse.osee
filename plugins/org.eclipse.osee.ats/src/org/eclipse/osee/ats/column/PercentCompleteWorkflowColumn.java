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
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.swt.SWT;

public class PercentCompleteWorkflowColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteWorkflowColumn instance = new PercentCompleteWorkflowColumn();

   public static PercentCompleteWorkflowColumn getInstance() {
      return instance;
   }

   private PercentCompleteWorkflowColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".workflowPercentComplete", "Workflow Percent Complete", 40,
         SWT.CENTER, false, SortDataType.Percent, false,
         "Percent Complete for full workflow (if work definition configured for single percent).\n\nAmount entered from user.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteWorkflowColumn copy() {
      PercentCompleteWorkflowColumn newXCol = new PercentCompleteWorkflowColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            int awaPercent =
               ((AbstractWorkflowArtifact) element).getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
            return String.valueOf(awaPercent);
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}

/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.core.workflow.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.SWT;

public class PercentCompleteTotalColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteTotalColumn instance = new PercentCompleteTotalColumn();

   public static PercentCompleteTotalColumn getInstance() {
      return instance;
   }

   private PercentCompleteTotalColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".totalPercentComplete", "Total Percent Complete", 40, SWT.CENTER,
         false, SortDataType.Percent, false, "Percent Complete for the reviews related to the current state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteTotalColumn copy() {
      PercentCompleteTotalColumn newXCol = new PercentCompleteTotalColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return String.valueOf(PercentCompleteTotalUtil.getPercentCompleteTotal((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}

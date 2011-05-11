/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.HoursSpentUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.SWT;

public class HoursSpentStateTotalColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static HoursSpentStateTotalColumn instance = new HoursSpentStateTotalColumn();

   public static HoursSpentStateTotalColumn getInstance() {
      return instance;
   }

   private HoursSpentStateTotalColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".stateTotalHoursSpent", "State Total Hours Spent", 40, SWT.CENTER,
         false, SortDataType.Float, false, "Hours spent for all work related to the current state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HoursSpentStateTotalColumn copy() {
      HoursSpentStateTotalColumn newXCol = new HoursSpentStateTotalColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return AtsUtilCore.doubleToI18nString(HoursSpentUtil.getHoursSpentStateTotal((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}

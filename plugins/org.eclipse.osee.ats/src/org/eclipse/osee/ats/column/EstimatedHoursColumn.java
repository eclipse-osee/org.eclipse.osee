/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.EstimatedHoursUtil;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.SWT;

public class EstimatedHoursColumn extends XViewerAtsAttributeValueColumn {

   public static EstimatedHoursColumn instance = new EstimatedHoursColumn();

   public static EstimatedHoursColumn getInstance() {
      return instance;
   }

   private EstimatedHoursColumn() {
      super(AtsAttributeTypes.EstimatedHours, WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedHours",
         AtsAttributeTypes.EstimatedHours.getUnqualifiedName(), 40, SWT.CENTER, false, SortDataType.Float, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EstimatedHoursColumn copy() {
      EstimatedHoursColumn newXCol = new EstimatedHoursColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return AtsUtilCore.doubleToI18nString(EstimatedHoursUtil.getEstimatedHours(element));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return super.getColumnText(element, column, columnIndex);
   }

}

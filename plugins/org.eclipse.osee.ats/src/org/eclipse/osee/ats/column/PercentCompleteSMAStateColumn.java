/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.core.workflow.PercentCompleteSMAStateUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.SWT;

public class PercentCompleteSMAStateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteSMAStateColumn instance = new PercentCompleteSMAStateColumn();

   public static PercentCompleteSMAStateColumn getInstance() {
      return instance;
   }

   private PercentCompleteSMAStateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".statePercentComplete", "State Percent Complete", 40, SWT.CENTER,
         false, SortDataType.Percent, false,
         "Percent Complete for the changes to the current state.\n\nAmount entered from user.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteSMAStateColumn copy() {
      PercentCompleteSMAStateColumn newXCol = new PercentCompleteSMAStateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return String.valueOf(PercentCompleteSMAStateUtil.getPercentCompleteSMAState((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}

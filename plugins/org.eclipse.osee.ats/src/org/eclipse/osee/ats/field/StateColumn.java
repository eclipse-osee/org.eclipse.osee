/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.swt.SWT;

public class StateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static StateColumn instance = new StateColumn();

   public static StateColumn getInstance() {
      return instance;
   }

   private StateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".state", "State", 75, SWT.LEFT, true, SortDataType.String, false,
         null);
   }

   public StateColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public StateColumn copy() {
      return new StateColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IWorldViewArtifact) {
            return ((IWorldViewArtifact) element).getState();
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}

/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.swt.SWT;

public class ParentStateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static ParentStateColumn instance = new ParentStateColumn();

   public static ParentStateColumn getInstance() {
      return instance;
   }

   private ParentStateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".parentstate", "Parent State", 75, SWT.LEFT, false,
         SortDataType.String, false, "State of the Parent Team Workflow or Action");
   }

   public ParentStateColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ParentStateColumn copy() {
      return new ParentStateColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) element).getParentSMA() != null) {
            return ((AbstractWorkflowArtifact) element).getParentSMA().getStateMgr().getCurrentStateName();
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}

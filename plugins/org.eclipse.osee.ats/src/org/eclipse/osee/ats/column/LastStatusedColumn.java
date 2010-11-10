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
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.swt.SWT;

public class LastStatusedColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static LastStatusedColumn instance = new LastStatusedColumn();

   public static LastStatusedColumn getInstance() {
      return instance;
   }

   private LastStatusedColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".lastStatused", "Last Statused", 40, SWT.CENTER, false,
         SortDataType.Date, false, "Retrieves timestamp of status (percent completed or hours spent).");
   }

   public LastStatusedColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LastStatusedColumn copy() {
      return new LastStatusedColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            return DateUtil.getMMDDYYHHMM(((AbstractWorkflowArtifact) element).getLog().getLastStatusedDate());
         } else if (element instanceof ActionArtifact) {
            return "(see children)";
         }
      } catch (OseeCoreException ex) {
         XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }
}

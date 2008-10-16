/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerSmaCreatedDateColumn extends XViewerValueColumn {

   public XViewerSmaCreatedDateColumn() {
      this("Created Date");
   }

   public XViewerSmaCreatedDateColumn(String name) {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".createdDate", name, 80, SWT.LEFT, true, SortDataType.Date, false,
            "Date this workflow was created.");
   }

   public XViewerSmaCreatedDateColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   public XViewerSmaCreatedDateColumn copy() {
      return new XViewerSmaCreatedDateColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
            isMultiColumnEditable(), getDescription());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException {
      if (element instanceof IWorldViewArtifact) {
         return ((IWorldViewArtifact) element).getWorldViewCreatedDateStr();
      }
      return super.getColumnText(element, column, columnIndex);
   }

}

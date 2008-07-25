/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XViewerArtifactTypeColumn extends XViewerValueColumn {

   public XViewerArtifactTypeColumn(String name) {
      this("framework.artifact.type." + name, name, 150, SWT.LEFT, true, SortDataType.String, false, "Artifact Type");
   }

   public XViewerArtifactTypeColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    * 
    * @param col
    */
   public XViewerArtifactTypeColumn copy() {
      return new XViewerArtifactTypeColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
            isMultiColumnEditable(), getDescription());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException, SQLException {
      if (element instanceof Artifact) {
         return ((Artifact) element).getArtifactTypeName();
      }
      return super.getColumnText(element, column, columnIndex);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnImage(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException, SQLException {
      if (element instanceof Artifact) {
         return ((Artifact) element).getImage();
      }
      return super.getColumnImage(element, column, columnIndex);
   }

}

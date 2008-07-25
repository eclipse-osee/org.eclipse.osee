/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerAttributeColumn extends XViewerValueColumn {

   private final String attributeTypeName;

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   public XViewerAttributeColumn copy() {
      return new XViewerAttributeColumn(getId(), getName(), getAttributeTypeName(), getWidth(), getAlign(), isShow(),
            getSortDataType(), isMultiColumnEditable(), getDescription());
   }

   public XViewerAttributeColumn(String id, String name, String attributeTypeName, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.attributeTypeName = attributeTypeName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException, SQLException {
      if (element instanceof Artifact) {
         return ((Artifact) element).getAttributesToString(attributeTypeName);
      }
      if (element instanceof Change) {
         return ((Change) element).getArtifact().getAttributesToString(attributeTypeName);
      }
      if (element instanceof Conflict) {
         return ((Conflict) element).getArtifact().getAttributesToString(attributeTypeName);
      }
      return super.getColumnText(element, column, columnIndex);
   }

   public String getAttributeTypeName() {
      return attributeTypeName;
   }
}

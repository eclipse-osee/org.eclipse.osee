/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.SWT;

public class TitleColumn extends XViewerAtsAttributeValueColumn {

   public static TitleColumn instance = new TitleColumn();

   public static TitleColumn getInstance() {
      return instance;
   }

   public TitleColumn() {
      super("framework.artifact.name.Title", CoreAttributeTypes.Name, "Title", 150, SWT.LEFT, true,
         SortDataType.String, false);
   }

   public TitleColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TitleColumn copy() {
      return new TitleColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof Artifact && ((Artifact) element).isDeleted()) {
         return "<deleted>";
      }
      return super.getColumnText(element, column, columnIndex);
   }

}

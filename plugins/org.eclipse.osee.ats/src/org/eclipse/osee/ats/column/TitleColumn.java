/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.SWT;

public class TitleColumn extends XViewerAtsAttributeValueColumn {

   public static TitleColumn instance = new TitleColumn();

   public static TitleColumn getInstance() {
      return instance;
   }

   private TitleColumn() {
      super(CoreAttributeTypes.Name, "framework.artifact.name.Title", "Title", 150, SWT.LEFT, true,
         SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TitleColumn copy() {
      TitleColumn newXCol = new TitleColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof Artifact && ((Artifact) element).isDeleted()) {
         return "<deleted>";
      }
      return super.getColumnText(element, column, columnIndex);
   }

}

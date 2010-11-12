/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.swt.SWT;

public class DescriptionColumn extends XViewerAtsAttributeValueColumn {

   public static DescriptionColumn instance = new DescriptionColumn();

   public static DescriptionColumn getInstance() {
      return instance;
   }

   private DescriptionColumn() {
      super(AtsAttributeTypes.Description, 150, SWT.LEFT, false, SortDataType.String, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public DescriptionColumn copy() {
      DescriptionColumn newXCol = new DescriptionColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean isMultiLineStringAttribute() {
      return true;
   }

}

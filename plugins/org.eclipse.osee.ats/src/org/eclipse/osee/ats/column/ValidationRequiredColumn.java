/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.swt.SWT;

public class ValidationRequiredColumn extends XViewerAtsAttributeValueColumn {

   public static ValidationRequiredColumn instance = new ValidationRequiredColumn();

   public static ValidationRequiredColumn getInstance() {
      return instance;
   }

   private ValidationRequiredColumn() {
      super(AtsAttributeTypes.ValidationRequired, WorldXViewerFactory.COLUMN_NAMESPACE + ".validationRequired",
         AtsAttributeTypes.ValidationRequired.getUnqualifiedName(), 80, SWT.LEFT, false, SortDataType.String, false,
         "If set, Originator will be asked to perform a review to\nensure changes are as expected.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ValidationRequiredColumn copy() {
      ValidationRequiredColumn newXCol = new ValidationRequiredColumn();
      copy(this, newXCol);
      return newXCol;
   }

}

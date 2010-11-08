/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.swt.SWT;

public class EstimatedHoursColumn extends XViewerAtsAttributeValueColumn implements IMultiColumnEditProvider {

   public static final IAttributeType EstimatedHours =
      new AtsAttributeTypes(
         "AAMFEdCSqBh+cPyadiwA",
         "Estimated Hours",
         "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static EstimatedHoursColumn instance = new EstimatedHoursColumn();

   public static EstimatedHoursColumn getInstance() {
      return instance;
   }

   private EstimatedHoursColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedHours", EstimatedHours, 40, SWT.CENTER, false,
         SortDataType.Float, true);
   }

   public EstimatedHoursColumn(String id, IAttributeType attributeType, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, attributeType, name, width, align, show, sortDataType, multiColumnEditable);
      setDescription(description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EstimatedHoursColumn copy() {
      return new EstimatedHoursColumn(getId(), getAttributeType(), getName(), getWidth(), getAlign(), isShow(),
         getSortDataType(), isMultiColumnEditable(), getDescription());
   }

}

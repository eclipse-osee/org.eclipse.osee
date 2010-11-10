/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.SWT;

public class WorkPackageColumn extends XViewerAtsAttributeValueColumn {

   public static WorkPackageColumn instance = new WorkPackageColumn();

   public static WorkPackageColumn getInstance() {
      return instance;
   }

   public WorkPackageColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".workPackage", AtsAttributeTypes.WorkPackage, 80, SWT.LEFT, false,
         SortDataType.String, true);
   }

   public WorkPackageColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkPackageColumn copy() {
      return new WorkPackageColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof TaskArtifact) {
         try {
            return getColumnText(((TaskArtifact) element).getParentTeamWorkflow(), column, columnIndex);
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         }
      }
      return super.getColumnText(element, column, columnIndex);
   }
}

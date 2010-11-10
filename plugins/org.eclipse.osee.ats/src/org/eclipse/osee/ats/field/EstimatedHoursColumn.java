/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.SWT;

public class EstimatedHoursColumn extends XViewerAtsAttributeValueColumn {

   public static EstimatedHoursColumn instance = new EstimatedHoursColumn();

   public static EstimatedHoursColumn getInstance() {
      return instance;
   }

   private EstimatedHoursColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedHours", AtsAttributeTypes.EstimatedHours, 40, SWT.CENTER,
         false, SortDataType.Float, true);
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

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            return AtsUtil.doubleToI18nString(((AbstractWorkflowArtifact) element).getEstimatedHoursTotal());
         } else if (element instanceof ActionArtifact) {
            double total = 0;
            for (TeamWorkFlowArtifact team : ((ActionArtifact) element).getTeamWorkFlowArtifacts()) {
               total += team.getEstimatedHoursTotal();
            }
            return AtsUtil.doubleToI18nString(total);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return super.getColumnText(element, column, columnIndex);
   }

}

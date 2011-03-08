/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

public class EstimatedHoursColumn extends XViewerAtsAttributeValueColumn {

   public static EstimatedHoursColumn instance = new EstimatedHoursColumn();

   public static EstimatedHoursColumn getInstance() {
      return instance;
   }

   private EstimatedHoursColumn() {
      super(AtsAttributeTypes.EstimatedHours, WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedHours",
         AtsAttributeTypes.EstimatedHours.getUnqualifiedName(), 40, SWT.CENTER, false, SortDataType.Float, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EstimatedHoursColumn copy() {
      EstimatedHoursColumn newXCol = new EstimatedHoursColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return AtsUtil.doubleToI18nString(getEstimatedHours(element));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return super.getColumnText(element, column, columnIndex);
   }

   public static double getEstimatedHours(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         return ((AbstractWorkflowArtifact) object).getEstimatedHoursTotal();
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         double total = 0;
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(object)) {
            total += team.getEstimatedHoursTotal();
         }
         return total;
      }
      return 0.0;
   }
}

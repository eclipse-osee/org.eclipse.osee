/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class RemainingHoursColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider {

   public static RemainingHoursColumn instance = new RemainingHoursColumn();

   public static RemainingHoursColumn getInstance() {
      return instance;
   }

   private RemainingHoursColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".remainingHours", "Remaining Hours", 40, SWT.CENTER, false,
         SortDataType.Float, false,
         "Hours that remain to complete the changes.\n\nEstimated Hours - (Estimated Hours * Percent Complete).");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RemainingHoursColumn copy() {
      RemainingHoursColumn newXCol = new RemainingHoursColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof AbstractWorkflowArtifact) {
         try {
            Result result = RemainingHoursColumn.isRemainingHoursValid(element);
            if (result.isFalse()) {
               return result.getText();
            }
            return AtsUtil.doubleToI18nString(getRemainingHours(element));
         } catch (OseeCoreException ex) {
            XViewerCells.getCellExceptionString(ex);
         }
      }
      return "";
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         AbstractWorkflowArtifact aba = null;
         if (treeItem.getData() instanceof AbstractWorkflowArtifact) {
            aba = (AbstractWorkflowArtifact) treeItem.getData();
         } else if (treeItem.getData() instanceof ActionArtifact && ((ActionArtifact) treeItem.getData()).getTeamWorkFlowArtifacts().size() == 1) {
            aba = ((ActionArtifact) treeItem.getData()).getTeamWorkFlowArtifacts().iterator().next();
         }
         if (aba != null) {
            AWorkbench.popup("Calculated Field",
               "Hours Remaining field is calculated.\nHour Estimate - (Hour Estimate * Percent Complete)");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static Result isRemainingHoursValid(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact aba = (AbstractWorkflowArtifact) object;
         if (!aba.isAttributeTypeValid(AtsAttributeTypes.EstimatedHours)) {
            return Result.TrueResult;
         }
         try {
            Double value = aba.getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, null);
            if (aba.isCancelled()) {
               return Result.TrueResult;
            }
            if (value == null) {
               return new Result("Estimated Hours not set.");
            }
            return Result.TrueResult;
         } catch (Exception ex) {
            return new Result(
               ex.getClass().getName() + ": " + ex.getLocalizedMessage() + "\n\n" + Lib.exceptionToString(ex));
         }
      } else if (object instanceof ActionArtifact) {
         for (TeamWorkFlowArtifact team : ((ActionArtifact) object).getTeamWorkFlowArtifacts()) {
            if (!isRemainingHoursValid(team).isFalse()) {
               return Result.FalseResult;
            }
         }
      }
      return Result.FalseResult;
   }

   public static double getRemainingHours(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         return ((AbstractWorkflowArtifact) object).getRemainHoursTotal();
      } else if (object instanceof ActionArtifact) {
         double hours = 0;
         // Add up hours for all children
         for (TeamWorkFlowArtifact team : ((ActionArtifact) object).getTeamWorkFlowArtifacts()) {
            hours += getRemainingHours(team);
         }
         return hours;
      }
      return 0;
   }

}

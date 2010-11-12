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
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class WorkDaysNeededColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider {

   public static WorkDaysNeededColumn instance = new WorkDaysNeededColumn();

   public static WorkDaysNeededColumn getInstance() {
      return instance;
   }

   private WorkDaysNeededColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".workDaysNeeded", "Hours Per Work Day", 40, SWT.CENTER, false,
         SortDataType.Float, false, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkDaysNeededColumn copy() {
      WorkDaysNeededColumn newXCol = new WorkDaysNeededColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof AbstractWorkflowArtifact) {
         try {
            Result result = isWorldViewManDaysNeededValid(element);
            if (result.isFalse()) {
               return result.getText();
            }
            return AtsUtil.doubleToI18nString(getWorldViewManDaysNeeded(element));
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
            AWorkbench.popup(
               "Calculated Field",
               "Work Days Needed field is calculated.\nRemaining Hours / Hours per Week (" + aba.getManHrsPerDayPreference() + ")");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static Result isWorldViewManDaysNeededValid(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact aba = (AbstractWorkflowArtifact) object;
         Result result = RemainingHoursColumn.isRemainingHoursValid(aba);
         if (result.isFalse()) {
            return result;
         }
         if (aba.getManHrsPerDayPreference() == 0) {
            return new Result("Man Day Hours Preference is not set.");
         }

         return Result.TrueResult;
      } else if (object instanceof ActionArtifact) {
         for (TeamWorkFlowArtifact team : ((ActionArtifact) object).getTeamWorkFlowArtifacts()) {
            if (!isWorldViewManDaysNeededValid(team).isFalse()) {
               return Result.FalseResult;
            }
         }
      }
      return Result.FalseResult;
   }

   public static double getWorldViewManDaysNeeded(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         double hrsRemain = RemainingHoursColumn.getRemainingHours(object);
         double manDaysNeeded = 0;
         if (hrsRemain != 0) {
            manDaysNeeded = hrsRemain / ((AbstractWorkflowArtifact) object).getManHrsPerDayPreference();
         }
         return manDaysNeeded;
      } else if (object instanceof ActionArtifact) {
         double hours = 0;
         // Add up hours for all children
         for (TeamWorkFlowArtifact team : ((ActionArtifact) object).getTeamWorkFlowArtifacts()) {
            hours += getWorldViewManDaysNeeded(team);
         }
         return hours;
      }
      return 0;
   }
}

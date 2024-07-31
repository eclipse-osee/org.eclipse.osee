/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class RemainingHoursColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static RemainingHoursColumnUI instance = new RemainingHoursColumnUI();

   public static RemainingHoursColumnUI getInstance() {
      return instance;
   }

   private RemainingHoursColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".remainingHours", "Remaining Hours", 40, XViewerAlign.Center, false,
         SortDataType.Float, false,
         "Hours that remain to complete the changes.\n\nEstimated Hours - (Estimated Hours * Percent Complete).");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public RemainingHoursColumnUI copy() {
      RemainingHoursColumnUI newXCol = new RemainingHoursColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof AbstractWorkflowArtifact) {
         try {
            Result result = RemainingHoursColumnUI.isRemainingHoursValid(element);
            if (result.isFalse()) {
               return result.getText();
            }
            return AtsUtil.doubleToI18nString(getRemainingHours(element));
         } catch (OseeCoreException ex) {
            LogUtil.getCellExceptionString(ex);
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
         } else if (Artifacts.isOfType(treeItem.getData(),
            AtsArtifactTypes.Action) && AtsApiService.get().getWorkItemService().getTeams(
               treeItem.getData()).size() == 1) {
            aba = (AbstractWorkflowArtifact) AtsApiService.get().getWorkItemService().getFirstTeam(
               treeItem.getData()).getStoreObject();
         }
         if (aba != null) {
            AWorkbench.popup("Calculated Field",
               "Hours Remaining field is calculated.\nHour Estimate - (Hour Estimate * Percent Complete)");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static Result isRemainingHoursValid(Object object) {
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
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(object)) {
            if (!isRemainingHoursValid(team).isFalse()) {
               return Result.FalseResult;
            }
         }
      }
      return Result.FalseResult;
   }

   public static double getRemainingHours(Object object) {
      if (object instanceof AbstractWorkflowArtifact) {
         return AtsApiService.get().getEarnedValueService().getRemainHoursTotal((AbstractWorkflowArtifact) object);
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         double hours = 0;
         // Add up hours for all children
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(object)) {
            hours += getRemainingHours(team);
         }
         return hours;
      }
      return 0;
   }
}

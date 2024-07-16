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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class AnnualCostAvoidanceColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static AnnualCostAvoidanceColumnUI instance = new AnnualCostAvoidanceColumnUI();

   public static AnnualCostAvoidanceColumnUI getInstance() {
      return instance;
   }

   private AnnualCostAvoidanceColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".annualCostAvoidance", "Annual Cost Avoidance", 50,
         XViewerAlign.Left, false, SortDataType.Float, false,
         "Hours that would be saved for the first year if this change were completed.\n\n" + "(Weekly Benefit Hours * 52 weeks) - Remaining Hours\n\n" + "If number is high, benefit is great given hours remaining.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AnnualCostAvoidanceColumnUI copy() {
      AnnualCostAvoidanceColumnUI newXCol = new AnnualCostAvoidanceColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         Result result = isWorldViewAnnualCostAvoidanceValid(element);
         if (result.isFalse()) {
            return result.getText();
         }
         return AtsUtil.doubleToI18nString(getWorldViewAnnualCostAvoidance(element), true);
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   public static double getWorldViewAnnualCostAvoidance(Object object) {
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         double hours = 0;
         // Add up hours for all children
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(object)) {
            if (!team.isCompleted() && !team.isCancelled()) {
               hours += getWorldViewAnnualCostAvoidance(team);
            }
         }
         return hours;
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.TeamWorkflow)) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) object;
         double benefit = getWorldViewWeeklyBenefit(teamArt);
         double remainHrs = AtsApiService.get().getEarnedValueService().getRemainHoursTotal(teamArt);
         return benefit * 52 - remainHrs;
      } else if (object instanceof IAtsWorkItem) {
         return getWorldViewAnnualCostAvoidance(((IAtsWorkItem) object).getStoreObject());
      }
      return 0;
   }

   public static double getWorldViewWeeklyBenefit(TeamWorkFlowArtifact teamArt) {
      if (teamArt.isAttributeTypeValid(AtsAttributeTypes.WeeklyBenefit)) {
         return 0;
      }
      String value = teamArt.getSoleAttributeValue(AtsAttributeTypes.WeeklyBenefit, "");
      if (!Strings.isValid(value)) {
         return 0;
      }
      return Double.valueOf(value);
   }

   public static Result isWorldViewAnnualCostAvoidanceValid(Object object) {
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(object)) {
            Result result = isWorldViewAnnualCostAvoidanceValid(team);
            if (result.isFalse()) {
               return result;
            }
         }
      }
      if (object instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact artifact = (AbstractWorkflowArtifact) object;
         if (artifact.isAttributeTypeValid(AtsAttributeTypes.WeeklyBenefit)) {
            return Result.TrueResult;
         }
         Result result = RemainingHoursColumnUI.isRemainingHoursValid(artifact);
         if (result.isFalse()) {
            return result;
         }
         String value = null;
         try {
            value = artifact.getSoleAttributeValue(AtsAttributeTypes.WeeklyBenefit, "");
            if (!Strings.isValid(value)) {
               return new Result("Weekly Benefit Hours not set.");
            }
            double val = Double.valueOf(value);
            if (val == 0) {
               return new Result("Weekly Benefit Hours not set.");
            }
         } catch (NumberFormatException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "ID " + artifact.getAtsId(), ex);
            return new Result("Weekly Benefit value is invalid double \"" + value + "\"");
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "ID " + artifact.getAtsId(), ex);
            return new Result("Exception calculating cost avoidance.  See log for details.");
         }
         return Result.TrueResult;
      }
      return Result.FalseResult;
   }
}

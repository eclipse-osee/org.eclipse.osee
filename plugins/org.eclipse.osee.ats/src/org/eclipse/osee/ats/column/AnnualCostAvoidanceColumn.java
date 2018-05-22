/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
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
public class AnnualCostAvoidanceColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static AnnualCostAvoidanceColumn instance = new AnnualCostAvoidanceColumn();

   public static AnnualCostAvoidanceColumn getInstance() {
      return instance;
   }

   private AnnualCostAvoidanceColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".annualCostAvoidance", "Annual Cost Avoidance", 50,
         XViewerAlign.Left, false, SortDataType.Float, false,
         "Hours that would be saved for the first year if this change were completed.\n\n" + "(Weekly Benefit Hours * 52 weeks) - Remaining Hours\n\n" + "If number is high, benefit is great given hours remaining.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AnnualCostAvoidanceColumn copy() {
      AnnualCostAvoidanceColumn newXCol = new AnnualCostAvoidanceColumn();
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
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(object)) {
            if (!team.isCompleted() && !team.isCancelled()) {
               hours += getWorldViewAnnualCostAvoidance(team);
            }
         }
         return hours;
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.TeamWorkflow)) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) object;
         double benefit = getWorldViewWeeklyBenefit(teamArt);
         double remainHrs = teamArt.getRemainHoursTotal();
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
      return new Float(value).doubleValue();
   }

   public static Result isWorldViewAnnualCostAvoidanceValid(Object object) {
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(object)) {
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
         Result result = RemainingHoursColumn.isRemainingHoursValid(artifact);
         if (result.isFalse()) {
            return result;
         }
         String value = null;
         try {
            value = artifact.getSoleAttributeValue(AtsAttributeTypes.WeeklyBenefit, "");
            if (!Strings.isValid(value)) {
               return new Result("Weekly Benefit Hours not set.");
            }
            double val = new Float(value).doubleValue();
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

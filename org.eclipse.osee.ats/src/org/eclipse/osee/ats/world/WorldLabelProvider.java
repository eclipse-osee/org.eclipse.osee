/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class WorldLabelProvider extends XViewerLabelProvider implements ITableColorProvider {

   private final WorldXViewer treeViewer;
   protected Font font;

   public WorldLabelProvider(WorldXViewer treeViewer) {
      super(treeViewer);
      this.treeViewer = treeViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (!(element instanceof IWorldViewArtifact)) return null;
         IWorldViewArtifact wva = (IWorldViewArtifact) element;
         if (xCol == WorldXViewerFactory.Type_Col)
            return ((Artifact) wva).getImage();
         else if (xCol == WorldXViewerFactory.Change_Type_Col)
            return wva.getWorldViewChangeType().getImage();
         else if (xCol == WorldXViewerFactory.Assignees_Col)
            return wva.getAssigneeImage();
         else if (xCol == WorldXViewerFactory.Deadline_Col) {
            if (wva.isWorldViewDeadlineAlerting().isTrue()) return AtsPlugin.getInstance().getImage("warn.gif");
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (!(element instanceof IWorldViewArtifact)) return "";
         IWorldViewArtifact wva = (IWorldViewArtifact) element;
         if (xCol == WorldXViewerFactory.Type_Col) return wva.getWorldViewType();
         if (xCol == WorldXViewerFactory.Actionable_Items_Col) return wva.getWorldViewActionableItems();
         if (xCol == WorldXViewerFactory.User_Community_Col) return wva.getWorldViewUserCommunity();
         if (xCol == WorldXViewerFactory.Title_Col) return wva.getWorldViewTitle();
         if (xCol == WorldXViewerFactory.Number_of_Tasks_Col) return wva.getWorldViewNumberOfTasks();
         if (xCol == WorldXViewerFactory.Last_Modified_Col) return wva.getWorldViewLastUpdated();
         if (xCol == WorldXViewerFactory.Last_Statused_Col) return wva.getWorldViewLastStatused();
         if (xCol == WorldXViewerFactory.Description_Col) return wva.getWorldViewDescription();
         if (xCol == WorldXViewerFactory.Validation_Required_Col) return wva.getWorldViewValidationRequiredStr();
         if (xCol == WorldXViewerFactory.Version_Target_Col) return wva.getWorldViewVersion();
         if (xCol == WorldXViewerFactory.Completed_Date_Col) return wva.getWorldViewCompletedDateStr();
         if (xCol == WorldXViewerFactory.Cancelled_Date_Col) return wva.getWorldViewCancelledDateStr();
         if (xCol == WorldXViewerFactory.Team_Col) return wva.getWorldViewTeam();
         if (xCol == WorldXViewerFactory.Related_To_State_Col) return wva.getWorldViewRelatedToState();
         if (xCol == WorldXViewerFactory.Originator_Col) return wva.getWorldViewOriginator();
         if (xCol == WorldXViewerFactory.Branch_Status_Col) return wva.getWorldViewBranchStatus();
         if (xCol == WorldXViewerFactory.Implementor_Col) return wva.getWorldViewImplementer();
         if (xCol == WorldXViewerFactory.Review_Author_Col) return wva.getWorldViewReviewAuthor();
         if (xCol == WorldXViewerFactory.Review_Moderator_Col) return wva.getWorldViewReviewModerator();
         if (xCol == WorldXViewerFactory.Review_Reviewer_Col) return wva.getWorldViewReviewReviewer();
         if (xCol == WorldXViewerFactory.Review_Decider_Col) return wva.getWorldViewReviewDecider();
         if (xCol == WorldXViewerFactory.Change_Type_Col) return wva.getWorldViewChangeTypeStr();
         if (xCol == WorldXViewerFactory.State_Col) return wva.getWorldViewState();
         if (xCol == WorldXViewerFactory.Assignees_Col) return wva.getWorldViewActivePoc();
         if (xCol == WorldXViewerFactory.Created_Date_Col) return wva.getWorldViewCreatedDateStr();
         if (xCol == WorldXViewerFactory.ID_Col) return wva.getWorldViewID();
         if (xCol == WorldXViewerFactory.Priority_Col) return wva.getWorldViewPriority();
         if (xCol == WorldXViewerFactory.Resolution_Col) return wva.getWorldViewResolution();
         if (xCol == WorldXViewerFactory.Decision_Col) return wva.getWorldViewDecision();
         if (xCol == WorldXViewerFactory.Legacy_PCR_Col) return wva.getWorldViewLegacyPCR();
         if (xCol == WorldXViewerFactory.Weekly_Benefit_Hrs_Col) return AtsLib.doubleToStrString(
               wva.getWorldViewWeeklyBenefit(), true);
         if (xCol == WorldXViewerFactory.Estimated_Hours_Col) return AtsLib.doubleToStrString(wva.getWorldViewEstimatedHours());
         if (xCol == WorldXViewerFactory.Annual_Cost_Avoidance_Col) {
            Result result = wva.isWorldViewAnnualCostAvoidanceValid();
            if (result.isFalse()) return result.getText();
            return AtsLib.doubleToStrString(wva.getWorldViewAnnualCostAvoidance(), true);
         }
         if (xCol == WorldXViewerFactory.Remaining_Hours_Col) {
            Result result = wva.isWorldViewRemainHoursValid();
            if (result.isFalse()) return result.getText();
            return AtsLib.doubleToStrString(wva.getWorldViewRemainHours());
         }
         if (xCol == WorldXViewerFactory.Man_Days_Needed_Col) {
            Result result = wva.isWorldViewManDaysNeededValid();
            if (result.isFalse()) return result.getText();
            return AtsLib.doubleToStrString(wva.getWorldViewManDaysNeeded());
         }
         if (xCol == WorldXViewerFactory.Percent_Complete_State_Col) return AtsLib.doubleToStrString(wva.getWorldViewPercentCompleteState());
         if (xCol == WorldXViewerFactory.Percent_Complete_State_Task_Col) return AtsLib.doubleToStrString(wva.getWorldViewPercentCompleteStateTask());
         if (xCol == WorldXViewerFactory.Percent_Complete_State_Review_Col) return AtsLib.doubleToStrString(wva.getWorldViewPercentCompleteStateReview());
         if (xCol == WorldXViewerFactory.Percent_Complete_Total_Col) return AtsLib.doubleToStrString(wva.getWorldViewPercentCompleteTotal());
         if (xCol == WorldXViewerFactory.Hours_Spent_State_Col) return AtsLib.doubleToStrString(wva.getWorldViewHoursSpentState());
         if (xCol == WorldXViewerFactory.Hours_Spent_State_Task_Col) return AtsLib.doubleToStrString(wva.getWorldViewHoursSpentStateTask());
         if (xCol == WorldXViewerFactory.Hours_Spent_State_Review_Col) return AtsLib.doubleToStrString(wva.getWorldViewHoursSpentStateReview());
         if (xCol == WorldXViewerFactory.Hours_Spent_Total_Col) return AtsLib.doubleToStrString(wva.getWorldViewHoursSpentStateTotal());

         if (xCol == WorldXViewerFactory.Total_Hours_Spent_Col) return AtsLib.doubleToStrString(wva.getWorldViewHoursSpentTotal());

         if (xCol == WorldXViewerFactory.Notes_Col) return wva.getWorldViewNotes();
         if (xCol == WorldXViewerFactory.Percent_Rework_Col) return wva.getWorldViewPercentReworkStr();
         if (xCol == WorldXViewerFactory.Estimated_Release_Date_Col) return wva.getWorldViewEstimatedReleaseDateStr();
         if (xCol == WorldXViewerFactory.Release_Date_Col) return wva.getWorldViewReleaseDateStr();
         if (xCol == WorldXViewerFactory.Deadline_Col) return wva.getWorldViewDeadlineDateStr();
         if (xCol == WorldXViewerFactory.Work_Package_Col) return wva.getWorldViewWorkPackage();
         if (xCol == WorldXViewerFactory.Category_Col) return wva.getWorldViewCategory();
         if (xCol == WorldXViewerFactory.Category2_Col) return wva.getWorldViewCategory2();
         if (xCol == WorldXViewerFactory.Category3_Col) return wva.getWorldViewCategory3();

         return "Unhandled Column";
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public WorldXViewer getTreeViewer() {
      return treeViewer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
    */
   public Color getBackground(Object element, int columnIndex) {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
    */
   public Color getForeground(Object element, int columnIndex) {
      return null;
   }

}

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
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class WorldLabelProvider implements ITableLabelProvider, ITableColorProvider {
   Font font = null;

   private final WorldXViewer treeViewer;

   public WorldLabelProvider(WorldXViewer treeViewer) {
      super();
      this.treeViewer = treeViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      Artifact artifact = ((WorldArtifactItem) element).getArtifact();
      if (artifact == null || artifact.isDeleted()) return "";
      XViewerColumn xCol = treeViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         AtsXColumn aCol = AtsXColumn.getAtsXColumn(xCol);
         return getColumnText(element, columnIndex, artifact, xCol, aCol);
      }
      return "";
   }

   /**
    * Provided as optimization of subclassed classes so provider doesn't have to retrieve the same information that has
    * already been retrieved
    * 
    * @param element
    * @param columnIndex
    * @param artifact
    * @param xCol
    * @param aCol
    * @return column string
    */
   public String getColumnText(Object element, int columnIndex, Artifact artifact, XViewerColumn xCol, AtsXColumn aCol) {
      if (!xCol.isShow()) return ""; // Since not shown, don't display
      IWorldViewArtifact wva = (IWorldViewArtifact) artifact;
      if (aCol == AtsXColumn.Type_Col) return wva.getWorldViewType();
      if (aCol == AtsXColumn.Actionable_Items_Col) return wva.getWorldViewActionableItems();
      if (aCol == AtsXColumn.User_Community_Col) return wva.getWorldViewUserCommunity();
      if (aCol == AtsXColumn.Title_Col) return wva.getWorldViewTitle();
      if (aCol == AtsXColumn.Number_of_Tasks_Col) return wva.getWorldViewNumberOfTasks();
      if (aCol == AtsXColumn.Description_Col) return wva.getWorldViewDescription();
      if (aCol == AtsXColumn.Metrics_from_Tasks_Col) {
         try {
            return wva.isMetricsFromTasks() ? "yes" : "";
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return ex.getLocalizedMessage();
         }
      }
      if (aCol == AtsXColumn.Validation_Required_Col) return wva.getWorldViewValidationRequiredStr();
      if (aCol == AtsXColumn.Version_Target_Col) return wva.getWorldViewVersion();
      if (aCol == AtsXColumn.Completed_Date_Col) return wva.getWorldViewCompletedDateStr();
      if (aCol == AtsXColumn.Cancelled_Date_Col) return wva.getWorldViewCancelledDateStr();
      if (aCol == AtsXColumn.Team_Col) return wva.getWorldViewTeam();
      if (aCol == AtsXColumn.Related_To_State_Col) return wva.getWorldViewRelatedToState();
      if (aCol == AtsXColumn.Originator_Col) return wva.getWorldViewOriginator();
      if (aCol == AtsXColumn.Branch_Status_Col) return wva.getWorldViewBranchStatus();
      if (aCol == AtsXColumn.Implementor_Col) return wva.getWorldViewImplementer();
      if (aCol == AtsXColumn.Change_Type_Col) return wva.getWorldViewChangeTypeStr();
      if (aCol == AtsXColumn.State_Col) return wva.getWorldViewState();
      if (aCol == AtsXColumn.Assignees_Col) return wva.getWorldViewActivePoc();
      if (aCol == AtsXColumn.Created_Date_Col) return wva.getWorldViewCreatedDateStr();
      if (aCol == AtsXColumn.ID_Col) return wva.getWorldViewID();
      if (aCol == AtsXColumn.Priority_Col) return wva.getWorldViewPriority();
      if (aCol == AtsXColumn.Resolution_Col) return wva.getWorldViewResolution();
      if (aCol == AtsXColumn.Decision_Col) return wva.getWorldViewDecision();
      if (aCol == AtsXColumn.Legacy_PCR_Col) return wva.getWorldViewLegacyPCR();
      if (aCol == AtsXColumn.Weekly_Benefit_Hrs_Col) return AtsLib.doubleToStrString(wva.getWorldViewWeeklyBenefit(),
            true);
      if (aCol == AtsXColumn.Estimated_Hours_Col) return AtsLib.doubleToStrString(wva.getWorldViewEstimatedHours());
      if (aCol == AtsXColumn.Annual_Cost_Avoidance_Col) {
         Result result = wva.isWorldViewAnnualCostAvoidanceValid();
         if (result.isFalse()) return result.getText();
         return AtsLib.doubleToStrString(wva.getWorldViewAnnualCostAvoidance(), true);
      }
      if (aCol == AtsXColumn.Remaining_Hours_Col) {
         Result result = wva.isWorldViewRemainHoursValid();
         if (result.isFalse()) return result.getText();
         return AtsLib.doubleToStrString(wva.getWorldViewRemainHours());
      }
      if (aCol == AtsXColumn.Man_Days_Needed_Col) {
         Result result = wva.isWorldViewManDaysNeededValid();
         if (result.isFalse()) return result.getText();
         return AtsLib.doubleToStrString(wva.getWorldViewManDaysNeeded());
      }
      if (aCol == AtsXColumn.State_Percent_Col) return AtsLib.doubleToStrString(wva.getWorldViewStatePercentComplete());
      if (aCol == AtsXColumn.State_Hours_Spent_Col) return AtsLib.doubleToStrString(wva.getWorldViewStateHoursSpent());
      if (aCol == AtsXColumn.Notes_Col) return wva.getWorldViewNotes();
      if (aCol == AtsXColumn.Percent_Rework_Col) return wva.getWorldViewPercentReworkStr();
      if (aCol == AtsXColumn.Estimated_Release_Date_Col) return wva.getWorldViewEstimatedReleaseDateStr();
      if (aCol == AtsXColumn.Release_Date_Col) return wva.getWorldViewReleaseDateStr();
      if (aCol == AtsXColumn.Deadline_Col) return wva.getWorldViewDeadlineDateStr();
      if (aCol == AtsXColumn.Work_Package_Col) return wva.getWorldViewWorkPackage();
      if (aCol == AtsXColumn.Category_Col) return wva.getWorldViewCategory();
      if (aCol == AtsXColumn.Category2_Col) return wva.getWorldViewCategory2();
      if (aCol == AtsXColumn.Category3_Col) return wva.getWorldViewCategory3();
      if (aCol == AtsXColumn.Total_Percent_Complete_Col) return wva.getWorldViewTotalPercentComplete() + "";
      if (aCol == AtsXColumn.Total_Hours_Spent_Col) return AtsLib.doubleToStrString(wva.getWorldViewTotalHoursSpent());

      return "Unhandled Column";
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

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      Artifact artifact = ((WorldArtifactItem) element).getArtifact();
      if (artifact == null || artifact.isDeleted()) return null;
      XViewerColumn xCol = treeViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         AtsXColumn aCol = AtsXColumn.getAtsXColumn(xCol);
         return getColumnImage(element, columnIndex, artifact, xCol, aCol);
      }
      return null;
   }

   /**
    * Provided as optimization of subclassed classes so provider doesn't have to retrieve the same information that has
    * already been retrieved
    * 
    * @param element
    * @param columnIndex
    * @param artifact
    * @param xCol
    * @param aCol
    * @return image to display
    */
   public Image getColumnImage(Object element, int columnIndex, Artifact artifact, XViewerColumn xCol, AtsXColumn aCol) {
      if (element instanceof String) return null;
      IWorldViewArtifact wva = (IWorldViewArtifact) artifact;
      if (!xCol.isShow()) return null; // Since not shown, don't display
      if (aCol == AtsXColumn.Type_Col)
         return artifact.getImage();
      else if (aCol == AtsXColumn.Change_Type_Col)
         return wva.getWorldViewChangeType().getImage();
      else if (aCol == AtsXColumn.Assignees_Col)
         return wva.getAssigneeImage();
      else if (aCol == AtsXColumn.Deadline_Col) {
         if (wva.isWorldViewDeadlineAlerting().isTrue()) return AtsPlugin.getInstance().getImage("warn.gif");
      }
      return null;
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

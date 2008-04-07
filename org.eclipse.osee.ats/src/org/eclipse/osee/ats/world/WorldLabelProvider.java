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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

public class WorldLabelProvider implements ITableLabelProvider, ITableColorProvider {
   Font font = null;

   private final WorldXViewer treeViewer;
   private Map<AtsXColumn, Benchmark> bm = new HashMap<AtsXColumn, Benchmark>();

   public WorldLabelProvider(WorldXViewer treeViewer) {
      super();
      PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {

         @Override
         public void postShutdown(IWorkbench workbench) {
         }

         @Override
         public boolean preShutdown(IWorkbench workbench, boolean forced) {
            File file = new File("BenchmarkStats.txt");
            System.out.println("writing : " + file.getAbsolutePath());
            try {
               FileWriter fw = new FileWriter(file);
               for (Benchmark b : bm.values()) {
                  if (b.getTotalSamples() > 0) {
                     fw.append(b.toString() + "\n");
                  }
               }
               fw.close();
            } catch (IOException ex) {
               ex.printStackTrace();
            }

            return true;
         }

      });
      this.treeViewer = treeViewer;
      for (AtsXColumn col : AtsXColumn.values()) {
         bm.put(col, new Benchmark(col.getName()));
      }
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
      if (true) return "test";
      IWorldViewArtifact wva = (IWorldViewArtifact) artifact;
      String value = "";
      if (aCol == AtsXColumn.Type_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewType();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Actionable_Items_Col) {
         bm.get(aCol).startSample();
         value = "test";//wva.getWorldViewActionableItems();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.User_Community_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewUserCommunity();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Title_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewTitle();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Number_of_Tasks_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewNumberOfTasks();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Description_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewDescription();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Metrics_from_Tasks_Col) {
         try {
            bm.get(aCol).startSample();
            value = wva.isMetricsFromTasks() ? "yes" : "";
            bm.get(aCol).endSample();
            return value;
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            bm.get(aCol).startSample();
            value = ex.getLocalizedMessage();
            bm.get(aCol).endSample();
            return value;
         }
      }
      if (aCol == AtsXColumn.Validation_Required_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewValidationRequiredStr();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Version_Target_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewVersion();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Completed_Date_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewCompletedDateStr();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Cancelled_Date_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewCancelledDateStr();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Team_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewTeam();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Related_To_State_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewRelatedToState();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Originator_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewOriginator();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Branch_Status_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewBranchStatus();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Implementor_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewImplementer();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Review_Author_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewReviewAuthor();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Review_Moderator_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewReviewModerator();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Review_Reviewer_Col) {
         {
            bm.get(aCol).startSample();
            value = wva.getWorldViewReviewReviewer();
            bm.get(aCol).endSample();
            return value;
         }
      }
      if (aCol == AtsXColumn.Review_Decider_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewReviewDecider();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Change_Type_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewChangeTypeStr();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.State_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewState();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Assignees_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewActivePoc();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Created_Date_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewCreatedDateStr();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.ID_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewID();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Priority_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewPriority();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Resolution_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewResolution();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Decision_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewDecision();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Legacy_PCR_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewLegacyPCR();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Weekly_Benefit_Hrs_Col) {
         bm.get(aCol).startSample();
         value = AtsLib.doubleToStrString(wva.getWorldViewWeeklyBenefit(), true);
         bm.get(aCol).endSample();
         return value;
      }

      if (aCol == AtsXColumn.Estimated_Hours_Col) {
         bm.get(aCol).startSample();
         value = AtsLib.doubleToStrString(wva.getWorldViewEstimatedHours());
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Annual_Cost_Avoidance_Col) {
         Result result = wva.isWorldViewAnnualCostAvoidanceValid();
         if (result.isFalse()) bm.get(aCol).startSample();
         value = result.getText();
         bm.get(aCol).startSample();
         value = AtsLib.doubleToStrString(wva.getWorldViewAnnualCostAvoidance(), true);
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Remaining_Hours_Col) {
         Result result = wva.isWorldViewRemainHoursValid();
         if (result.isFalse()) bm.get(aCol).startSample();
         value = result.getText();
         bm.get(aCol).startSample();
         value = AtsLib.doubleToStrString(wva.getWorldViewRemainHours());
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Man_Days_Needed_Col) {
         Result result = wva.isWorldViewManDaysNeededValid();
         if (result.isFalse()) bm.get(aCol).startSample();
         value = result.getText();
         bm.get(aCol).startSample();
         value = AtsLib.doubleToStrString(wva.getWorldViewManDaysNeeded());
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.State_Percent_Col) {
         bm.get(aCol).startSample();
         value = AtsLib.doubleToStrString(wva.getWorldViewStatePercentComplete());
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.State_Hours_Spent_Col) {
         bm.get(aCol).startSample();
         value = AtsLib.doubleToStrString(wva.getWorldViewStateHoursSpent());
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Notes_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewNotes();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Percent_Rework_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewPercentReworkStr();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Estimated_Release_Date_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewEstimatedReleaseDateStr();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Release_Date_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewReleaseDateStr();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Deadline_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewDeadlineDateStr();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Work_Package_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewWorkPackage();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Category_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewCategory();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Category2_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewCategory2();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Category3_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewCategory3();
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Total_Percent_Complete_Col) {
         bm.get(aCol).startSample();
         value = wva.getWorldViewTotalPercentComplete() + "";
         bm.get(aCol).endSample();
         return value;
      }
      if (aCol == AtsXColumn.Total_Hours_Spent_Col) {
         bm.get(aCol).startSample();
         value = AtsLib.doubleToStrString(wva.getWorldViewTotalHoursSpent());
         bm.get(aCol).endSample();
         return value;
      }

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

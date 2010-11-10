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

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class WorldLabelProvider extends XViewerLabelProvider {

   private final WorldXViewer worldXViewer;
   protected Font font;
   private GoalArtifact parentGoalArtifact;

   public WorldLabelProvider(WorldXViewer worldXViewer) {
      super(worldXViewer);
      this.worldXViewer = worldXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            if (item.isXColumnProvider(xCol)) {
               Image image = item.getColumnImage(element, xCol, columnIndex);
               if (image != null) {
                  return image;
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            if (item.isXColumnProvider(xCol)) {
               Color color = item.getForeground(element, xCol, columnIndex);
               if (color != null) {
                  return color;
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         // NOTE: HRID, Type, Title are handled by XViewerValueColumn values
         if (!(element instanceof IWorldViewArtifact)) {
            return "";
         }
         IWorldViewArtifact wva = (IWorldViewArtifact) element;
         if (xCol.equals(WorldXViewerFactory.Percent_Complete_State_Col)) {
            return String.valueOf(wva.getWorldViewPercentCompleteState());
         }
         if (xCol.equals(WorldXViewerFactory.Percent_Complete_State_Task_Col)) {
            return String.valueOf(wva.getWorldViewPercentCompleteStateTask());
         }
         if (xCol.equals(WorldXViewerFactory.Percent_Complete_State_Review_Col)) {
            return String.valueOf(wva.getWorldViewPercentCompleteStateReview());
         }
         if (xCol.equals(WorldXViewerFactory.Percent_Complete_Total_Col)) {
            return String.valueOf(wva.getWorldViewPercentCompleteTotal());
         }
         if (xCol.equals(WorldXViewerFactory.Hours_Spent_State_Col)) {
            return AtsUtil.doubleToI18nString(wva.getWorldViewHoursSpentState());
         }
         if (xCol.equals(WorldXViewerFactory.Hours_Spent_State_Task_Col)) {
            return AtsUtil.doubleToI18nString(wva.getWorldViewHoursSpentStateTask());
         }
         if (xCol.equals(WorldXViewerFactory.Hours_Spent_State_Review_Col)) {
            return AtsUtil.doubleToI18nString(wva.getWorldViewHoursSpentStateReview());
         }
         if (xCol.equals(WorldXViewerFactory.Hours_Spent_Total_Col)) {
            return AtsUtil.doubleToI18nString(wva.getWorldViewHoursSpentStateTotal());
         }
         if (xCol.equals(WorldXViewerFactory.Total_Hours_Spent_Col)) {
            return AtsUtil.doubleToI18nString(wva.getWorldViewHoursSpentTotal());
         }
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            if (item.isXColumnProvider(xCol)) {
               String text = item.getColumnText(element, xCol, columnIndex);
               if (text != null) {
                  return text;
               }
            }
         }

         return "Unhandled Column";
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   @Override
   public void dispose() {
      if (font != null) {
         font.dispose();
      }
      font = null;
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   public WorldXViewer getWorldXViewer() {
      return worldXViewer;
   }

   /**
    * Value will be set, and changed, as label provider refreshes its elements. This is so the goal members can tell
    * which parent they belong to.
    */
   public void setParentGoal(GoalArtifact parentGoalArtifact) {
      this.parentGoalArtifact = parentGoalArtifact;
   }

   public GoalArtifact getParentGoalArtifact() {
      return parentGoalArtifact;
   }
}

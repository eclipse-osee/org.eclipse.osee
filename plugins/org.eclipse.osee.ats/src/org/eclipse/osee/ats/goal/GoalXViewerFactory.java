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
package org.eclipse.osee.ats.goal;

import java.util.Arrays;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.column.AssigneeColumnUI;
import org.eclipse.osee.ats.column.AtsIdColumnUI;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.column.CreatedDateColumn;
import org.eclipse.osee.ats.column.GoalOrderColumn;
import org.eclipse.osee.ats.column.GoalOrderVoteColumn;
import org.eclipse.osee.ats.column.NotesColumn;
import org.eclipse.osee.ats.column.PriorityColumn;
import org.eclipse.osee.ats.column.StateColumnUI;
import org.eclipse.osee.ats.column.TargetedVersionColumn;
import org.eclipse.osee.ats.column.TypeColumn;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.ats.world.WorldXViewerSorter;
import org.eclipse.osee.ats.world.WorldXViewerUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class GoalXViewerFactory extends SkynetXViewerFactory {

   private GoalArtifact soleGoalArtifact;

   private List<? extends XViewerColumn> getGoalViewerVisibleColumns() {
      List<XViewerColumn> columns = Arrays.asList(GoalOrderColumn.getInstance(), GoalOrderVoteColumn.getInstance(),
         new XViewerAtsAttributeValueColumn(AtsColumnToken.TitleColumn), TypeColumn.getInstance(),
         StateColumnUI.getInstance(), PriorityColumn.getInstance(), ChangeTypeColumn.getInstance(),
         AssigneeColumnUI.getInstance(), new AtsIdColumnUI(true), CreatedDateColumn.getInstance(),
         TargetedVersionColumn.getInstance(), NotesColumn.getInstance());
      return columns;
   }
   private final Integer[] widths = new Integer[] {
      GoalOrderColumn.DEFAULT_WIDTH,
      GoalOrderVoteColumn.DEFAULT_WIDTH,
      250,
      60,
      60,
      20,
      20,
      100,
      50,
      50,
      50,
      80};

   public GoalXViewerFactory(GoalArtifact soleGoalArtifact) {
      super("org.eclipse.osee.ats.GoalXViewer");
      this.soleGoalArtifact = soleGoalArtifact;
      int widthIndex = 0;
      // Create new column from world columns but set show and width for task
      List<? extends XViewerColumn> goalViewerVisibleColumns = getGoalViewerVisibleColumns();
      for (XViewerColumn taskCol : goalViewerVisibleColumns) {
         XViewerColumn newCol = taskCol.copy();
         newCol.setShow(true);
         newCol.setWidth(widths[widthIndex++]);
         registerColumns(newCol);
      }
      // Add remaining columns from world columns
      for (XViewerColumn worldCol : WorldXViewerFactory.getWorldViewColumns()) {
         if (!goalViewerVisibleColumns.contains(worldCol)) {
            XViewerColumn newCol = worldCol.copy();
            newCol.setShow(false);
            registerColumns(newCol);
         }
      }
      WorldXViewerUtil.registerAtsAttributeColumns(this);
      WorldXViewerUtil.registerPluginColumns(this);
      WorldXViewerUtil.registerStateColumns(this);
      WorldXViewerUtil.registerConfigurationsColumns(this);
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(GoalOrderColumn.COLUMN_ID)) {
            xCol.setSortForward(true);
         }
      }
      customizeData.getSortingData().setSortingNames(GoalOrderColumn.COLUMN_ID);
      return customizeData;
   }

   public GoalArtifact getSoleGoalArtifact() {
      return soleGoalArtifact;
   }

   public void setSoleGoalArtifact(GoalArtifact soleGoalArtifact) {
      this.soleGoalArtifact = soleGoalArtifact;
   }

}

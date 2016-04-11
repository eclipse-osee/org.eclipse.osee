/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.agile;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.column.AbstractMembersOrderColumn;
import org.eclipse.osee.ats.column.AssigneeColumnUI;
import org.eclipse.osee.ats.column.AtsColumnIdUI;
import org.eclipse.osee.ats.column.BacklogOrderColumn;
import org.eclipse.osee.ats.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.column.CreatedDateColumn;
import org.eclipse.osee.ats.column.NotesColumn;
import org.eclipse.osee.ats.column.PointsColumn;
import org.eclipse.osee.ats.column.PriorityColumnUI;
import org.eclipse.osee.ats.column.TargetedVersionColumn;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.ats.world.WorldXViewerSorter;
import org.eclipse.osee.ats.world.WorldXViewerUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class BacklogXViewerFactory extends SkynetXViewerFactory {

   private GoalArtifact soleBacklogArtifact;

   public BacklogXViewerFactory(GoalArtifact soleBacklogArtifact) {
      super("org.eclipse.osee.ats.BacklogXViewer");
      this.soleBacklogArtifact = soleBacklogArtifact;

      List<XViewerAtsAttributeValueColumn> configCols = WorldXViewerUtil.getConfigurationColumns();
      List<XViewerColumn> backlogCols = new LinkedList<>();

      // Add default Backlog columns
      WorldXViewerUtil.addColumn(this, BacklogOrderColumn.getInstance(), AbstractMembersOrderColumn.DEFAULT_WIDTH,
         backlogCols);
      WorldXViewerUtil.addColumn(this, PointsColumn.getInstance(), 20, backlogCols);
      WorldXViewerUtil.addColumn(this, new XViewerAtsAttributeValueColumn(AtsColumnToken.TitleColumn), 300,
         backlogCols);
      WorldXViewerUtil.addColumn(this,
         new AtsColumnIdUI(AtsColumnToken.StateColumn, AtsClientService.get().getServices()), 74, backlogCols);
      WorldXViewerUtil.addColumn(this, PriorityColumnUI.getInstance(), 20, backlogCols);
      WorldXViewerUtil.addColumn(this, ChangeTypeColumnUI.getInstance(), 20, backlogCols);
      WorldXViewerUtil.addColumn(this, AssigneeColumnUI.getInstance(), 113, backlogCols);
      WorldXViewerUtil.addColumn(this, TargetedVersionColumn.getInstance(), 50, backlogCols);
      WorldXViewerUtil.addColumn(this, SprintColumn.getInstance(), 100, backlogCols);
      XViewerColumn unPlannedWorkColumn = WorldXViewerUtil.getConfigColumn("ats.Unplanned Work", configCols);
      if (unPlannedWorkColumn != null) {
         WorldXViewerUtil.addColumn(this, unPlannedWorkColumn, 20, backlogCols);
         configCols.remove(unPlannedWorkColumn);
      }
      WorldXViewerUtil.addColumn(this, AgileFeatureGroupColumn.getInstance(), 91, backlogCols);
      WorldXViewerUtil.addColumn(this, NotesColumn.getInstance(), 116, backlogCols);
      WorldXViewerUtil.addColumn(this, CreatedDateColumn.getInstance(), 82, backlogCols);
      WorldXViewerUtil.addColumn(this,
         new AtsColumnIdUI(AtsColumnToken.AtsIdColumnShow, AtsClientService.get().getServices()), 50, backlogCols);

      // Add remaining columns from world columns
      for (XViewerColumn worldCol : WorldXViewerFactory.getWorldViewColumns()) {
         if (!backlogCols.contains(worldCol)) {
            XViewerColumn newCol = worldCol.copy();
            newCol.setShow(false);
            registerColumns(newCol);
         }
      }
      WorldXViewerUtil.registerAtsAttributeColumns(this);
      WorldXViewerUtil.registerPluginColumns(this);
      WorldXViewerUtil.registerStateColumns(this);
      // Add remaining Configuration Columns
      for (XViewerAtsAttributeValueColumn col : configCols) {
         registerColumns(col);
      }
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(BacklogOrderColumn.COLUMN_ID)) {
            xCol.setSortForward(true);
         }
      }
      customizeData.getSortingData().setSortingNames(BacklogOrderColumn.COLUMN_ID);
      return customizeData;
   }

   public GoalArtifact getSoleBacklogArtifact() {
      return soleBacklogArtifact;
   }

   public void setSoleBacklogArtifact(GoalArtifact soleBacklogArtifact) {
      this.soleBacklogArtifact = soleBacklogArtifact;
   }

}

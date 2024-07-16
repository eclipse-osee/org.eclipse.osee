/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.agile;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.ide.column.AbstractMembersOrderColumn;
import org.eclipse.osee.ats.ide.column.AgileTeamPointsColumnUI;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.column.BacklogOrderColumn;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.ide.column.CreatedDateColumnUI;
import org.eclipse.osee.ats.ide.column.PriorityColumnUI;
import org.eclipse.osee.ats.ide.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.ats.ide.world.WorldXViewerSorter;
import org.eclipse.osee.ats.ide.world.WorldXViewerUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class BacklogXViewerFactory extends SkynetXViewerFactory {

   public final static String NAMESPACE = "BacklogXViewer";

   public BacklogXViewerFactory(GoalArtifact soleBacklogArtifact, IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);

      List<XViewerAtsAttributeValueColumn> configCols = WorldXViewerUtil.getConfigurationColumns();
      List<XViewerColumn> backlogCols = new LinkedList<>();

      // Add default Backlog columns
      WorldXViewerUtil.addColumn(this, BacklogOrderColumn.getInstance(), AbstractMembersOrderColumn.DEFAULT_WIDTH,
         backlogCols);
      WorldXViewerUtil.addColumn(this, new XViewerAtsAttributeValueColumn(AtsColumnTokens.TitleColumn), 300,
         backlogCols);
      WorldXViewerUtil.addColumn(this, AgileTeamPointsColumnUI.getInstance(),
         AtsColumnTokens.AgileTeamPointsColumn.getWidth(), backlogCols);
      WorldXViewerUtil.addColumn(this, WorldXViewerFactory.getColumnServiceColumn(AtsColumnTokens.StateColumn), 116,
         backlogCols);
      WorldXViewerUtil.addColumn(this, PriorityColumnUI.getInstance(), 20, backlogCols);
      WorldXViewerUtil.addColumn(this, ChangeTypeColumnUI.getInstance(), 20, backlogCols);
      WorldXViewerUtil.addColumn(this, AssigneeColumnUI.getInstance(), 113, backlogCols);
      WorldXViewerUtil.addColumn(this, TargetedVersionColumnUI.getInstance(), 50, backlogCols);
      WorldXViewerUtil.addColumn(this, SprintColumn.getInstance(), 100, backlogCols);
      XViewerColumn unPlannedWorkColumn = WorldXViewerUtil.getConfigColumn("ats.Unplanned Work", configCols);
      if (unPlannedWorkColumn != null) {
         WorldXViewerUtil.addColumn(this, unPlannedWorkColumn, 20, backlogCols);
         configCols.remove(unPlannedWorkColumn);
      }
      WorldXViewerUtil.addColumn(this, AgileFeatureGroupColumn.getInstance(), 91, backlogCols);
      WorldXViewerUtil.addColumn(this, CreatedDateColumnUI.getInstance(), 82, backlogCols);
      WorldXViewerUtil.addColumn(this, WorldXViewerFactory.getColumnServiceColumn(AtsColumnTokens.AtsIdColumnShow), 50,
         backlogCols);

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

}

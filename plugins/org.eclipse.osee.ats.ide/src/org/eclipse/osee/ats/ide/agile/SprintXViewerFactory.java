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
package org.eclipse.osee.ats.ide.agile;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.ide.column.AgileTeamPointsColumnUI;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.ide.column.CreatedDateColumnUI;
import org.eclipse.osee.ats.ide.column.PriorityColumnUI;
import org.eclipse.osee.ats.ide.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.ats.ide.world.WorldXViewerSorter;
import org.eclipse.osee.ats.ide.world.WorldXViewerUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class SprintXViewerFactory extends SkynetXViewerFactory {

   private final SprintArtifact soleSprintArtifact;
   private static String NAMESPACE = "SprintXViewer";

   public SprintXViewerFactory(SprintArtifact soleSprintArtifact, IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      this.soleSprintArtifact = soleSprintArtifact;

      List<XViewerAtsAttributeValueColumn> configCols = WorldXViewerUtil.getConfigurationColumns();
      List<XViewerColumn> sprintCols = new LinkedList<>();

      // Add default Sprint columns
      WorldXViewerUtil.addColumn(this, SprintOrderColumn.getInstance(), 45, sprintCols);
      WorldXViewerUtil.addColumn(this, new XViewerAtsAttributeValueColumn(AtsColumnToken.TitleColumn), 339, sprintCols);
      WorldXViewerUtil.addColumn(this, AgileTeamPointsColumnUI.getInstance(),
         AtsColumnToken.AgileTeamPointsColumn.getWidth(), sprintCols);
      WorldXViewerUtil.addColumn(this, WorldXViewerFactory.getColumnServiceColumn(AtsColumnToken.StateColumn), 74,
         sprintCols);
      WorldXViewerUtil.addColumn(this, PriorityColumnUI.getInstance(), 20, sprintCols);
      WorldXViewerUtil.addColumn(this, ChangeTypeColumnUI.getInstance(), 20, sprintCols);
      WorldXViewerUtil.addColumn(this, AssigneeColumnUI.getInstance(), 113, sprintCols);
      XViewerColumn unPlannedWorkColumn = WorldXViewerUtil.getConfigColumn("ats.Unplanned Work", configCols);
      if (unPlannedWorkColumn != null) {
         WorldXViewerUtil.addColumn(this, unPlannedWorkColumn, 40, sprintCols);
      } else {
         WorldXViewerUtil.addColumn(this, new XViewerAtsAttributeValueColumn(AtsColumnToken.UnPlannedWorkColumn), 40,
            sprintCols);
      }
      WorldXViewerUtil.addColumn(this, TargetedVersionColumnUI.getInstance(), 50, sprintCols);
      WorldXViewerUtil.addColumn(this, AgileFeatureGroupColumn.getInstance(), 91, sprintCols);
      WorldXViewerUtil.addColumn(this, CreatedDateColumnUI.getInstance(), 82, sprintCols);
      WorldXViewerUtil.addColumn(this, WorldXViewerFactory.getColumnServiceColumn(AtsColumnToken.AtsIdColumnShow), 50,
         sprintCols);

      // Add remaining columns from world columns
      for (XViewerColumn worldCol : WorldXViewerFactory.getWorldViewColumns()) {
         if (!sprintCols.contains(worldCol)) {
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
         if (xCol.getId().equals(SprintOrderColumn.getInstance().getId())) {
            xCol.setSortForward(true);
         }
      }
      customizeData.getSortingData().setSortingNames(SprintOrderColumn.getInstance().getId());
      return customizeData;
   }

   public SprintArtifact getSoleSprintArtifact() {
      return soleSprintArtifact;
   }

}

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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.column.AssigneeColumnUI;
import org.eclipse.osee.ats.column.AtsIdColumnUI;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.column.CreatedDateColumn;
import org.eclipse.osee.ats.column.NotesColumn;
import org.eclipse.osee.ats.column.PriorityColumn;
import org.eclipse.osee.ats.column.StateColumnUI;
import org.eclipse.osee.ats.column.TargetedVersionColumn;
import org.eclipse.osee.ats.core.client.artifact.SprintArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.ats.world.WorldXViewerSorter;
import org.eclipse.osee.ats.world.WorldXViewerUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class SprintXViewerFactory extends SkynetXViewerFactory {

   private SprintArtifact soleSprintArtifact;

   public SprintXViewerFactory(SprintArtifact soleSprintArtifact) {
      super("org.eclipse.osee.ats.SprintXViewer");
      this.soleSprintArtifact = soleSprintArtifact;

      List<XViewerAtsAttributeValueColumn> configCols = WorldXViewerUtil.getConfigurationColumns();
      List<XViewerColumn> sprintCols = new LinkedList<>();

      // Add default Sprint columns
      WorldXViewerUtil.addColumn(this, SprintOrderColumn.getInstance(), 45, sprintCols);
      WorldXViewerUtil.addColumn(this, new XViewerAtsAttributeValueColumn(AtsColumnToken.TitleColumn), 339, sprintCols);
      WorldXViewerUtil.addColumn(this, StateColumnUI.getInstance(), 74, sprintCols);
      WorldXViewerUtil.addColumn(this, PriorityColumn.getInstance(), 20, sprintCols);
      WorldXViewerUtil.addColumn(this, ChangeTypeColumn.getInstance(), 20, sprintCols);
      WorldXViewerUtil.addColumn(this, AssigneeColumnUI.getInstance(), 113, sprintCols);
      XViewerColumn unPlannedWorkColumn = WorldXViewerUtil.getConfigColumn("ats.Unplanned Work", configCols);
      if (unPlannedWorkColumn != null) {
         WorldXViewerUtil.addColumn(this, unPlannedWorkColumn, 43, sprintCols);
         configCols.remove(unPlannedWorkColumn);
      }
      WorldXViewerUtil.addColumn(this, TargetedVersionColumn.getInstance(), 50, sprintCols);
      WorldXViewerUtil.addColumn(this, NotesColumn.getInstance(), 116, sprintCols);
      WorldXViewerUtil.addColumn(this, AgileFeatureGroupColumn.getInstance(), 91, sprintCols);
      WorldXViewerUtil.addColumn(this, CreatedDateColumn.getInstance(), 82, sprintCols);
      WorldXViewerUtil.addColumn(this, new AtsIdColumnUI(true), 50, sprintCols);

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

   public void setSoleSprintArtifact(SprintArtifact soleSprintArtifact) {
      this.soleSprintArtifact = soleSprintArtifact;
   }

}

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

import java.util.Arrays;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.column.AssigneeColumnUI;
import org.eclipse.osee.ats.column.AtsIdColumn;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.column.CreatedDateColumn;
import org.eclipse.osee.ats.column.GoalOrderColumn;
import org.eclipse.osee.ats.column.NotesColumn;
import org.eclipse.osee.ats.column.PriorityColumn;
import org.eclipse.osee.ats.column.StateColumn;
import org.eclipse.osee.ats.column.TargetedVersionColumn;
import org.eclipse.osee.ats.column.TitleColumn;
import org.eclipse.osee.ats.column.TypeColumn;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.ats.world.WorldXViewerSorter;
import org.eclipse.osee.ats.world.WorldXViewerUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class BacklogXViewerFactory extends SkynetXViewerFactory {

   private GoalArtifact soleBacklogArtifact;
   @SuppressWarnings("unchecked")
   public static final List<? extends XViewerColumn> BacklogViewerVisibleColumns = Arrays.asList(
      GoalOrderColumn.getBacklogInstance(), TitleColumn.getInstance(), TypeColumn.getInstance(),
      StateColumn.getInstance(), PriorityColumn.getInstance(), ChangeTypeColumn.getInstance(),
      AssigneeColumnUI.getInstance(), new AtsIdColumn(true), CreatedDateColumn.getInstance(),
      TargetedVersionColumn.getInstance(), NotesColumn.getInstance());
   public static Integer[] widths = new Integer[] {
      GoalOrderColumn.getBacklogInstance().getWidth(),
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

   public BacklogXViewerFactory(GoalArtifact soleBacklogArtifact) {
      super("org.eclipse.osee.ats.BacklogXViewer");
      this.soleBacklogArtifact = soleBacklogArtifact;
      int widthIndex = 0;
      // Create new column from world columns but set show and width for task
      for (XViewerColumn taskCol : BacklogViewerVisibleColumns) {
         XViewerColumn newCol = taskCol.copy();
         newCol.setShow(true);
         newCol.setWidth(widths[widthIndex++]);
         registerColumns(newCol);
      }
      // Add remaining columns from world columns
      for (XViewerColumn worldCol : WorldXViewerFactory.WorldViewColumns) {
         if (!BacklogViewerVisibleColumns.contains(worldCol)) {
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
         if (xCol.getId().equals(GoalOrderColumn.getBacklogInstance().getId())) {
            xCol.setSortForward(true);
         }
      }
      customizeData.getSortingData().setSortingNames(GoalOrderColumn.getBacklogInstance().getId());
      return customizeData;
   }

   public GoalArtifact getSoleBacklogArtifact() {
      return soleBacklogArtifact;
   }

   public void setSoleBacklogArtifact(GoalArtifact soleBacklogArtifact) {
      this.soleBacklogArtifact = soleBacklogArtifact;
   }

}

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
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.ats.world.WorldXViewerSorter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerHridColumn;

/**
 * @author Donald G. Dunne
 */
public class GoalXViewerFactory extends SkynetXViewerFactory {

   private GoalArtifact soleGoalArtifact;
   public static final List<XViewerColumn> GoalViewerVisibleColumns = Arrays.asList(WorldXViewerFactory.Goal_Order,
      WorldXViewerFactory.Goal_Order_Vote_Col, WorldXViewerFactory.Title_Col, WorldXViewerFactory.Type_Col,
      WorldXViewerFactory.State_Col, WorldXViewerFactory.Priority_Col, WorldXViewerFactory.Change_Type_Col,
      WorldXViewerFactory.Assignees_Col, new XViewerHridColumn(true), WorldXViewerFactory.Created_Date_Col,
      WorldXViewerFactory.Version_Target_Col, WorldXViewerFactory.Notes_Col);
   public static Integer[] widths = new Integer[] {
      WorldXViewerFactory.Goal_Order.getWidth(),
      WorldXViewerFactory.Goal_Order_Vote_Col.getWidth(),
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
      for (XViewerColumn taskCol : GoalViewerVisibleColumns) {
         XViewerColumn newCol = taskCol.copy();
         newCol.setShow(true);
         newCol.setWidth(widths[widthIndex++]);
         registerColumns(newCol);
      }
      // Add remaining columns from world columns
      for (XViewerColumn worldCol : WorldXViewerFactory.WorldViewColumns) {
         if (!GoalViewerVisibleColumns.contains(worldCol)) {
            XViewerColumn newCol = worldCol.copy();
            newCol.setShow(false);
            registerColumns(newCol);
         }
      }
      // Register any columns from other plugins
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            for (XViewerColumn xCol : item.getXViewerColumns()) {
               registerColumns(xCol);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      registerAllAttributeColumns();
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(WorldXViewerFactory.Goal_Order.getId())) {
            xCol.setSortForward(true);
         }
      }
      customizeData.getSortingData().setSortingNames(WorldXViewerFactory.Goal_Order.getId());
      return customizeData;
   }

   public GoalArtifact getSoleGoalArtifact() {
      return soleGoalArtifact;
   }

   public void setSoleGoalArtifact(GoalArtifact soleGoalArtifact) {
      this.soleGoalArtifact = soleGoalArtifact;
   }

}

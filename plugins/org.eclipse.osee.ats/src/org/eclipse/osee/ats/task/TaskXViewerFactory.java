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
package org.eclipse.osee.ats.task;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.ats.field.AssigneeColumn;
import org.eclipse.osee.ats.field.EstimatedHoursColumn;
import org.eclipse.osee.ats.field.NotesColumn;
import org.eclipse.osee.ats.field.RelatedToStateColumn;
import org.eclipse.osee.ats.field.ResolutionColumn;
import org.eclipse.osee.ats.field.StateColumn;
import org.eclipse.osee.ats.field.TitleColumn;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.ats.world.WorldXViewerSorter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewerFactory extends SkynetXViewerFactory {

   public static final List<XViewerColumn> TaskViewerVisibleColumns = Arrays.asList(TitleColumn.getInstance(),
      StateColumn.getInstance(), AssigneeColumn.getInstance(), WorldXViewerFactory.Percent_Complete_Total_Col,
      WorldXViewerFactory.Total_Hours_Spent_Col, ResolutionColumn.getInstance(), EstimatedHoursColumn.getInstance(),
      WorldXViewerFactory.Remaining_Hours_Col, RelatedToStateColumn.getInstance(), NotesColumn.getInstance());
   public static Integer[] widths = new Integer[] {450, 60, 150, 40, 40, 100, 50, 50, 50, 80, 80};

   public TaskXViewerFactory() {
      super("org.eclipse.osee.ats.TaskXViewer");
      int widthIndex = 0;
      // Create new column from world columns but set show and width for task
      for (XViewerColumn taskCol : TaskViewerVisibleColumns) {
         XViewerColumn newCol = taskCol.copy();
         newCol.setShow(true);
         newCol.setWidth(widths[widthIndex++]);
         registerColumns(newCol);
      }
      // Add remaining columns from world columns
      for (XViewerColumn worldCol : WorldXViewerFactory.WorldViewColumns) {
         if (!TaskViewerVisibleColumns.contains(worldCol)) {
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

}

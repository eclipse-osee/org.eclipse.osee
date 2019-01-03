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
package org.eclipse.osee.ats.ide.workflow.task;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.ide.column.AssigneeColumnUI;
import org.eclipse.osee.ats.ide.column.EstimatedHoursColumn;
import org.eclipse.osee.ats.ide.column.HoursSpentTotalColumn;
import org.eclipse.osee.ats.ide.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumn;
import org.eclipse.osee.ats.ide.column.RemainingHoursColumn;
import org.eclipse.osee.ats.ide.column.ResolutionColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.ide.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.ats.ide.world.WorldXViewerSorter;
import org.eclipse.osee.ats.ide.world.WorldXViewerUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewerFactory extends SkynetXViewerFactory {

   public static final List<XViewerColumn> TaskViewerVisibleColumns =
      Arrays.asList(new XViewerAtsAttributeValueColumn(AtsColumnToken.TitleColumn),
         WorldXViewerFactory.getColumnServiceColumn(AtsColumnToken.StateColumn), AssigneeColumnUI.getInstance(),
         PercentCompleteTotalColumn.getInstance(), HoursSpentTotalColumn.getInstance(), ResolutionColumn.getInstance(),
         EstimatedHoursColumn.getInstance(), RemainingHoursColumn.getInstance(), RelatedToStateColumn.getInstance(),
         WorldXViewerFactory.getColumnServiceColumn(AtsColumnToken.StateColumn));
   public static Integer[] widths = new Integer[] {450, 60, 150, 40, 40, 100, 50, 50, 50, 80, 80};

   public TaskXViewerFactory(IOseeTreeReportProvider reportProvider) {
      super("org.eclipse.osee.ats.ide.TaskXViewer", reportProvider);
      int widthIndex = 0;
      // Create new column from world columns but set show and width for task
      for (XViewerColumn taskCol : TaskViewerVisibleColumns) {
         XViewerColumn newCol = taskCol.copy();
         newCol.setShow(true);
         newCol.setWidth(widths[widthIndex++]);
         registerColumns(newCol);
      }
      // Add remaining columns from world columns
      for (XViewerColumn worldCol : WorldXViewerFactory.getWorldViewColumns()) {
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
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      WorldXViewerUtil.registerConfigurationsColumns(this);
      registerAllAttributeColumns();
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new WorldXViewerSorter(xViewer);
   }

}

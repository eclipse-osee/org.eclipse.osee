/*
 * Created on May 15, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.branch.gantt.views;

import org.eclipse.nebula.widgets.ganttchart.DefaultSettings;
import org.eclipse.nebula.widgets.ganttchart.ISettings;

public class BranchGanttSettings extends DefaultSettings {

   @Override
   public int getInitialZoomLevel() {
      return ISettings.ZOOM_YEAR_VERY_SMALL;
   }

   @Override
   public boolean allowInfiniteHorizontalScrollBar() {
      return false;
   }

   @Override
   public boolean lockHeaderOnVerticalScroll() {
      return true;
   }

}

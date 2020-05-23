/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.branch.gantt.views;

import org.eclipse.nebula.widgets.ganttchart.DefaultSettings;
import org.eclipse.nebula.widgets.ganttchart.ISettings;

/**
 * @author Donald G. Dunne
 */
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

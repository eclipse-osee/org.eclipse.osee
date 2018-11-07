/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

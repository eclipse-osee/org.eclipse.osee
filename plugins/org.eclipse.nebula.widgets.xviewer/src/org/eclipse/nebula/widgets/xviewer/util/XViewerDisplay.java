/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.xviewer.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class XViewerDisplay {

   private static Boolean standalone = false;
   private static Display display = null;

   public static Display getDisplay() {
      Display result = display;
      if (result == null && !isStandaloneXViewer()) {
         result = PlatformUI.getWorkbench().getDisplay();
      }
      return result;
   }

   public static Boolean isStandaloneXViewer() {
      return standalone;
   }

   /**
    * @return true if this xviewer is embedded in Eclipse workbench and it's running
    */
   public static boolean isWorkbenchRunning() {
      return PlatformUI.isWorkbenchRunning();
   }

   public static void setStandaloneXViewer(boolean workbenchRunningOverride, Display display) {
      XViewerDisplay.standalone = workbenchRunningOverride;
      XViewerDisplay.display = display;
   }

}

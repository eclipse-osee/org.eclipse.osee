/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.swt;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Robert A. Fisher
 */
public final class Displays {

   private Displays() {
      // Utility Class
   }

   public static void ensureInDisplayThread(Runnable runnable) {
      ensureInDisplayThread(runnable, false);
   }

   public static void pendInDisplayThread(Runnable runnable) {
      ensureInDisplayThread(runnable, true);
   }

   public static void ensureInDisplayThread(Runnable runnable, boolean forcePend) {
      if (isDisplayThread()) {
         // No need to check for force since this will always pend
         runnable.run();
      } else {
         if (forcePend) {
            Display.getDefault().syncExec(runnable);
         } else {
            Display.getDefault().asyncExec(runnable);
         }
      }
   }

   public static boolean isDisplayThread() {
      boolean isDisplayTh = false;
      Display currentDisplay = Display.getCurrent();
      if (currentDisplay != null) {
         isDisplayTh = currentDisplay.getThread() == Thread.currentThread();
      }
      return isDisplayTh;
   }

   public static Shell getActiveShell() {
      return Display.getDefault().getActiveShell();
   }

   public static Color getSystemColor(int colorId) {
      return Display.getDefault().getSystemColor(colorId);
   }

   /**
    * Creates a new Color resource. Remember to dispose the resource when done
    * 
    * @param red
    * @param green
    * @param blue
    * @return
    */

   public static Color getColor(int red, int green, int blue) {
      return new Color(Display.getDefault(), red, green, blue);
   }

   public static Image getSystemImage(int id) {
      return Display.getDefault().getSystemImage(id);
   }
}

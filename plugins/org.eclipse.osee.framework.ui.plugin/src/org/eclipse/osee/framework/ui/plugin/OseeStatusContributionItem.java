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

package org.eclipse.osee.framework.ui.plugin;

import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.StatusLineContributionItem;

/**
 * @author Jeff C. Phillips
 */
public abstract class OseeStatusContributionItem extends StatusLineContributionItem {

   private boolean isDisposed = false;

   protected OseeStatusContributionItem(String id) {
      this(id, 4);
   }

   protected OseeStatusContributionItem(String id, int width) {
      super(id, true, width);
   }

   protected abstract String getEnabledToolTip();

   protected abstract String getDisabledToolTip();

   protected abstract Image getEnabledImage();

   protected abstract Image getDisabledImage();

   @Override
   public void dispose() {
      this.isDisposed = true;
      super.dispose();
   }

   public boolean isDisposed() {
      return isDisposed;
   }

   public void updateStatus(final boolean isActive) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            Image image = isActive ? getEnabledImage() : getDisabledImage();
            String toolTip = isActive ? getEnabledToolTip() : getDisabledToolTip();

            if (image != null) {
               setImage(image);
            }
            if (toolTip != null) {
               setToolTipText(toolTip);
            }
         }
      });
   }
}

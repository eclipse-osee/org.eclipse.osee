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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.StatusLineContributionItem;

/**
 * @author Jeff C. Phillips
 */
public abstract class OseeContributionItem extends StatusLineContributionItem {

   protected OseeContributionItem(String id) {
      this(id, 4);
   }

   protected OseeContributionItem(String id, int width) {
      super(id, true, width);
   }

   protected abstract String getEnabledToolTip();

   protected abstract String getDisabledToolTip();

   protected abstract Image getEnabledImage();

   protected abstract Image getDisabledImage();

   protected void updateStatus(final boolean isActive) {
      Display.getDefault().asyncExec(new Runnable() {
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

   public static void addTo(IStatusLineManager manager) {
      if (OseeAts.isAtsAdmin()) {
         AdminContributionItem.addTo(manager);
      }
      SkynetServiceContributionItem.addTo(manager);
      OseeServicesStatusContributionItem.addTo(manager);
      SessionContributionItem.addTo(manager);
   }

   public static void addTo(IPageSite pageSite, boolean update) {
      addTo(pageSite.getActionBars().getStatusLineManager());

      if (update) {
         pageSite.getActionBars().updateActionBars();
      }
   }

   public static void addTo(ViewPart view, boolean update) {
      addTo(view.getViewSite().getActionBars().getStatusLineManager());

      if (update) {
         view.getViewSite().getActionBars().updateActionBars();
      }
   }

   public static void addTo(MultiPageEditorPart editorPart, boolean update) {
      addTo(editorPart.getEditorSite().getActionBars().getStatusLineManager());
      if (update) {
         editorPart.getEditorSite().getActionBars().updateActionBars();
      }
   }

}

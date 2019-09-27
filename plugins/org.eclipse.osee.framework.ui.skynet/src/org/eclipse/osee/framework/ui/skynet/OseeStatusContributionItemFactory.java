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

import java.util.logging.Level;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeStatusContributionItem;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jeff C. Phillips
 */
public final class OseeStatusContributionItemFactory {

   private OseeStatusContributionItemFactory() {
      // Static Factory Class
   }

   public static void addTo(IStatusLineManager manager) {
      createItem(manager, ResServiceContributionItem.class);
      createItem(manager, SessionContributionItem.class);
      createItem(manager, WorkspaceContributionItem.class);
      createItem(manager, OseeTargetContributionItem.class);
   }

   private static void createItem(IStatusLineManager manager, Class<? extends OseeStatusContributionItem> contribClazz) {
      boolean wasFound = false;
      for (IContributionItem item : manager.getItems()) {
         if (contribClazz.isAssignableFrom(item.getClass())) {
            wasFound = true;
            break;
         }
      }
      if (!wasFound) {
         try {
            OseeStatusContributionItem object = contribClazz.newInstance();
            manager.add(object);
         } catch (Exception ex) {
            OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error creating status line contribution item [%s]",
               contribClazz);
         }
      }
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

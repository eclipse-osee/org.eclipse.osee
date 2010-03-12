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
package org.eclipse.osee.framework.ui.service.control.menu;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Roberto E. Escobar
 */
public class MenuBuilder {

   private ViewPart view;
   private List<Action> actions;

   public MenuBuilder(ViewPart view) {
      this.view = view;
      this.actions = new ArrayList<Action>();
   }

   public void addAction(Action action) {
      actions.add(action);
   }

   public void createPopUpMenu(Viewer viewer) {
      MenuManager menuManager = new MenuManager("#PopupMenu");
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuBuilder.this.fillMenu(manager);
         }
      });

      registerPopup(menuManager, viewer);
   }

   private void registerPopup(MenuManager menuManager, Viewer viewer) {
      Control control = viewer.getControl();
      Menu menu = menuManager.createContextMenu(control);
      control.setMenu(menu);

      view.getSite().registerContextMenu(menuManager, viewer);
   }

   public void contributeToActionBars() {
      IActionBars bars = view.getViewSite().getActionBars();
      fillMenu(bars.getMenuManager());
      fillLocalToolBar(bars.getToolBarManager());
   }

   private void fillMenu(IMenuManager manager) {
      int count = 0;
      for (Action action : actions) {
         count++;
         manager.add(action);
         if (count == 2) {
            manager.add(new Separator());
         }
      }
      manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
   }

   private void fillLocalToolBar(IToolBarManager manager) {
      int count = 0;
      for (Action action : actions) {
         count++;
         manager.add(action);
         if (count == 2) {
            break;
         }
      }
      manager.add(new Separator());
   }
}

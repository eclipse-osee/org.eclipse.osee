/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.change.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class CompareDropDown extends Action {
   private final MenuManager manager;

   public CompareDropDown() {
      super("Compare Against...", IAction.AS_DROP_DOWN_MENU);
      setToolTipText("Select from the drop down to change the current change report settings.");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.CHANGE_LOG));
      manager = new MenuManager();
      setMenuCreator(new CompareMenuCreator());
   }

   public void add(IAction action) {
      manager.add(action);
   }

   private final class CompareMenuCreator implements IMenuCreator {

      @Override
      public void dispose() {
         manager.dispose();
      }

      @Override
      public Menu getMenu(Control parent) {
         if (manager.getMenu() == null) {
            manager.createContextMenu(parent);
         }
         return manager.getMenu();
      }

      @Override
      public Menu getMenu(Menu parent) {
         return null;
      }

   }
}

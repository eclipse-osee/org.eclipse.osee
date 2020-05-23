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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Robert A. Fisher
 */
public final class MenuItems {

   /**
    * Builds a MenuItem based on the supplied Action.
    * 
    * @param action The action to make a MenuItem of.
    * @throws IllegalArgumentException if the action is null.
    */
   public static final MenuItem createMenuItem(Menu parent, int style, final Action action) {
      if (action == null) {
         throw new IllegalArgumentException("action can not be null");
      }

      MenuItem menuItem = new MenuItem(parent, style);
      menuItem.setText(action.getText());
      menuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            action.run();
         }
      });

      ImageDescriptor descriptor = action.getImageDescriptor();
      if (descriptor != null) {
         menuItem.setImage(descriptor.createImage());
      }

      return menuItem;
   }
}

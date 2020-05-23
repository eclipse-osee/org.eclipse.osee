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

package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;

/**
 * A Handler that will fire an enabled changed event each time the menu is shown.
 * 
 * @author Robert A. Fisher
 */
public abstract class AbstractSelectionEnabledHandler extends CommandHandler implements MenuListener {
   private final HandlerEvent enabledChangedEvent = new HandlerEvent(this, true, false);

   public AbstractSelectionEnabledHandler(MenuManager menuManager) {
      menuManager.getMenu().addMenuListener(this);
   }

   protected AbstractSelectionEnabledHandler() {
      // for testing only
   }

   @Override
   public final void menuHidden(MenuEvent e) {
      //Do nothing
   }

   @Override
   public final void menuShown(MenuEvent e) {
      fireHandlerChanged(enabledChangedEvent);
   }
}

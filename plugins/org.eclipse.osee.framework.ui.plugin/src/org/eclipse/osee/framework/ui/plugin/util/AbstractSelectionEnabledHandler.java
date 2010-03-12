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

   public final void menuHidden(MenuEvent e) {
   }

   public final void menuShown(MenuEvent e) {
      fireHandlerChanged(enabledChangedEvent);
   }
}

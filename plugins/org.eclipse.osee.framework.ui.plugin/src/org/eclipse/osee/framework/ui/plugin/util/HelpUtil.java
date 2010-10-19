/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

public class HelpUtil {

   public static void setHelp(Control control, String name, String library) {
      PlatformUI.getWorkbench().getHelpSystem().setHelp(control, library + "." + name);
   }

   public static void setHelp(IAction action, String name, String library) {
      PlatformUI.getWorkbench().getHelpSystem().setHelp(action, library + "." + name);
   }

   public static void setHelp(Menu menu, String name, String library) {
      PlatformUI.getWorkbench().getHelpSystem().setHelp(menu, library + "." + name);
   }

   public static void setHelp(MenuItem menuItem, String name, String library) {
      PlatformUI.getWorkbench().getHelpSystem().setHelp(menuItem, library + "." + name);
   }

   public static void displayHelp(String name, String library) {
      PlatformUI.getWorkbench().getHelpSystem().displayHelp(library + "." + name);
   }

   public static void displayHelp(String contextId) {
      PlatformUI.getWorkbench().getHelpSystem().displayHelp();
   }

}

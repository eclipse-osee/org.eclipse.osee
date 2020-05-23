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

package org.eclipse.osee.framework.ui.plugin.util;

import static org.eclipse.osee.framework.core.data.HelpContextRegistry.asContext;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.framework.core.data.HelpContext;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

public class HelpUtil {

   public static void setHelp(Control control, HelpContext context) {
      getHelpSystem().setHelp(control, context.asReference());
   }

   public static void setHelp(IAction action, HelpContext context) {
      getHelpSystem().setHelp(action, context.asReference());
   }

   public static void setHelp(Menu menu, HelpContext context) {
      getHelpSystem().setHelp(menu, context.asReference());
   }

   public static void setHelp(MenuItem menuItem, HelpContext context) {
      getHelpSystem().setHelp(menuItem, context.asReference());
   }

   public static void displayHelp(HelpContext context) {
      getHelpSystem().displayHelp(context.asReference());
   }

   @Deprecated
   public static void setHelp(Control control, String name, String library) {
      setHelp(control, asContext(library, name));
   }

   @Deprecated
   public static void setHelp(IAction action, String name, String library) {
      setHelp(action, asContext(library, name));
   }

   @Deprecated
   public static void setHelp(Menu menu, String name, String library) {
      setHelp(menu, asContext(library, name));
   }

   @Deprecated
   public static void setHelp(MenuItem menuItem, String name, String library) {
      setHelp(menuItem, asContext(library, name));
   }

   @Deprecated
   public static void displayHelp(String name, String library) {
      displayHelp(asContext(library, name));
   }

   @Deprecated
   public static void displayHelp(String contextId) {
      getHelpSystem().displayHelp(contextId);
   }

   public static IWorkbenchHelpSystem getHelpSystem() {
      return PlatformUI.getWorkbench().getHelpSystem();
   }

}

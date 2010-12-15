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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

public class HelpUtil {

   private static final Map<String, HelpContext> contexts = new ConcurrentHashMap<String, HelpContext>();

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

   public static HelpContext asContext(String pluginId, String name) {
      String key = HelpContext.asReference(pluginId, name);
      HelpContext context = contexts.get(key);
      if (context == null) {
         context = new HelpContext(pluginId, name);
         contexts.put(context.asReference(), context);
      }
      return context;
   }
}

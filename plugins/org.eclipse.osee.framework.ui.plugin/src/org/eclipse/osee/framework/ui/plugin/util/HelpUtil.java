/*
 * Created on Sep 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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

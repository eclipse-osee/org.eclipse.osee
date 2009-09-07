package org.eclipse.osee.ats.world;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class DropDownAction extends Action implements IMenuCreator {
   private Menu fMenu;

   public DropDownAction() {
      setText("Other");
      setMenuCreator(this);
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
   }

   public void dispose() {
      if (fMenu != null) {
         fMenu.dispose();
         fMenu = null;
      }
   }

   public Menu getMenu(Menu parent) {
      return null;
   }

   public Menu getMenu(Control parent) {
      if (fMenu != null) fMenu.dispose();

      fMenu = new Menu(parent);
      int i = 0;
      Action filterAction = new Action("Filter") {
         public void run() {
         }
      };
      Action gen = new Action("gen", IAction.AS_DROP_DOWN_MENU) {
         public void run() {
         }
      };
      addActionToMenu(fMenu, filterAction);
      new MenuItem(fMenu, SWT.SEPARATOR);
      addActionToMenu(fMenu, gen);

      return fMenu;
   }

   protected void addActionToMenu(Menu parent, Action action) {
      ActionContributionItem item = new ActionContributionItem(action);
      item.fill(parent, -1);
   }

   public void run() {

   }

   /**
    * Get's rid of the menu, because the menu hangs on to * the searches, etc.
    */
   void clear() {
      dispose();
   }
}
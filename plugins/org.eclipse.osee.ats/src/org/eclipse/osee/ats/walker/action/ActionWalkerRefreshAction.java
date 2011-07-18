/*
 * Created on Jul 15, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.walker.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.walker.ActionWalkerView;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class ActionWalkerRefreshAction extends Action {

   private final ActionWalkerView view;

   public ActionWalkerRefreshAction(ActionWalkerView view) {
      super("Refresh", ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      this.view = view;
   }

   @Override
   public void run() {
      view.refresh();
   }

}

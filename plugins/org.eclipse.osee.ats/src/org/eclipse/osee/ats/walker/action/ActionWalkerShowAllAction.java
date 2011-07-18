/*
 * Created on Jul 15, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.walker.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.walker.ActionWalkerView;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class ActionWalkerShowAllAction extends Action {

   private final ActionWalkerView view;
   private boolean showAll = false;

   public ActionWalkerShowAllAction(ActionWalkerView view) {
      super("Toggle Show All", ImageManager.getImageDescriptor(FrameworkImage.EXPAND_ALL));
      this.view = view;
   }

   @Override
   public void run() {
      showAll = !showAll;
      view.setShowAll(showAll);
   }
}

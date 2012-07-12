/*
 * Created on Jul 15, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.walker.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.walker.ActionWalkerView;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ActionWalkerLayoutAction extends Action {

   private final ActionWalkerView view;

   public ActionWalkerLayoutAction(ActionWalkerView view) {
      super("Change Layout", ImageManager.getImageDescriptor(AtsImage.LAYOUT));
      this.view = view;
   }

   @Override
   public void run() {
      view.getLayoutMgr().nextLayout();
      setText("Change Layout (" + view.getLayoutMgr().getCurrentLayoutName() + ")");
   }

}

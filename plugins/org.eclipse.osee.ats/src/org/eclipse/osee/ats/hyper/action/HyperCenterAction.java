/*
 * Created on Oct 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.hyper.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.hyper.HyperView;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class HyperCenterAction extends Action {

   private final HyperView hyperView;

   public HyperCenterAction(final HyperView hyperView) {
      this.hyperView = hyperView;
      setText("Center");
      setToolTipText("Center");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.CENTER));
   }

   @Override
   public void run() {
      hyperView.center();
   }

}

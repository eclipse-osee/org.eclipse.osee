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

public class HyperZoomInAction extends Action {

   private final HyperView hyperView;

   public HyperZoomInAction(final HyperView hyperView) {
      this.hyperView = hyperView;
      setText("Zoom In");
      setToolTipText("Zoom In");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ZOOM_IN));
   }

   @Override
   public void run() {
      if (hyperView.homeSearchItem == null) {
         return;
      }
      hyperView.zoom.pcRadius += hyperView.zoom.pcRadiusFactor;
      hyperView.zoom.uuRadius += hyperView.zoom.uuRadiusFactor;
      hyperView.zoom.xSeparation += hyperView.zoom.xSeparationFactor;
      hyperView.refresh();
   }

}

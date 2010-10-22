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

public class HyperZoomOutAction extends Action {

   private final HyperView hyperView;

   public HyperZoomOutAction(final HyperView hyperView) {
      this.hyperView = hyperView;
      setText("Zoom Out");
      setToolTipText("Zoom Out");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ZOOM_OUT));
   }

   @Override
   public void run() {
      if (hyperView.homeSearchItem == null) {
         return;
      }
      if (hyperView.zoom.pcRadius >= hyperView.zoom.pcRadiusFactor) {
         hyperView.zoom.pcRadius -= hyperView.zoom.pcRadiusFactor;
      }
      if (hyperView.zoom.uuRadius >= hyperView.zoom.uuRadiusFactor) {
         hyperView.zoom.uuRadius -= hyperView.zoom.uuRadiusFactor;
      }
      if (hyperView.zoom.xSeparation >= hyperView.zoom.xSeparationFactor) {
         hyperView.zoom.xSeparation -= hyperView.zoom.xSeparationFactor;
      }
      hyperView.refresh();
   }

}

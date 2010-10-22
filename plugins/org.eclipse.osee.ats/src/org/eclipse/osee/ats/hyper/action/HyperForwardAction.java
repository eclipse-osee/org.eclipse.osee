/*
 * Created on Oct 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.hyper.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.hyper.HyperView;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class HyperForwardAction extends Action {

   private final HyperView hyperView;

   public HyperForwardAction(final HyperView hyperView) {
      this.hyperView = hyperView;
      setText("Forward");
      setToolTipText("Forward");
      setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
   }

   @Override
   public void run() {
      hyperView.printBackList("pre forwardSelected");
      if (hyperView.getBackList().size() - 1 > hyperView.backListIndex) {
         hyperView.backListIndex++;
         hyperView.jumpTo(hyperView.getBackList().get(hyperView.backListIndex));
      }
      hyperView.printBackList("post forwardSelected");
   }

}

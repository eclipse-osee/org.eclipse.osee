/*
 * Created on Oct 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.hyper.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ats.hyper.HyperView;

public class HyperExpandTitleAction extends Action {

   private final HyperView hyperView;

   public HyperExpandTitleAction(final HyperView hyperView) {
      super("Expand Titles", IAction.AS_CHECK_BOX);
      this.hyperView = hyperView;
      setToolTipText("Expand Titles");
   }

   @Override
   public void run() {
      hyperView.refresh();
   }

}

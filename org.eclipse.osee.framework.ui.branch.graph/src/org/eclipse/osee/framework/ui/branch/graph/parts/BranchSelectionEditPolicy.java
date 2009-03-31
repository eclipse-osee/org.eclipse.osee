package org.eclipse.osee.framework.ui.branch.graph.parts;

import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.osee.framework.ui.branch.graph.figure.BranchFigure;

public class BranchSelectionEditPolicy extends NonResizableEditPolicy {

   protected BranchFigure getLabel() {
      return (BranchFigure) getHostFigure();
   }

   /**
    * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#hideFocus()
    */
   protected void hideFocus() {
      //      getLabel().setFocus(false);
   }

   /**
    * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#hideSelection()
    */
   protected void hideSelection() {
      //      getLabel().setSelected(false);
      //      getLabel().setFocus(false);

   }

   /**
    * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#showFocus()
    */
   protected void showFocus() {
      //      getLabel().setFocus(true);
   }

   /**
    * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#showSelection()
    */
   protected void showPrimarySelection() {
      //      getLabel().setSelected(true);
      //      getLabel().setFocus(true);
   }

   /**
    * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#showSelection()
    */
   protected void showSelection() {
      //      getLabel().setSelected(true);
      //      getLabel().setFocus(false);
   }

}

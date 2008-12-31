/*
 * Created on Dec 26, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.editor.model;

import org.eclipse.draw2d.Graphics;

/**
 * @author Donald G. Dunne
 */
public class ReturnTransitionConnection extends TransitionConnection {

   /**
    * @param source
    * @param target
    */
   public ReturnTransitionConnection(Shape source, Shape target) {
      super(source, target);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Connection#getLineStyle()
    */
   @Override
   public int getLineStyle() {
      return Graphics.LINE_DASH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Connection#getLabel()
    */
   @Override
   public String getLabel() {
      return "Return Transition";
   }
}

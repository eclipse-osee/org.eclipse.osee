/*
 * Created on Dec 26, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.editor.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DefaultTransitionConnection extends TransitionConnection {

   /**
    * @param source
    * @param target
    */
   public DefaultTransitionConnection(Shape source, Shape target) {
      super(source, target);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Connection#getForegroundColor()
    */
   @Override
   public Color getForegroundColor() {
      return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Connection#getLineWidth()
    */
   @Override
   public int getLineWidth() {
      return 3;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Connection#getLabel()
    */
   @Override
   public String getLabel() {
      return "Default Transition";
   }

}

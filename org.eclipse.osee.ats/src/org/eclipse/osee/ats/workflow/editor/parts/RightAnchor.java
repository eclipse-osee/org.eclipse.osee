package org.eclipse.osee.ats.workflow.editor.parts;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Donald G. Dunne
 */
class RightAnchor extends ChopboxAnchor {

   RightAnchor(IFigure source) {
      super(source);
   }

   @Override
   public Point getLocation(Point reference) {
      Rectangle r = getOwner().getBounds().getCopy();
      getOwner().translateToAbsolute(r);
      return r.getRight();
   }

}

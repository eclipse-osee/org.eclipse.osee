/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workdef.viewer.parts;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Donald G. Dunne
 */
class LeftAnchor extends ChopboxAnchor {

   LeftAnchor(IFigure source) {
      super(source);
   }

   @Override
   public Point getLocation(Point reference) {
      Rectangle r = getOwner().getBounds().getCopy();
      getOwner().translateToAbsolute(r);
      return r.getLeft();
   }

}

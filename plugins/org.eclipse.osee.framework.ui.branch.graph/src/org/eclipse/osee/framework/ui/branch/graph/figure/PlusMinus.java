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
package org.eclipse.osee.framework.ui.branch.graph.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Toggle;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Roberto E. Escobar
 */
public class PlusMinus extends Toggle {

   {
      setPreferredSize(9, 9);
   }

   protected void paintFigure(Graphics g) {
      super.paintFigure(g);
      Rectangle r = Rectangle.SINGLETON;
      r.setBounds(getBounds()).resize(-1, -1);
      g.drawRectangle(r);
      int xMid = r.x + r.width / 2;
      int yMid = r.y + r.height / 2;
      g.drawLine(r.x + 2, yMid, r.right() - 2, yMid);
      if (!isSelected()) g.drawLine(xMid, r.y + 2, xMid, r.bottom() - 2);
   }

}

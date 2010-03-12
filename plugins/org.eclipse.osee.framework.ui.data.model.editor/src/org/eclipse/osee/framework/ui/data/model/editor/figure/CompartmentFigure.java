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
package org.eclipse.osee.framework.ui.data.model.editor.figure;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Roberto E. Escobar
 */
public class CompartmentFigure extends Figure {

   public CompartmentFigure() {
      ToolbarLayout layout = new ToolbarLayout();
      layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
      layout.setStretchMinorAxis(false);
      layout.setSpacing(2);
      setLayoutManager(layout);
      setBorder(new CompoundBorder(new CompartmentFigureBorder(), new MarginBorder(1)));
   }

   public Dimension getPreferredSize(int wHint, int hHint) {
      Dimension size = super.getPreferredSize(wHint, hHint);
      size.height = Math.max(size.height, 10);
      return size;
   }

   private final static class CompartmentFigureBorder extends AbstractBorder {
      private static final Insets INSETS = new Insets(2, 0, 0, 1);

      public Insets getInsets(IFigure figure) {
         return INSETS;
      }

      public void paint(IFigure figure, Graphics graphics, Insets insets) {
         graphics.setForegroundColor(ColorConstants.black);
         Rectangle rect = getPaintRectangle(figure, insets);
         graphics.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
      }
   }
}

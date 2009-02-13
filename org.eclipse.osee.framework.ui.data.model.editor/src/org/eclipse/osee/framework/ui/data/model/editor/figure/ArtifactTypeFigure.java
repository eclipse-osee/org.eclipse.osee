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

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeFigure extends SelectableFigure {

   private Panel attributePane;
   private Panel relationPane;
   private Panel header;

   public ArtifactTypeFigure(Image image, String name, Color bgColor, Color fgColor) {
      header = new Panel();
      BorderLayout layout1;
      header.setLayoutManager(layout1 = new BorderLayout());
      layout1.setHorizontalSpacing(20);
      header.add(new ImageFigure(image), BorderLayout.LEFT);
      header.add(new Label(name), BorderLayout.CENTER);

      header.setFont(ODMConstants.HEADER_FONT);
      header.setBorder(new MarginBorder(3, 5, 3, 5));

      attributePane = new Panel();
      ToolbarLayout layout;
      attributePane.setLayoutManager(layout = new ToolbarLayout());
      layout.setStretchMinorAxis(false);
      attributePane.setBorder(new SeparatorBorder());

      relationPane = new Panel();
      relationPane.setLayoutManager(layout = new ToolbarLayout());
      layout.setStretchMinorAxis(false);
      relationPane.setBorder(new SeparatorBorder());

      setBorder(new LineBorder());
      setLayoutManager(new ToolbarLayout());

      add(header);
      add(attributePane);
      add(relationPane);
      setOpaque(true);
      setBackgroundColor(bgColor);
   }

   public void addAttribute(IFigure figure) {
      attributePane.add(figure);
   }

   public void addRelation(IFigure figure) {
      relationPane.add(figure);
   }

   private final class SeparatorBorder extends MarginBorder {
      SeparatorBorder() {
         super(3, 5, 3, 5);
      }

      public void paint(IFigure figure, Graphics graphics, Insets insets) {
         Rectangle where = getPaintRectangle(figure, insets);
         graphics.drawLine(where.getTopLeft(), where.getTopRight());
      }
   }

}
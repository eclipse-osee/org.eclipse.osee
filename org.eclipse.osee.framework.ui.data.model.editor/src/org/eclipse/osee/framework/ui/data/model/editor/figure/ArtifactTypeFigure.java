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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeFigure extends RoundedRectangle {

   private DataTypeFigure header;
   private IFigure body;

   public ArtifactTypeFigure(IFigure namespaceFigure, IFigure nameFigure) {
      setOpaque(true);
      ToolbarLayout layout = new ToolbarLayout();
      layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
      setLayoutManager(layout);

      //      setBorder(SimpleEtchedBorder.singleton);
      //      setBorder(new CompoundBorder(SimpleEtchedBorder.singleton, new LineBorder(ColorConstants.black, 1)));
      //      setBackgroundColor(ColorConstants.white);

      header = new DataTypeFigure(namespaceFigure, nameFigure);
      header.setBorder(new MarginBorder(4));
      ToolbarLayout layout2 = new ToolbarLayout(true);
      layout2.setStretchMinorAxis(true);
      layout2.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
      layout2.setSpacing(5);
      header.setLayoutManager(layout2);
      add(header);

      body = new Figure();
      body.setLayoutManager(new ToolbarLayout());
      add(body);
   }

   public IFigure getContentPane() {
      return body;
   }

   public IFigure getNamespaceFigure() {
      return header.getNamespaceFigure();
   }

   public IFigure getNameFigure() {
      return header.getNameFigure();
   }

   public void setHeaderIcon(Image image) {
      header.setImage(image);
   }
}

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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeFigure extends Figure {

   private IFigure body;
   private IFigure header;

   public ArtifactTypeFigure(IFigure header) {
      this.header = header;
      setLayoutManager(new ToolbarLayout());
      setBorder(new CompoundBorder(new LineBorder(ColorConstants.black, 1), new MarginBorder(1)));
      setBackgroundColor(ColorConstants.white);
      setOpaque(true);
      add(header);
      body = new Figure();
      body.setLayoutManager(new ToolbarLayout());
      add(body);
   }

   public IFigure getContentPane() {
      return body;
   }

   public IFigure getHeader() {
      return header;
   }

}

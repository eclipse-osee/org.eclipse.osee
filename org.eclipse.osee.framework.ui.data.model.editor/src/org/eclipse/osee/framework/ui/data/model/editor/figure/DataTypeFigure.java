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
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeFigure extends Figure {

   private ImageFigure iconFigure;
   private IFigure namespaceFigure;
   private IFigure nameFigure;

   public DataTypeFigure(IFigure namespaceFigure, IFigure nameFigure) {
      this.namespaceFigure = namespaceFigure;
      this.nameFigure = nameFigure;
      this.iconFigure = new ImageFigure();

      setLayoutManager(new ToolbarLayout(true));

      add(iconFigure);
      add(namespaceFigure);
      Label separator = new Label(":");
      separator.setTextAlignment(PositionConstants.CENTER);
      add(separator);
      add(nameFigure);
   }

   public IFigure getNamespaceFigure() {
      return namespaceFigure;
   }

   public IFigure getNameFigure() {
      return nameFigure;
   }

   public void setImage(Image image) {
      iconFigure.setImage(image);
   }
}

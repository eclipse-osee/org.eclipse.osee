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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeFigure extends RoundedRectangle {

   public DataTypeFigure(Color bgColor, Color fgColor) {
      setOpaque(true);
      setFont(Display.getDefault().getSystemFont());
      setLayoutManager(new ToolbarLayout());
      setBorder(new MarginBorder(1));
      setBackgroundColor(bgColor);
      setForegroundColor(fgColor);
   }

   public void addContents(IFigure figure) {
      add(figure);
   }
}

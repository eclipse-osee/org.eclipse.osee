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

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.LabeledContainer;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * @author Roberto E. Escobar
 */
public class FrameFigure extends LabeledContainer {

   static class InternalFrameBorder extends CompoundBorder {
      InternalFrameBorder() {
         TitleBarBorder titlebar = new TitleBarBorder();
         titlebar.setTextColor(ColorConstants.white);
         titlebar.setBackgroundColor(ColorConstants.darkGray);

         outer = new CompoundBorder(new SchemeBorder(SchemeBorder.SCHEMES.RAISED), titlebar);

         inner =
               new CompoundBorder(new LineBorder(FigureUtilities.mixColors(ColorConstants.buttonDarker,
                     ColorConstants.button), 3), new SchemeBorder(SchemeBorder.SCHEMES.LOWERED));

      }
   }

   public FrameFigure() {
      super(new InternalFrameBorder());
      setLayoutManager(new BorderLayout());
      setOpaque(true);
   }

   public FrameFigure(String title) {
      this();
      setLabel(title);
   }

   public Dimension getPreferredSize(int wHint, int hHint) {
      Dimension size = super.getPreferredSize(wHint, hHint);
      size.height = Math.max(size.height, 10);
      return size;
   }
}
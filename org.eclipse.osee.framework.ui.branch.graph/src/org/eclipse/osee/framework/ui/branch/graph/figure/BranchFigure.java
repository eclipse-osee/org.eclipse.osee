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
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphColorConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class BranchFigure extends RoundedRectangle {

   public BranchFigure(String branchName, Image branchImage, IFigure toolTip, Color bgcolor, Color fgcolor) {
      super();
      setOpaque(true);
      setBorder(new MarginBorder(2));
      setLayoutManager(new BorderLayout());

      setBackgroundColor(bgcolor);
      setForegroundColor(fgcolor);

      add(new ImageFigure(branchImage), BorderLayout.LEFT);
      add(FigureFactory.createLabel(branchName, getFont(), PositionConstants.CENTER, GraphColorConstants.FONT_COLOR),
            BorderLayout.CENTER);
      setToolTip(toolTip);
      setCursor(Cursors.HAND);
   }
}

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
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;

/**
 * @author Roberto E. Escobar
 */
public class TxFigure extends Ellipse {

   private Color bgcolor;
   private boolean selected;
   private final long txNumber;

   public TxFigure(long txNumber, IFigure toolTip, Color bgcolor, Color fgcolor) {
      this.txNumber = txNumber;
      this.bgcolor = bgcolor;
      setLayoutManager(new BorderLayout());
      setBackgroundColor(bgcolor);
      setForegroundColor(fgcolor);
      setOpaque(true);
      add(new Label(String.valueOf(txNumber)), BorderLayout.CENTER);
      setToolTip(toolTip);
      setCursor(Cursors.HAND);
   }

   public long getTxNumber() {
      return txNumber;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
      if (selected) {
         setBackgroundColor(ColorConstants.white);
      } else {
         setBackgroundColor(bgcolor);
      }
   }

   public boolean isSelected() {
      return selected;
   }
}

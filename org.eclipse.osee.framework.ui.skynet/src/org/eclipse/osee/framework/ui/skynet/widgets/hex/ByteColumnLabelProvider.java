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
package org.eclipse.osee.framework.ui.skynet.widgets.hex;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;

public class ByteColumnLabelProvider extends ColumnLabelProvider {

   private final int column;
   private final Font font;

   public ByteColumnLabelProvider(Font font, int column) {
      super();
      this.font = font;
      this.column = column;
   }

   @Override
   public String getToolTipText(Object element) {
      HexTableRow row = (HexTableRow) element;
      if (column < row.length) {
         return row.getToolTip(column);
      }
      return null;
   }

   public Point getToolTipShift(Object object) {
      return new Point(12, 12);
   }

   public int getToolTipDisplayDelayTime(Object object) {
      return 125;
   }

   public int getToolTipTimeDisplayed(Object object) {
      return 5000;
   }

   @Override
   public Color getBackground(Object element) {
      HexTableRow row = (HexTableRow) element;
      if (column < row.length) {
         return row.getBackgroundColor(column);
      }
      return null;
   }

   @Override
   public String getText(Object element) {
      HexTableRow row = (HexTableRow) element;
      if (column < row.length) {
         return row.getText(column);
      }
      return null;
   }

   @Override
   public Font getFont(Object element) {
      return font;
   }

}
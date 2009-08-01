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
import org.eclipse.osee.framework.ui.skynet.FontManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;

public class AsciiColumnLabelProvider extends ColumnLabelProvider {

   private final int associatedColumn;

   public AsciiColumnLabelProvider(int column) {
      super();
      this.associatedColumn = column;
   }

   @Override
   public String getToolTipText(Object element) {
      HexTableRow row = (HexTableRow) element;
      if (associatedColumn < row.length) {
         return row.getToolTip(associatedColumn);
      }
      return null;
   }

   @Override
   public Point getToolTipShift(Object object) {
      return new Point(12, 12);
   }

   @Override
   public int getToolTipDisplayDelayTime(Object object) {
      return 125;
   }

   @Override
   public int getToolTipTimeDisplayed(Object object) {
      return 5000;
   }

   @Override
   public Color getBackground(Object element) {
      HexTableRow row = (HexTableRow) element;
      if (associatedColumn < row.length) {
         return row.getBackgroundColor(associatedColumn);
      }
      return null;
   }

   @Override
   public String getText(Object element) {
      HexTableRow row = (HexTableRow) element;
      if (associatedColumn < row.length) {
         return row.getAscii(associatedColumn);
      }
      return null;
   }

   @Override
   public Font getFont(Object element) {
      return FontManager.getCourierNew8();
   }

}
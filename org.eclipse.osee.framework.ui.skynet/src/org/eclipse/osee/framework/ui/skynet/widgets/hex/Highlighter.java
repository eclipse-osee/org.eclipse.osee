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

import java.util.Arrays;
import org.eclipse.swt.graphics.Color;

class Highlighter implements IHexTblHighlighter {
   private int index;
   private int length;
   private Color color;
   private final HexTableContentProvider provider;
   private boolean displayAsAscii = false;

   /**
    * @param row
    * @param endRow
    * @param col
    */
   Highlighter(HexTableContentProvider provider, int index, int length, Color color) {
      this.index = index;
      this.length = length;
      this.color = color;
      this.provider = provider;
   }

   @Override
   public void highlight() {
      highlight(color, displayAsAscii);
   }

   private void highlight(Color highlightColor, boolean ascii) {
      int row = index / provider.getBytesPerRow();
      int endRow = row;
      int col = index - (row * provider.getBytesPerRow());
      HexTableRow[] elements = provider.getElements();
      for (int i = 0; i < length; i++) {
         HexTableRow e = elements[endRow];
         e.setBackgroundColor(col, highlightColor);
         e.setDisplayAsAscii(col, ascii);
         col++;
         if (col >= e.length) {
            endRow++;
            col = 0;
         }
      }
      provider.getViewer().update(Arrays.copyOfRange(elements, row, endRow + 1), null);
   }

   @Override
   public void setColor(Color color) {
      this.color = color;
      highlight();
   }

   @Override
   public void setRange(int start, int length) {
      this.index = start;
      this.length = length;
      highlight(provider.getViewer().getTable().getBackground(), false);
      highlight();
   }

   @Override
   public void setDisplayAsAscii(boolean displayAsAscii) {
      this.displayAsAscii = displayAsAscii;
   }

}
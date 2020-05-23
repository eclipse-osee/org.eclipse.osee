/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.swt.hex;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

class HexTableContentProvider implements ILazyContentProvider {
   private final TableViewer viewer;
   private byte[] array;
   private final int bytesPerRow;
   private HexTableRow[] elements;

   HexTableContentProvider(TableViewer viewer, int bytesPerRow) {
      this.viewer = viewer;
      this.bytesPerRow = bytesPerRow;
   }

   HexTableRow[] getElements() {
      return elements;
   }

   int getBytesPerRow() {
      return bytesPerRow;
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      if (oldInput != null && newInput != null) {
         byte[] oldArray = (byte[]) oldInput;
         byte[] newArray = (byte[]) newInput;
         if (oldArray.length == newArray.length) {
            // same array length so we are done
            this.array = newArray;
            return;
         }
      }
      if (newInput != null) {
         this.array = (byte[]) newInput;
         int rowCOunt = (array.length + bytesPerRow - 1) / bytesPerRow;
         elements = new HexTableRow[rowCOunt];
         int offset = 0;
         int bytesLeft = array.length;
         for (int i = 0; i < rowCOunt; i++) {
            elements[i] = new HexTableRow(offset, bytesLeft >= bytesPerRow ? bytesPerRow : bytesLeft, array);
            offset += bytesPerRow;
            bytesLeft -= bytesPerRow;
         }
      }
   }

   /**
    * @return the viewer
    */
   TableViewer getViewer() {
      return viewer;
   }

   @Override
   public void updateElement(int index) {
      viewer.replace(elements[index], index);
   }
}
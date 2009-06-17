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
package org.eclipse.osee.ote.ui.mux.datatable;

import java.nio.ByteBuffer;

/**
 * @author Ky Komadino
 */
public class DataNode {
   RowNode row1, row2, row3, row4;
   byte[] temp;

   public DataNode() {
      row1 = new RowNode();
      row2 = new RowNode();
      row3 = new RowNode();
      row4 = new RowNode();
      temp = new byte[16];
   }
   
   public RowNode getRow(int row) {
      switch (row) {
         case 1: return row1;
         case 2: return row2;
         case 3: return row3;
         case 4: return row4;
         default: return row1;
      }
   }
   
   public synchronized void setData(ByteBuffer data) {
      // discard header bytes
      if (data.remaining() <= 15)
         return;
      else {
         temp = new byte[15];
         data.get(temp, 0, 15);
      }
      
      int copySize = data.remaining() >= 16 ? 16 : data.remaining();
      temp = new byte[copySize];
      data.get(temp, 0, copySize);
      row1.setData(temp);
      copySize = data.remaining() >= 16 ? 16 : data.remaining();
      temp = new byte[copySize];
      data.get(temp, 0, copySize);
      row2.setData(temp);
      copySize = data.remaining() >= 16 ? 16 : data.remaining();
      temp = new byte[copySize];
      data.get(temp, 0, copySize);
      row3.setData(temp);
      copySize = data.remaining() >= 16 ? 16 : data.remaining();
      temp = new byte[copySize];
      data.get(temp, 0, copySize);
      row4.setData(temp);
   }
}

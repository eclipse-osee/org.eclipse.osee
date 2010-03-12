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
package org.eclipse.osee.ote.message.tool.rec.entry;

import java.nio.ByteBuffer;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.osee.ote.message.tool.rec.RecUtil;


public class ArrayElementEntry implements IElementEntry {

	private final ArrayElement element;
	private final byte[] nameAsBytes;
	private static final byte[] LENGTH_BYTES = ".LENGTH".getBytes();

	public ArrayElementEntry(ArrayElement element) {
		this.element = element;
		nameAsBytes = element.getElementPathAsString().getBytes();
	}

	public ArrayElement getElement() {
		return element;
	}

	public void write(ByteBuffer buffer, MemoryResource mem, int limit) {
		mem.setOffset(element.getMsgData().getMem().getOffset());
		int msgLimit = limit - element.getArrayStartOffset();
		int length = element.getArrayEndOffset() - element.getArrayStartOffset();
		length = msgLimit < length ? msgLimit : length;
      buffer.put(nameAsBytes).put(LENGTH_BYTES).put(COMMA).put(Integer.toString(length).getBytes()).put(COMMA).put(
            nameAsBytes).put(COMMA);
      for (int i = 0; i < length; i++) {
         RecUtil.byteToAsciiHex(element.getValue(mem, i), buffer);
         buffer.put((byte) ' ');
      }
      buffer.put(COMMA);
	}


	
	public static void main(String[] args) {
	   ByteBuffer buffer = ByteBuffer.allocate(100);

	   RecUtil.byteToAsciiHex((byte) 21, buffer);
      buffer.put((byte) ' ');
      RecUtil.byteToAsciiHex((byte) 255, buffer);
      buffer.put((byte) ' ');
      RecUtil.byteToAsciiHex((byte) 1, buffer);
      buffer.put((byte) ' ');
      RecUtil.byteToAsciiHex((byte) 254, buffer);
      buffer.put((byte) ' ');
      RecUtil.byteToAsciiHex((byte) 128, buffer);
      buffer.put((byte) ' ');
      RecUtil.byteToAsciiHex((byte) 16, buffer);
      buffer.put((byte) ' ');
      RecUtil.byteToAsciiHex((byte) 17, buffer);
      buffer.put((byte) ' ');
      RecUtil.byteToAsciiHex((byte) 0, buffer);
      buffer.put((byte) ' ');
      RecUtil.byteToAsciiHex((byte) 12, buffer);
      buffer.flip();
      byte[] data = new byte[buffer.remaining()];
      buffer.get(data);
      String ascii = new String(data);
      System.out.println(ascii);
   }
	
	
}

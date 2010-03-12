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
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.IEnumValue;


public class EnumeratedElementEntry implements IElementEntry {

	private final byte[] nameAsBytes;
	private final byte[][] valueToBytes;
	private final EnumeratedElement<?> element;
	private static final byte[] SPACE_LEFT_PAREN = new byte[]{' ', '('};
	public EnumeratedElementEntry(EnumeratedElement<?> element) {
		this.element = element;
		nameAsBytes = element.getElementPathAsString().getBytes();
		valueToBytes = new byte[element.getEnumValues().length][];
		for (Enum<?> val : element.getEnumValues()) {
			valueToBytes[val.ordinal()] = val.name().getBytes();
		}
	}
	
	public EnumeratedElement<?> getElement() {
		return element;
	}

	public void write(ByteBuffer buffer, MemoryResource mem, int limit) {
		mem.setOffset(element.getMsgData().getMem().getOffset());
		Enum<?> val = element.valueOf(mem);
		byte[] bytes = Integer.toString(((IEnumValue)val).getIntValue()).getBytes();
		buffer.put(nameAsBytes).put(COMMA).put(valueToBytes[val.ordinal()]);
		buffer.put(SPACE_LEFT_PAREN).put(bytes).put(RIGHT_PAREN).put(COMMA);

	}

}

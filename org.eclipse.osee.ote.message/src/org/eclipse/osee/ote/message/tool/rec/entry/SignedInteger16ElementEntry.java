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
import org.eclipse.osee.ote.message.elements.SignedInteger16Element;


public class SignedInteger16ElementEntry implements IElementEntry {

	private final SignedInteger16Element element;
	private final byte[] nameAsBytes;


	public SignedInteger16ElementEntry(SignedInteger16Element element) {
		this.element = element;
		nameAsBytes = element.getName().getBytes();
	}

	public SignedInteger16Element getElement() {
		return element;
	}

	public void write(ByteBuffer buffer, MemoryResource mem, int limit) {
		mem.setOffset(element.getMsgData().getMem().getOffset());
		buffer.put(nameAsBytes).put(COMMA).put(element.valueOf(mem).toString().getBytes()).put(COMMA);
	}

}

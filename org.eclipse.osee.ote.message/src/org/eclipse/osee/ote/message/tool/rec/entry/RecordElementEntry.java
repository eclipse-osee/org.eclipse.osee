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
import java.util.Collection;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.tool.rec.ElementEntryFactory;

public class RecordElementEntry implements IElementEntry {

	private final RecordElement element;
	private final IElementEntry[] entries;
    private final byte[] nameAsBytes;
    
	public RecordElementEntry(RecordElement element) {
		this.element = element;
        nameAsBytes = element.getName().getBytes();
        Collection<Element> elements = element.getElementMap().values();
        entries = new IElementEntry[elements.size()];
        int i = 0;
        for (Element elem : elements) {
           entries[i] = ElementEntryFactory.createEntry(elem);
           i++;
        }
	}
	
	public RecordElement getElement() {
		return element;
	}

	public void write(ByteBuffer buffer, MemoryResource mem, int limit) {
//       for (IElementEntry entry : entries) {
//    	   if (entry.getElement().getByteOffset() < limit) {
//    		   buffer.put(nameAsBytes).put((byte)'.');
//    		   entry.write(buffer, mem, limit);
//    		   buffer.put(COMMA);
//    	   }
//       }
	}
	
	public void write(byte[] prefix, ByteBuffer buffer, MemoryResource mem, int limit) {
//	       for (IElementEntry entry : entries) {
//	    	   if (entry.getElement().getByteOffset() < limit) {
//	    		   buffer.put(prefix).put(nameAsBytes).put((byte)'.');
//	    		   entry.write(buffer, mem, limit);
//	    		   buffer.put(COMMA);
//	    	   }
//	       }
		}

}

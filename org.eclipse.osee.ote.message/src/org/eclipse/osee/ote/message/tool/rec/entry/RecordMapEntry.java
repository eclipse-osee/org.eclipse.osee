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
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.elements.RecordMap;


/**
 * @author Ken J. Aguilar
 */
public class RecordMapEntry implements IElementEntry {

   private final RecordMap<? extends RecordElement> element;
   private final RecordElementEntry[] entries;
   
   public RecordMapEntry(final RecordMap<? extends RecordElement> element) {
      this.element = element;
      entries = new RecordElementEntry[element.length()];

      for (int i = 0; i < element.length(); i++) {
         entries[i] = new RecordElementEntry(element.get(i));
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.tool.rec.entry.IElementEntry#getElement()
    */
   public Element getElement() {
      return element;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.tool.rec.entry.IElementEntry#write(java.nio.ByteBuffer, org.eclipse.osee.ote.message.data.MemoryResource)
    */
   public void write(ByteBuffer buffer, MemoryResource mem, int limit) {
//      for (int i = 0; i < element.length(); i++) {
//    	  if (entries[i].getElement().getByteOffset() < limit) {
//    		  final byte[] prefix = String.format("[%d]", i).getBytes();
//    		  entries[i].write(prefix, buffer, mem, limit);
//    		  buffer.put(COMMA);
//    	  }
//      }
   }

}

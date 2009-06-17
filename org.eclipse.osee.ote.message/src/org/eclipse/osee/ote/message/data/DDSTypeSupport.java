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
package org.eclipse.osee.ote.message.data;

import org.eclipse.osee.ote.messaging.dds.service.Key;
import org.eclipse.osee.ote.messaging.dds.service.TypeSupport;

/**
 * @author Andrew M. Finkbeiner
 */
public class DDSTypeSupport extends TypeSupport{

   private Key key;
   private String readerName;
   private String writerName;
   private int size;
   
   public DDSTypeSupport(Key key, String readerName, String writerName, int size){
      this.key = key;
      this.readerName = readerName;
      this.writerName = writerName;
      this.size = size;
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.messaging.dds.service.TypeSupport#getKey()
    */
   @Override
   protected Key getKey() {
      return key;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.messaging.dds.service.TypeSupport#getReaderName()
    */
   @Override
   protected String getReaderName() {
      return this.readerName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.messaging.dds.service.TypeSupport#getTypeDataSize()
    */
   @Override
   protected int getTypeDataSize() {
      return this.size;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.messaging.dds.service.TypeSupport#getWriterName()
    */
   @Override
   protected String getWriterName() {
      return this.writerName;
   }

}

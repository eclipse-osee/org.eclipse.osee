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
package org.eclipse.osee.ote.ui.mux.msgtable;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * @author Ky Komadino
 */
public class MessageNode {
   private final String name;
   private int rt;
   private String rtRt;
   private int transmitReceive;
   private int subaddress;
   private int wordCount;
   private String statusWord;
   private String emulation;
   private String bus;
   private int activity;
   private int errCount;
   private String errType;
   private CharBuffer temp = ByteBuffer.allocate(32).asCharBuffer();

   public MessageNode(String muxId) {
      this.name = muxId;
   }

   /**
    * 
    * @param data - raw mux data from 1553 driver
    */
   public synchronized void setData(ByteBuffer data) {
      if (data.array()[3] == 0 && data.array()[4] == 0) {  // not an RT-RT msg
         rt = ((short)(data.array()[1] & 0x00F8)) >> 3;
         transmitReceive = ((short)(data.array()[1] & 0x0004)) >> 2;
         subaddress = (((short)(data.array()[1] & 0x0003)) << 3) + (((short)(data.array()[2] & 0x00E0)) >> 5);
         temp.clear();
         temp.append(String.format(" "));
         rtRt = temp.flip().toString();
      }
      else {  // is an RT-RT msg, reference the "T" side datawords
         rt = ((short)(data.array()[3] & 0x00F8)) >> 3;
         transmitReceive = ((short)(data.array()[3] & 0x0004)) >> 2;
         subaddress = (((short)(data.array()[3] & 0x0003)) << 3) + (((short)(data.array()[4] & 0x00E0)) >> 5);
         temp.clear();
         temp.append(String.format("%02d", ((short)(data.array()[3] & 0x00F8)) >> 3));
         temp.append((data.array()[3] & 0x0004) >> 2 == 1 ? 'T' : 'R');
         temp.append(String.format("%02d", (((short)(data.array()[3] & 0x0003)) << 3) +
                                           (((short)(data.array()[4] & 0x00E0)) >> 5)));
         rtRt = temp.flip().toString();
      }
      
      if (subaddress == 0 || subaddress == 31)
         wordCount = (short)(data.array()[2] & 0x001F) >= 16 ? 1 : 0;
      else
         wordCount = (short)(data.array()[2] & 0x001F) == 0 ? 32 : (short)(data.array()[2] & 0x001F);
      
      temp.clear();
      temp.append(String.format("0x%02X%02X", data.array()[5], data.array()[6]));
      statusWord = temp.flip().toString();
      
      temp.clear();
      switch (((short)(data.array()[9] & 0x00C0)) >> 6) {
         case 1:  temp.append("-/B");
                  break;
         case 2:  temp.append("A/-");
                  break;
         case 3:  temp.append("A/B");
                  break;
         default: temp.append("MON");
      }
      emulation = temp.flip().toString();
      
      temp.clear();
      if ((short)(data.array()[10] & 0x0008) == 0)
         temp.append(String.format("PRI"));
      else
         temp.append(String.format("SEC"));
      bus = temp.flip().toString();
      
      activity++;
      
      temp.clear();
      if ((short)(data.array()[10] & 0x0080) == 0) {
         temp.append(" ");
      }
      else {
         errCount++;
         temp.append("NO RESPONSE");
      }
      errType = temp.flip().toString();
   }
   
   /**
    * @return Returns the activity.
    */
   public synchronized int getActivity() {
      return activity;
   }

   /**
    * @return Returns the bus.
    */
   public synchronized String getBus() {
      return bus.toString();
   }

   /**
    * @return Returns the emulation.
    */
   public synchronized String getEmulation() {
      return emulation.toString();
   }

   /**
    * @return Returns the errCount.
    */
   public synchronized int getErrCount() {
      return errCount;
   }

   /**
    * @return Returns the errType.
    */
   public synchronized String getErrType() {
      return errType.toString();
   }

   /**
    * @return Returns the name.
    */
   public synchronized String getName() {
      return name;
   }

   /**
    * @return Returns the rt.
    */
   public synchronized int getRt() {
      return rt;
   }
   
   /**
    * @return Returns the rtRt.
    */
   public synchronized String getRtRt() {
      return rtRt.toString();
   }

   /**
    * @return Returns the statusWord.
    */
   public synchronized String getStatusWord() {
      return statusWord.toString();
   }

   /**
    * @return Returns the subaddress.
    */
   public synchronized int getSubaddress() {
      return subaddress;
   }
   
   /**
    * @return Returns the transmitReceive.
    */
   public synchronized int getTransmitReceive() {
      return transmitReceive;
   }
   
   /**
    * @return Returns the wordCount.
    */
   public synchronized int getWordCount() {
      return wordCount;
   }
}

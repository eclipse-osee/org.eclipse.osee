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

import java.nio.ByteBuffer;
import java.util.logging.Level;

import org.eclipse.osee.ote.message.MessageSystemException;


/**
 * @author Andrew M. Finkbeiner
 */
public class MemoryResourceByteBuffer {
   private byte _data[];
   private int _offset;
   public boolean _dataHasChanged;

   private final ByteBuffer buffer;
   
   public MemoryResourceByteBuffer(byte data[], int offset, int length) {
      _data = data;
      _offset = offset;
      _dataHasChanged = false;
      buffer = ByteBuffer.wrap(_data);
   }

   public void setData(byte data[]) {
      _data = data;
      _dataHasChanged = true;
   }

   public byte[] getData() {
      return _data;
   }

   public long getRawDataLong(int offset, int msb, int lsb) {

      if (lsb < 8) {
         return getByte(offset, msb, lsb);
      }
      else if (lsb < 16) {
         return getShort(offset, msb, lsb);
      }
      else if (lsb < 32) {
         offset += _offset;
         if (msb == 0 && lsb == 31) {
            return getIntFromOffset(offset);
         }
         else {
            int i = getIntFromOffset(offset);
            int mask = (1 << (32 - msb)) - 1;
            mask = mask & (0x80000000 >>> lsb);
            return (i & mask) >> (31 - lsb);
         }
      }
      else if (lsb == 63) {
         return getLong(offset, msb, lsb);
      }
      else {
         throw new IllegalArgumentException("lsb greater than 63");
      }
   }

   public int getRawDataInt(int offset, int msb, int lsb) {

      if (lsb < 8) {
         return getByte(offset, msb, lsb);
      }
      else if (lsb < 16) {
         return getShort(offset, msb, lsb);
      }
      else if (lsb < 32) {
         offset += _offset;
         if (msb == 0 && lsb == 31) {
            return getIntFromOffset(offset);
         }
         else {
            int i = getIntFromOffset(offset);
            int mask = (1 << (32 - msb)) - 1;
            mask = mask & (0x80000000 >>> lsb);
            return (i & mask) >> (31 - lsb);
         }
      }
      else {
         throw new IllegalArgumentException("lsb greater than 31");
      }
   }

   public void setRawData(long v, int offset, int msb, int lsb) {
      if (lsb < 8) {
         setByte((int) v, offset, msb, lsb);
      }
      else if (lsb < 16) {
         setShort((int) v, offset, msb, lsb);
      }
      else if (lsb < 32) {
         setInt((int) v, offset, msb, lsb);
      }
      else {
         setLong(v, offset, msb, lsb);
      }
   }

   public byte getByte(int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 7) {
         return getByteFromOffset(offset);
      }
      else {
         int b = getByteFromOffset(offset);
         int mask = (1 << (8 - msb)) - 1;
         mask = mask & (0xFFFFFF80 >>> lsb);
         return (byte) ((b & mask) >> (7 - lsb));
      }
   }

   private byte getByteFromOffset(int offset) {
      return _data[offset];
   }

   public short getShort(int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 15) {
         return getShortFromOffset(offset);
      }
      else {
         int s = getShortFromOffset(offset);
         int mask = (1 << (16 - msb)) - 1;
         mask = mask & (0xFFFF8000 >>> lsb);
         return (short) ((s & mask) >> (15 - lsb));
      }
   }

   private short getShortFromOffset(int offset) {
      int ch1 = _data[offset] & 0xFF;
      int ch2 = _data[offset + 1] & 0xFF;
      return (short) ((ch1 << 8) + (ch2 << 0));
   }

   public char getASCIIChar(int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 7) {
         return getASCIICharFromOffset(offset);
      }
      else if (msb == 8 && lsb == 15) {
         return getASCIICharFromOffset(offset + 1);
      }
      else if (msb == 16 && lsb == 23) {
         return getASCIICharFromOffset(offset + 2);
      }
      else if (msb == 24 && lsb == 31) {
         return getASCIICharFromOffset(offset + 3);
      }
      else {
         throw new IllegalArgumentException("not supported");
      }
   }

   private final char getASCIICharFromOffset(int offset) {
      return (char) _data[offset];
   }

   public int getInt(int offset, int msb, int lsb) {
      
      int nextFourBytes = getIntFromOffset( offset + _offset );
      
      int bitsToShift = 32 -msb;
      int maskLeft = (msb == 0 ? -1 : (1 << (bitsToShift)) - 1);//(int)Math.pow(2, 32 - msb ) -1;
      
      int retVal = (nextFourBytes & maskLeft) >>> (31 - lsb);
      return retVal;
      
//      if (lsb < 8) {
//         return getByte(offset, msb, lsb);
//      }
//      else if (lsb < 16) {
//         return getShort(offset, msb, lsb);
//      }
//      else if (lsb < 32) {
//         offset += _offset;
//         if (msb == 0 && lsb == 31) {
//            return getIntFromOffset(offset);
//         }
//         else {
//            int i = getIntFromOffset(offset);
//            int mask = (1 << (32 - msb)) - 1;
//            mask = mask & (0x80000000 >>> lsb);
//            return (i & mask) >> (31 - lsb);
//         }
//      }
//      else {
//         throw new IllegalArgumentException("lsb greater than 31");
//      }
   }

   private int getIntFromOffset(int offset) {
      int ch1 = 0, ch2 = 0, ch3 = 0, ch4 = 0;
      
      if( _data.length > offset)
         ch1 = _data[offset] & 0xFF;
      if( _data.length > offset + 1)
         ch2 = _data[offset + 1] & 0xFF;
      if( _data.length > offset + 2)
         ch3 = _data[offset + 2] & 0xFF;
      if( _data.length > offset + 3)
         ch4 = _data[offset + 3] & 0xFF;
      return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
   }

   public final long getLong(int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 63) {
         return getLongFromOffset(offset);
      }
      else {
         throw new IllegalArgumentException("gettting long with bits not supported");
      }
   }

   private final long getLongFromOffset(int offset) {
      long byte1 = 0, byte2 = 0, byte3 = 0,
           byte4 = 0, byte5 = 0, byte6 = 0,
           byte7 = 0, byte8 = 0;
      
      if( _data.length > offset)
         byte1 = _data[offset];
      if( _data.length > offset + 1)
         byte2 = _data[offset + 1];
      if( _data.length > offset + 2)
         byte3 = _data[offset + 2];
      if( _data.length > offset + 3)
         byte4 = _data[offset + 3];
      if( _data.length > offset + 4)
         byte5 = _data[offset + 4];
      if( _data.length > offset + 5)
         byte6 = _data[offset + 5];
      if( _data.length > offset + 6)
         byte7 = _data[offset + 6];
      if( _data.length > offset + 7)
         byte8 = _data[offset + 7];
      
      long retVal = (byte1 << 56) +
                    ((byte2 & 0xFF) << 48) +
                    ((byte3 & 0xFF) << 40) +
                    ((byte4 & 0xFF) << 32) +
                    ((byte5 & 0xFF) << 24) +
                    ((byte6 & 0xFF) << 16) +
                    ((byte7 & 0xFF) << 8)  +
                    ((byte8 & 0xFF));
      
      return retVal;
//      return (((long) _data[offset] << 56) + ((long) (_data[offset + 1] & 255) << 48)
//            + ((long) (_data[offset + 2] & 255) << 40) + ((long) (_data[offset + 3] & 255) << 32)
//            + ((long) (_data[offset + 4] & 255) << 24) + ((_data[offset + 5] & 255) << 16)
//            + ((_data[offset + 6] & 255) << 8) + ((_data[offset + 7] & 255) << 0));
   }

   public final String getASCIIString(int offset, int length) {
      offset += _offset;
      // int size = ((lsb - msb) + 1) / 8;

      StringBuffer str = new StringBuffer(length);
      for (int i = 0; i < length; i++) {
         char ch = getASCIICharFromOffset(offset + i);
         if (ch != 0) {// NOTE this was done to be compatible with java
            // Strings that don't null char termination
            str.append(getASCIICharFromOffset(offset + i));
         }
      }
      return str.toString();
   }

   public final String getASCIIString(int offset, int msb, int lsb) {
      offset += _offset;
      int size = ((lsb - msb) + 1) / 8;

      StringBuffer str = new StringBuffer(size);
      for (int i = 0; i < size; i++) {
         char ch = getASCIICharFromOffset(offset + i);
         if (ch != 0) {// NOTE this was done to be compatible with java
            // Strings that don't null char termination
            str.append(getASCIICharFromOffset(offset + i));
         }
      }
      return str.toString();
   }

   public void setBoolean(boolean v, int offset, int msb, int lsb) {
      int i = v ? 1 : 0;
      if (lsb < 8) {
         setByte(i, offset, msb, lsb);
      }
      else if (lsb < 16) {
         setShort(i, offset, msb, lsb);
      }
      else if (lsb < 32) {
         setInt(i, offset, msb, lsb);
      }
      else {
         throw new RuntimeException("Not supported lsb = " + lsb);
      }
   }

   public final void setByte(int v, int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 7) {
         setByteFromOffset(v, offset);
      }
      else {
         if ((v & (1 >>> (7 - (lsb - msb)))) != 0) {
            throw new IllegalArgumentException("Tried to set signal to value that is too large");
         }
         int mask = createMask( msb, lsb, 7);
         v = v << (7 - lsb);
         setByteFromOffset(v | (getByteFromOffset(offset) & mask), offset);
      }
   }

   public final void setBytesInHeader(int v, int offset, int msb, int lsb) {
      if (offset > _offset) {
         throw new IllegalArgumentException("Data beyond header attempting to be set!!!");
      }
      if (msb == 0 && lsb == 7) {
         setByteFromOffset(v, offset);
      }
      else {
         if ((v & (1 >>> (7 - (lsb - msb)))) != 0) {
            throw new IllegalArgumentException("Tried to set signal to value that is too large");
         }
         int mask = (1 << (7 - lsb)) - 1;
         mask = mask | (0xFFFFFF00 >>> msb);
         v = v << (7 - lsb);
         setByteFromOffset(v | (getByteFromOffset(offset) & mask), offset);
      }
   }

   private final void setByteFromOffset(int v, int offset) {
      _data[offset] = (byte) v;
      _dataHasChanged = true;
   }

   private final void setShort(int v, int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 15) {
         setShortFromOffset(v, offset);
      }
      else {
         if ((v & (1 >>> (15 - (lsb - msb)))) != 0) {
            throw new IllegalArgumentException("Tried to set signal to value that is too large");
         }
         int mask = createMask( msb, lsb, 15);

         // shift the value into the correct position within the whole int
         v = v << (15 - lsb);            
         // zero out the element we are setting before oring in the new value
         setShortFromOffset(v | (getShortFromOffset(offset) & mask), offset);
      }
   }

   private final void setShortFromOffset(int v, int offset) {
      _data[offset] = (byte) ((v >>> 8) & 0xFF);
      _data[offset + 1] = (byte) ((v >>> 0) & 0xFF);
      _dataHasChanged = true;
   }

   public final void setASCIIChar(char v, int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 7) {
         setASCIICharFromOffset(v, offset);
      }
      else if (msb == 8 && lsb == 15) {
         setASCIICharFromOffset(v, offset + 1);
      }
      else if (msb == 16 && lsb == 23) {
         setASCIICharFromOffset(v, offset + 2);
      }
      else if (msb == 24 && lsb == 31) {
         setASCIICharFromOffset(v, offset + 3);
      }
      else {
         throw new IllegalArgumentException("only 8 bit char supported");
      }
   }

   private final void setASCIICharFromOffset(char v, int offset) {
      _data[offset] = (byte) (v & 0xFF);
      _dataHasChanged = true;
   }

   public final void setInt(int v, int offset, int msb, int lsb) {
      if (lsb < 8) {
         setByte(v, offset, msb, lsb);
      }
      else if (lsb < 16) {
         setShort(v, offset, msb, lsb);
      }
      else {
         offset += _offset;
         if (msb == 0 && lsb == 31) {
            setIntFromOffset(v, offset);
         }
         else {
            if ((v & (1 >>> (31 - (lsb - msb)))) != 0) {
               throw new IllegalArgumentException("Tried to set signal to value that is too large");
            }
            int mask = createMask( msb, lsb, 31);
            // shift the value into the correct position within the whole int
            v = v << (31 - lsb);            
            // zero out the element we are setting before oring in the new value
            setIntFromOffset(v | (getIntFromOffset(offset) & mask), offset);
         }
      }
   }
   
   private int createMask( int msb, int lsb, int maxBitPosition)
   {
      int maximumElementValue = (int)Math.pow(2, lsb-msb+1) -1;
      int maxValueInPosition = maximumElementValue << (maxBitPosition - lsb);
      //the mask is all ones except at the bit positions we are setting
      int mask = ~maxValueInPosition;
      return mask;
   }

   private final void setIntFromOffset(int v, int offset) {
      if( _data.length > offset)
         _data[offset] = (byte) ((v >>> 24) & 0xFF);
      if( _data.length > offset + 1)
         _data[offset + 1] = (byte) ((v >>> 16) & 0xFF);
      if( _data.length > offset + 2)
         _data[offset + 2] = (byte) ((v >>> 8) & 0xFF);
      if( _data.length > offset + 3)
         _data[offset + 3] = (byte) ((v >>> 0) & 0xFF);
      _dataHasChanged = true;
   }

   public final void setLong(long v, int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 63) {
         setLongFromOffset(v, offset);
      }
      else {
         throw new IllegalArgumentException("not supported");
      }
   }

   private final void setLongFromOffset(long v, int offset) {
      if( _data.length > offset)
         _data[offset] = (byte) (v >>> 56);
      if( _data.length > offset + 1)
         _data[offset + 1] = (byte) (v >>> 48);
      if( _data.length > offset + 2)
         _data[offset + 2] = (byte) (v >>> 40);
      if( _data.length > offset + 3)
         _data[offset + 3] = (byte) (v >>> 32);
      if( _data.length > offset + 4)
         _data[offset + 4] = (byte) (v >>> 24);
      if( _data.length > offset + 5)
         _data[offset + 5] = (byte) (v >>> 16);
      if( _data.length > offset + 6)
         _data[offset + 6] = (byte) (v >>> 8);
      if( _data.length > offset + 7)
         _data[offset + 7] = (byte) (v >>> 0);
      _dataHasChanged = true;
   }

   public final void setASCIIString(String s, int offset, int msb, int lsb) {
      offset += _offset;
      int size = ((lsb - msb) + 1) / 8;

      int len = s.length();
      for (int i = 0; i < len && i < size; i++) {
         setASCIICharFromOffset(s.charAt(i), offset + i);
      }
   }

   public final void setASCIIString(String s, int offset) {
      offset += _offset;
      int len = s.length();
      for (int i = 0; i < len; i++) {
         setASCIICharFromOffset(s.charAt(i), offset + i);
      }
   }

   public boolean getBoolean(int offset, int msb, int lsb) {
      int i;
      if (lsb < 8) {
         i = getByte(offset, msb, lsb);
      }
      else if (lsb < 16) {
         i = getShort(offset, msb, lsb);
      }
      else {
         i = getInt(offset, msb, lsb);
      }
      return i != 0;
   }
   
   public void copyData(int offset, byte[] src, int srcOffset, int length) {
//	   assert(_data.length >= length );
	   if(_data.length < src.length){
		 throw new MessageSystemException("backing byte[] is too small for copy operation", Level.SEVERE);
	   }
	   System.arraycopy(src, srcOffset, _data, offset, length);
	   _dataHasChanged = true;
   }
   
   public void copyData(ByteBuffer src) {
	   src.get(_data);
	   _dataHasChanged = true;
   }
   
   public void copyData(int offset, ByteBuffer src, int length) {
	   src.get(_data, offset, length);
	   _dataHasChanged = true;
   }

   public ByteBuffer getAsBuffer() {
	   return ByteBuffer.wrap(_data);
   }
   
   public void set(ByteBuffer other) {
       buffer.put(other);
   }
   public ByteBuffer getAsBuffer(int offset, int length) {
	   return ByteBuffer.wrap(_data, offset, length);
   }
}

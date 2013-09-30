package org.eclipse.osee.ote.message.data;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

public class MemoryResourceTest {

   @Test
   public void testcopyDataByteBuffer() {
      byte[] data = new byte[32];
      ByteBuffer buffer = ByteBuffer.allocate(32);
      for(int i = 0; i < 16; i++){
         buffer.put((byte)0xDD);
      }
      for(int i = 0; i < 16; i++){
         buffer.put((byte)0xFF);
      }
      buffer.position(16);
      MemoryResource mem = new MemoryResource(data, 0, 64);
      mem.copyData(0, buffer, buffer.remaining());
      
      byte[] answer = new byte[32];
      for(int i = 0; i < 16; i++){
         answer[i] = (byte)0xFF;
      }
      
      Assert.assertArrayEquals(answer, mem.getData());
      
      buffer.position(0);
      mem.copyData(0, buffer, buffer.remaining());
      for(int i = 0; i < 16; i++){
         answer[i] = (byte)0xDD;
      }
      for(int i = 16; i < 32; i++){
         answer[i] = (byte)0xFF;
      }
      
      Assert.assertArrayEquals(answer, mem.getData());
   }

}

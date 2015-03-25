package org.eclipse.osee.ote.endpoint;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class AddressBuffer {
   
   private final ByteBuffer buffer;
   private InetSocketAddress address;
   
   public AddressBuffer(){
      buffer = ByteBuffer.allocate(131072);
   }
   
   public ByteBuffer getBuffer(){
      return buffer;
   }
   
   public InetSocketAddress getAddress(){
      return address;
   }
   
   public void setAddress(InetSocketAddress address){
      this.address = address;
   }

}

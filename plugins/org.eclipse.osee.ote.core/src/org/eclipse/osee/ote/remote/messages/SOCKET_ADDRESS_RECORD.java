package org.eclipse.osee.ote.remote.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.IntegerElement;

public class SOCKET_ADDRESS_RECORD extends ArrayElement{

   public static final int SIZE = 21;
   
   private final EnumeratedElement<ADDRESS_TYPE> type;   
   private final ArrayElement ipaddress;
   private final IntegerElement port;
   
   public SOCKET_ADDRESS_RECORD(Message<?, ?, ?> msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb);
      
      type = new EnumeratedElement<ADDRESS_TYPE>(msg, "type", ADDRESS_TYPE.class, messageData, byteOffset, 0, 7);
      ipaddress = new ArrayElement(msg, "ipaddress", messageData, byteOffset + 1, 0, 8*16-1);
      port = new IntegerElement(msg, "port",  messageData, ipaddress.getByteOffset() + 16, 0, 31);
   }

   public void setAddress(InetAddress address){
      byte[] bytes = address.getAddress();
      
      if(bytes.length == 4){
         type.setValue(ADDRESS_TYPE.IPV4);
      } else {
         type.setValue(ADDRESS_TYPE.IPV6);
      }
      
      ipaddress.zeroize();
      for(int i = 0; i < bytes.length; i++){
         ipaddress.setValue(i, bytes[i]);
      }
   }
   
   public InetAddress getAddress() throws UnknownHostException{
      byte[] bytes = null;
      if(type.getValue() == ADDRESS_TYPE.IPV4){
         bytes = new byte[4];
         for(int i = 0; i < 4; i++){
            bytes[i] = ipaddress.getValue(i);
         }
      } else {
         bytes = new byte[16];
         for(int i = 0; i < 6; i++){
            bytes[i] = ipaddress.getValue(i);
         }
      }
      return InetAddress.getByAddress(bytes);
   }
   
   public int getPort(){
      return port.getValue();
   }
   
   public void setPort(int port){
      this.port.setValue(port);
   }
   
}

package org.eclipse.osee.ote.endpoint;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.eclipse.osee.ote.OTEException;

public class OteEndpointUtil {

   /**
    * Get the socket address for the OTEUdpEndpoint from an encoded URI.  You should catch OTEException and 
    * handle the case when the sting is not as expected.
    * 
    * @param uriAddress
    * @return
    */
   public static InetSocketAddress getAddress(final String uriAddress) throws OTEException {
      String[] addPort = null;
      if(uriAddress.startsWith("tcp://")){
         String add = uriAddress.replaceFirst("tcp://", "");
         addPort = add.split(":");
      } else {
         addPort = uriAddress.split(":");
      }
      if(addPort != null && addPort.length == 2){
         try {
         InetAddress byName = InetAddress.getByName(addPort[0]);
         int port = Integer.parseInt(addPort[1]);
         return new InetSocketAddress(byName, port);
         } catch (UnknownHostException ex){
            throw new OTEException(String.format("Invalid address[%s]", uriAddress), ex);
         } catch (NumberFormatException ex){
            throw new OTEException(String.format("Invalid address format[%s], can't determine port", uriAddress), ex);
         }
      } else {
         throw new OTEException(String.format("Invalid address format[%s]", uriAddress));
      }
   }
   
   public static InetSocketAddress getAddress(final String address, final int port) {
      try {
         InetAddress byName = InetAddress.getByName(address);
         return new InetSocketAddress(byName, port);
      } catch (UnknownHostException ex){
         throw new OTEException(String.format("Invalid address[%s]", address), ex);
      }
   }
   
   public static String getAddressURI(final InetSocketAddress address){
      return String.format("tcp://%s:%d", address.getAddress().getHostAddress(), address.getPort());
   }
   
}

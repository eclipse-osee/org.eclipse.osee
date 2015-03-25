package org.eclipse.osee.ote.endpoint;

import java.net.InetSocketAddress;

import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;

public interface OteUdpEndpoint {
   
   public void setDebugOutput(boolean enable);
   public InetSocketAddress getLocalEndpoint();
   public OteUdpEndpointSender getOteEndpointSender(InetSocketAddress address);
   public void addBroadcast(OteUdpEndpointSender sender);
   public void removeBroadcast(OteUdpEndpointSender sender);
   public CopyOnWriteNoIteratorList<OteUdpEndpointSender> getBroadcastSenders();
   public void addDataProcessor(EndpointDataProcessor processor);
   public void removeDataProcessor(EndpointDataProcessor processor);
}

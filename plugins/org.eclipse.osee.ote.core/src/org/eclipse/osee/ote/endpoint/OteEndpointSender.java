package org.eclipse.osee.ote.endpoint;

import java.net.InetSocketAddress;

import org.eclipse.osee.ote.message.event.OteEventMessage;

public interface OteEndpointSender {

   void send(OteEventMessage sendMessage);

   InetSocketAddress getAddress();

   void stop() throws InterruptedException;

   boolean isClosed();

   void setDebug(boolean debug);

   void start();

}

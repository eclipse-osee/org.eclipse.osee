package org.eclipse.osee.ote.internal;

import java.io.IOException;

import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.io.SystemOutput;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.ConsoleInputMessage;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class OTESystemOutConnection {

   private OteUdpEndpoint endpoint;
   private SystemOutput output;
   private OTESystemOutputHandler outputHandler;
   private ServiceRegistration<EventHandler> subscribe;

   /**
    * osgi
    */
   public void start(){
      outputHandler = new OTESystemOutputHandler(endpoint);
      output.addListener(outputHandler);
      subscribe = OteEventMessageUtil.subscribe(ConsoleInputMessage.TOPIC, new ConsoleInputHandler(output));
   }
   
   /**
    * osgi
    */
   public void stop(){
      output.removeListener(outputHandler);
      if(subscribe != null){
         subscribe.unregister();
         subscribe = null;
      }
   }
   
   /**
    * osgi
    */
   public void bindOteUdpEndpoint(OteUdpEndpoint endpoint){
      this.endpoint = endpoint;
   }
   
   /**
    * osgi
    */
   public void unbindOteUdpEndpoint(OteUdpEndpoint endpoint){
      this.endpoint = null;
   }
   
   /**
    * osgi
    */
   public void bindSystemOutput(SystemOutput output){
      this.output = output;
   }
   
   /**
    * osgi
    */
   public void unbindSystemOutput(SystemOutput output){
      this.output = null;
   }
   
   private static class ConsoleInputHandler implements EventHandler {

      private SystemOutput output;
      private ConsoleInputMessage message;

      public ConsoleInputHandler(SystemOutput output) {
         this.output = output;
         message = new ConsoleInputMessage();
      }

      @Override
      public void handleEvent(Event arg0) {
         message.setBackingBuffer(OteEventMessageUtil.getBytes(arg0));
         try {
            output.write(message.getString());
         } catch (IOException e) {
            e.printStackTrace();
         } catch (ClassNotFoundException e) {
            e.printStackTrace();
         }
      }
      
   }
}

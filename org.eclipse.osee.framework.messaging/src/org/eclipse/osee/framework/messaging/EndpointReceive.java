/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.util.Properties;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.internal.Activator;
import org.eclipse.osee.framework.messaging.internal.ApplicationDistributer;


/**
 * @author b1528444
 *
 */
public abstract class EndpointReceive {

   private ApplicationDistributer distributer;
   
   /**
    * The MessagingGateway implementation must call this method to set the ApplicationDistributer callback so that received messages get propagated to the application.
    * 
    * @param distributer
    */
   public void onBind(ApplicationDistributer distributer){
      this.distributer = distributer;
   }
   
   public void onUnbind(ApplicationDistributer distributer){
      this.distributer = null;
   }
   
   /**
    *  This method must be called by the implementing class when it receives a message so that it gets propagated to the MessagingGateway 
    */
   protected void onReceive(Message message){
      if(distributer == null){
         String errorMsg = String.format("We have recieved message [%s] from [%s], but have no active ApplicationDistributer available.", message.getId().toString(), message.getSource().toString());
         OseeLog.log(Activator.class, Level.WARNING, errorMsg);
      } else {
         distributer.distribute(message);
      }
   }

   public abstract void start(Properties properties);
   public abstract void dispose();
}

package org.eclipse.osee.ote.internal;

import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.remote.messages.AVAILABLE_PHYSICAL_TYPES_REQ;
import org.eclipse.osee.ote.remote.messages.GET_INET_ADDRESS_REQ;
import org.eclipse.osee.ote.remote.messages.STOP_RECORDING_CMD;
import org.eclipse.osee.ote.remote.messages.SerializedRecordCommandMessage;
import org.eclipse.osee.ote.remote.messages.SerializedSetElementMessage;
import org.eclipse.osee.ote.remote.messages.SerializedSubscribeToMessage;
import org.eclipse.osee.ote.remote.messages.SerializedUnSubscribeMessage;
import org.eclipse.osee.ote.remote.messages.SerializedZeroizeElementMessage;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class RemoteMessageServiceHandler {

   private EventAdmin eventAdmin;
   private IRemoteMessageService messageService;
   private ServiceRegistration<EventHandler> addressResponseReg;
   private ServiceRegistration<EventHandler> availablePhysicalTypesReg;
   private ServiceRegistration<EventHandler> subscribeToMessageReg;
   private ServiceRegistration<EventHandler> unsubscribeToMessageReg;
   private ServiceRegistration<EventHandler> recordReg;
   private ServiceRegistration<EventHandler> setElementReg;
   private ServiceRegistration<EventHandler> zeroizeElementReg;
   private ServiceRegistration<EventHandler> stopRecordReg;

   /**
    * osgi
    */
   public void bindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = eventAdmin;
   }
   
   /**
    * osgi
    */
   public void unbindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = null;
   }
   
   /**
    * osgi
    */
   public void bindIRemoteMessageService(IRemoteMessageService messageService){
      this.messageService = messageService;
   }
   
   /**
    * osgi
    */
   public void unbindIRemoteMessageService(IRemoteMessageService messageService){
      this.messageService = null;
   }
   
   /**
    * osgi
    */
   public void start(){
      addressResponseReg = OteEventMessageUtil.subscribe(GET_INET_ADDRESS_REQ.TOPIC, new AddressResponseListener(eventAdmin, messageService));
      availablePhysicalTypesReg = OteEventMessageUtil.subscribe(AVAILABLE_PHYSICAL_TYPES_REQ.TOPIC, new PhysicalTypesResponceListener(eventAdmin, messageService));
      subscribeToMessageReg = OteEventMessageUtil.subscribe(SerializedSubscribeToMessage.EVENT, new SubscriptionCommandListener(eventAdmin, messageService));
      unsubscribeToMessageReg = OteEventMessageUtil.subscribe(SerializedUnSubscribeMessage.EVENT, new UnSubscribeCommandListener(eventAdmin, messageService));
      recordReg = OteEventMessageUtil.subscribe(SerializedRecordCommandMessage.EVENT, new RecordCommandListener(eventAdmin, messageService));
      setElementReg = OteEventMessageUtil.subscribe(SerializedSetElementMessage.EVENT, new SetElementCommandListener(eventAdmin, messageService));
      zeroizeElementReg = OteEventMessageUtil.subscribe(SerializedZeroizeElementMessage.EVENT, new ZeroizeElementCommandListener(eventAdmin, messageService));
      stopRecordReg = OteEventMessageUtil.subscribe(STOP_RECORDING_CMD.TOPIC, new StopRecordCommandListener(eventAdmin, messageService));
   }
   
   /**
    * osgi 
    */
   public void stop(){
      addressResponseReg.unregister();
      availablePhysicalTypesReg.unregister();
      subscribeToMessageReg.unregister();
      unsubscribeToMessageReg.unregister();
      recordReg.unregister();
      setElementReg.unregister();
      zeroizeElementReg.unregister();
      stopRecordReg.unregister();
   }
  
}

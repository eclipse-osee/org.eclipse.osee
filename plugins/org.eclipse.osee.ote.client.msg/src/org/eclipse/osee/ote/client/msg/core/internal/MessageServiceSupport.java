package org.eclipse.osee.ote.client.msg.core.internal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;

import org.eclipse.osee.ote.OTEException;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.commands.SetElementValue;
import org.eclipse.osee.ote.message.commands.SubscribeToMessage;
import org.eclipse.osee.ote.message.commands.UnSubscribeToMessage;
import org.eclipse.osee.ote.message.commands.ZeroizeElement;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.event.send.OteSendEventMessage;
import org.eclipse.osee.ote.message.tool.SubscriptionDetails;
import org.eclipse.osee.ote.remote.messages.AVAILABLE_PHYSICAL_TYPES_REQ;
import org.eclipse.osee.ote.remote.messages.GET_INET_ADDRESS_REQ;
import org.eclipse.osee.ote.remote.messages.GET_INET_ADDRESS_RESP;
import org.eclipse.osee.ote.remote.messages.SOCKET_ID;
import org.eclipse.osee.ote.remote.messages.STOP_RECORDING_CMD;
import org.eclipse.osee.ote.remote.messages.SerializedAvailablePhysicalTypesMessage;
import org.eclipse.osee.ote.remote.messages.SerializedRecordCommandMessage;
import org.eclipse.osee.ote.remote.messages.SerializedSetElementMessage;
import org.eclipse.osee.ote.remote.messages.SerializedSubscribeToMessage;
import org.eclipse.osee.ote.remote.messages.SerializedSubscriptionDetailsMessage;
import org.eclipse.osee.ote.remote.messages.SerializedUnSubscribeMessage;
import org.eclipse.osee.ote.remote.messages.SerializedZeroizeElementMessage;
import org.osgi.service.event.EventAdmin;

public class MessageServiceSupport {

   private static EventAdmin admin;
   private static OteSendEventMessage send;
   
   private static EventAdmin getEventAdmin(){
      if(admin == null){
         admin = ServiceUtility.getService(EventAdmin.class);
      }
      return admin;
   }
   
   private static OteSendEventMessage get(){
      if(send == null){
         send = new OteSendEventMessage(getEventAdmin());
      }
      return send;
   }
   

   public static SubscriptionDetails subscribeToMessage(SubscribeToMessage subscribeToMessage) {
      SerializedSubscriptionDetailsMessage resp = new SerializedSubscriptionDetailsMessage();
      try{
         SerializedSubscribeToMessage cmd = new SerializedSubscribeToMessage(subscribeToMessage);
         resp = get().synchSendAndResponse(resp, cmd, 10000);
         if(resp == null){
            throw new OTEException("Timed out waiting for message response");
         } 
         SubscriptionDetails details = resp.getObject();
         return details;
      } catch (IOException ex){
         throw new OTEException("Serialization Error", ex);
      } catch (ClassNotFoundException e) {
         throw new OTEException("Serialization Error", e);
      }
   }

   public static void unsubscribeToMessage(UnSubscribeToMessage unSubscribeToMessage) {
      SerializedUnSubscribeMessage cmd;
      try {
         cmd = new SerializedUnSubscribeMessage(unSubscribeToMessage);
         OteEventMessageUtil.postEvent(cmd);
      } catch (IOException e) {
         throw new OTEException("Serialization Error", e);
      }
   }

   public static Set<? extends DataType> getAvailablePhysicalTypes() throws OTEException {
      AVAILABLE_PHYSICAL_TYPES_REQ req = new AVAILABLE_PHYSICAL_TYPES_REQ();
      try{
         SerializedAvailablePhysicalTypesMessage types = new SerializedAvailablePhysicalTypesMessage();
         types = get().synchSendAndResponse(types, req, 10000);
         if(types == null){
            throw new OTEException("Timed out waiting for message response");
         } 
         return types.getObject();
      } catch (IOException ex){
         throw new OTEException("Serialization Error", ex);
      } catch (ClassNotFoundException e) {
         throw new OTEException("Serialization Error", e);
      }
   }

   public static InetSocketAddress getMsgUpdateSocketAddress() throws OTEException {
      return getCommonAddress(SOCKET_ID.MSG_UPDATES);
   }

   public static InetSocketAddress getRecorderSocketAddress() throws OTEException {
      return getCommonAddress(SOCKET_ID.RECORDER);
   }

   private static InetSocketAddress getCommonAddress(SOCKET_ID id){
      GET_INET_ADDRESS_REQ req = new GET_INET_ADDRESS_REQ();
      req.SOCKET_ID.setValue(id);
      GET_INET_ADDRESS_RESP resp = get().synchSendAndResponse(GET_INET_ADDRESS_RESP.class, GET_INET_ADDRESS_RESP.TOPIC, req, 10000);//todo retry?
      InetSocketAddress address;
      if(resp == null){
         throw new OTEException("Timed out waiting for message response");
      } else {
         try {
            address = new InetSocketAddress(resp.ADDRESS.getAddress(), resp.ADDRESS.getPort());
         } catch (UnknownHostException e) {
            throw new OTEException(e);
         } 
      }
      return address;
   }
   
   public static void startRecording(RecordCommand cmd) {
      try {
         SerializedRecordCommandMessage msg = new SerializedRecordCommandMessage(cmd);
         OteEventMessageUtil.postEvent(msg);
      } catch (IOException e) {
         throw new OTEException("Error starting recording", e);
      }
   }

   public static void stopRecording() {
      STOP_RECORDING_CMD cmd = new STOP_RECORDING_CMD();
      OteEventMessageUtil.postEvent(cmd);
   }

   public static void setElementValue(SetElementValue cmd) {
      try {
         SerializedSetElementMessage msg = new SerializedSetElementMessage(cmd);
         OteEventMessageUtil.postEvent(msg);
      } catch (IOException e) {
         throw new OTEException(e);
      }
   }

   public static void zeroizeElement(ZeroizeElement cmd) {
      try {
         SerializedZeroizeElementMessage msg = new SerializedZeroizeElementMessage(cmd);
         OteEventMessageUtil.postEvent(msg);
      } catch (IOException e) {
         throw new OTEException(e);
      }
   }

}

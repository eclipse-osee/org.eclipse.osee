package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestCommandResult;
import org.eclipse.osee.ote.core.framework.command.RunTests;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.BooleanResponse;
import org.eclipse.osee.ote.remote.messages.RunTestsCancel;
import org.eclipse.osee.ote.remote.messages.RunTestsGetCommandResultReq;
import org.eclipse.osee.ote.remote.messages.RunTestsGetCommandResultResp;
import org.eclipse.osee.ote.remote.messages.RunTestsIsCancelled;
import org.eclipse.osee.ote.remote.messages.RunTestsIsDone;
import org.eclipse.osee.ote.remote.messages.RunTestsSerialized;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class RunTestListener implements EventHandler {

   private OTEApi oteApi;
   
   private ConcurrentHashMap<String, ICommandHandle> handles;
   
   public RunTestListener(EventAdmin eventAdmin, OteUdpEndpoint oteEndpoint, OTEApi oteApi) {
      this.oteApi = oteApi;
      handles = new ConcurrentHashMap<String, ICommandHandle>();
   }

   @Override
   public void handleEvent(Event event) {
      if(oteApi.getTestEnvironment() == null){
         return;
      }
      OteEventMessage generic = OteEventMessageUtil.getOteEventMessage(event);
      if(RunTestsSerialized.TOPIC.equals(generic.getHeader().TOPIC.getValue())){
         RunTestsSerialized serialized = new RunTestsSerialized(OteEventMessageUtil.getBytes(event));
         try {
            RunTests runTests = serialized.getObject();
            String guid = runTests.getGUID();
            ICommandHandle addCommand = oteApi.getTestEnvironment().addCommand(runTests);
            handles.put(guid, addCommand);
         } catch (ExportException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         } catch (ClassNotFoundException e) {
            e.printStackTrace();
         }
      } else if (RunTestsCancel.TOPIC.equals(generic.getHeader().TOPIC.getValue())){
         BooleanResponse booleanResponse = new BooleanResponse();
         RunTestsCancel cancel = new RunTestsCancel(OteEventMessageUtil.getBytes(event));
         ICommandHandle iCommandHandle = handles.get(cancel.GUID.getValue());
         boolean status = false;
         if(iCommandHandle != null){
            if(cancel.CANCEL_ALL.getValue()){
               status = iCommandHandle.cancelAll(true);
            } else {
               status = iCommandHandle.cancelSingle(true);
            }
         }      
         booleanResponse.VALUE.setValue(status);
         booleanResponse.setResponse(cancel);
         OteEventMessageUtil.postEvent(booleanResponse);
      } else if (RunTestsGetCommandResultReq.TOPIC.equals(generic.getHeader().TOPIC.getValue())){
         RunTestsGetCommandResultReq req = new RunTestsGetCommandResultReq(OteEventMessageUtil.getBytes(event));
         ICommandHandle iCommandHandle = handles.get(req.GUID.getValue());
         RunTestsGetCommandResultResp resp = new RunTestsGetCommandResultResp();
         if(iCommandHandle != null){
            ITestCommandResult iTestCommandResult = iCommandHandle.get();
            try {
               resp.setObject(iTestCommandResult);
            } catch (IOException e) {
               e.printStackTrace();
            }
         }   
         resp.setResponse(req);
         OteEventMessageUtil.postEvent(resp);
      } else if (RunTestsIsCancelled.TOPIC.equals(generic.getHeader().TOPIC.getValue())){
         BooleanResponse booleanResponse = new BooleanResponse();
         RunTestsIsCancelled isCanceled = new RunTestsIsCancelled(OteEventMessageUtil.getBytes(event));
         ICommandHandle iCommandHandle = handles.get(isCanceled.GUID.getValue());
         boolean status = false;
         if(iCommandHandle != null){
            status = iCommandHandle.isCancelled();
         }      
         booleanResponse.VALUE.setValue(status);
         booleanResponse.setResponse(isCanceled);
         OteEventMessageUtil.postEvent(booleanResponse);
      } else if (RunTestsIsDone.TOPIC.equals(generic.getHeader().TOPIC.getValue())){
         BooleanResponse booleanResponse = new BooleanResponse();
         RunTestsIsDone isDone = new RunTestsIsDone(OteEventMessageUtil.getBytes(event));
         ICommandHandle iCommandHandle = handles.get(isDone.GUID.getValue());
         boolean status = false;
         if(iCommandHandle != null){
            status = iCommandHandle.isDone();
         }    
         booleanResponse.VALUE.setValue(status);
         booleanResponse.setResponse(isDone);
         OteEventMessageUtil.postEvent(booleanResponse);
      }
   }

}

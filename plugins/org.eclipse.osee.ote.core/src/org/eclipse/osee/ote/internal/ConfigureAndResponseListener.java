package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ConfigurationStatus;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.OTEStatusCallback;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.endpoint.OteUdpEndpointSender;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.ConfigurationAndResponse;
import org.eclipse.osee.ote.remote.messages.JobStatus;
import org.eclipse.osee.ote.remote.messages.SerializedConfigurationAndResponse;
import org.eclipse.osee.ote.remote.messages.SerializedOTEJobStatus;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class ConfigureAndResponseListener implements EventHandler {

   private final OTEApi oteApi;
   private final OteUdpEndpoint endpoint;

   public ConfigureAndResponseListener(EventAdmin eventAdmin, OteUdpEndpoint endpoint, OTEApi oteApi) {
      this.oteApi = oteApi;
      this.endpoint = endpoint;
   }

   @Override
   public void handleEvent(Event arg0) {
      SerializedConfigurationAndResponse serialized = new SerializedConfigurationAndResponse(OteEventMessageUtil.getBytes(arg0));
      try {
         ConfigurationAndResponse config = serialized.getObject();
         
         OteUdpEndpointSender oteEndpointSender = endpoint.getOteEndpointSender(serialized.getHeader().getSourceInetSocketAddress());
         endpoint.addBroadcast(oteEndpointSender);
         try{
            String id = (String) oteApi.getIHostTestEnvironment().getProperties().getProperty("id");
            /*
             * Ensure that the id matches so that we are talking with the server we intended to talk to 
             */            
            if(id != null && config.getId().equals(id)){
               if(config.install()){
                  oteApi.loadConfiguration(config.getConfiguration(), new Status(oteEndpointSender));
               } else {
                  oteApi.downloadConfigurationJars(config.getConfiguration(), new Status(oteEndpointSender));
               }
            } else {
               handleException(new Exception("Bad server id."), oteEndpointSender);
            }
         } catch (ExecutionException e) {
               handleException(e, oteEndpointSender);
         }
      }catch (Throwable e) {
         handleException(e);
      }
   }

   private void handleException(Throwable e, OteUdpEndpointSender oteEndpointSender) {
      SerializedOTEJobStatus message = new SerializedOTEJobStatus();
      JobStatus jobStatus = new JobStatus();
      StringBuilder sb = new StringBuilder();
      sb.append("error: ").append(message).append("\n").append(Lib.exceptionToString(e)).append("\n");
      jobStatus.setErrorLog(sb.toString());
      jobStatus.setSuccess(false);
      jobStatus.setJobComplete(true);
      try {
         message.setObject(jobStatus);
         oteEndpointSender.send(message);
      } catch (IOException e1) {
         e1.printStackTrace();
      }
   }

   private void handleException(Throwable e) {
      OseeLog.log(getClass(), Level.SEVERE, e);
   }

   private static class Status implements OTEStatusCallback<ConfigurationStatus> {
      
      private OteUdpEndpointSender oteEndpointSender;
      private SerializedOTEJobStatus message;
      private JobStatus jobStatus;
      private StringBuilder sb;

      public Status(OteUdpEndpointSender oteEndpointSender) {
         this.oteEndpointSender = oteEndpointSender;
         this.message = new SerializedOTEJobStatus();
         this.jobStatus = new JobStatus();
         sb = new StringBuilder();
      }

      private void setAndSend(){
         try {
            message.setObject(jobStatus);
            oteEndpointSender.send(message);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
      @Override
      public void complete(ConfigurationStatus done) {
         this.jobStatus.setSuccess(done.isSuccess());
         this.jobStatus.setJobComplete(true);
         this.jobStatus.setErrorLog(done.getMessage());
         setAndSend();
      }

      @Override
      public void setTotalUnitsOfWork(int totalUnitsOfWork) {
         jobStatus.setTotalUnitsOfWork(totalUnitsOfWork);
         setAndSend();
      }

      @Override
      public void incrememtUnitsWorked(int count) {
         jobStatus.setUnitsWorked(jobStatus.getUnitsWorked() + count);
         setAndSend();
      }

      @Override
      public void log(String message) {
         sb.append("log: ").append(message).append("\n");
      }

      @Override
      public void error(String message, Throwable th) {
         sb.append("error: ").append(message).append("\n").append(Lib.exceptionToString(th)).append("\n");
      }

      @Override
      public void error(String message) {
         sb.append("error: ").append(message).append("\n");
      }

   }

}

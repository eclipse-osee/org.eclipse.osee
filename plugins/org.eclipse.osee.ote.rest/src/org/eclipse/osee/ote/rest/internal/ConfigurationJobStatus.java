package org.eclipse.osee.ote.rest.internal;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.ote.OTEConfigurationStatus;
import org.eclipse.osee.ote.OTEFuture;
import org.eclipse.osee.ote.OTEStatusCallback;
import org.eclipse.osee.ote.rest.model.OteJobStatus;

public class ConfigurationJobStatus implements OTEStatusCallback<OTEConfigurationStatus>, OteJob {

   private int totalUnitsOfWork;
   private int count;
   private StringBuilder errorLog;
   private StringBuilder statusLog;
   private OTEFuture<OTEConfigurationStatus> future;
   private String uuid;
   private URL url;
   
   public ConfigurationJobStatus(){
      totalUnitsOfWork = 0;
      errorLog = new StringBuilder();
      statusLog = new StringBuilder();
   }
   
   @Override
   public void complete(OTEConfigurationStatus done) {
   }

   @Override
   public void setTotalUnitsOfWork(int totalUnitsOfWork) {
      this.totalUnitsOfWork = totalUnitsOfWork;
   }

   @Override
   public void incrememtUnitsWorked(int count) {
      this.count+=count;
   }

   @Override
   public void log(String string) {
      statusLog.append(string);
      statusLog.append("\n");
   }

   @Override
   public void error(String message, Throwable th) {
      errorLog.append(message);
      errorLog.append("\n");
      errorLog.append(Lib.exceptionToString(th));
      errorLog.append("\n");
   }
   
   @Override
   public void error(String message) {
      errorLog.append(message);
      errorLog.append("\n");
   }

   public void setFuture(OTEFuture<OTEConfigurationStatus> future) {
      this.future = future;
   }

   @Override
   public OteJobStatus getStatus() throws InterruptedException, ExecutionException {
      OteJobStatus jobStatus = new OteJobStatus();
      if(future.isDone()){
         jobStatus.setJobComplete(true);
         jobStatus.setSuccess(future.get().isSuccess());
         if(!future.get().isSuccess()){
            error(future.get().getMessage());
         }
      } else {
         jobStatus.setJobComplete(false);
         jobStatus.setSuccess(false);
      }
      jobStatus.setJobId(uuid);
      jobStatus.setErrorLog(errorLog.toString());
      jobStatus.setTotalUnitsOfWork(totalUnitsOfWork);
      jobStatus.setUnitsWorked(count);
      jobStatus.setUpdatedJobStatus(url);
      return jobStatus;
   }

   @Override
   public String getId() {
      return uuid;
   }

   @Override
   public void setId(String uuid) {
      this.uuid = uuid;
   }

   public void setUrl(URL url) {
      this.url = url;
   }

}

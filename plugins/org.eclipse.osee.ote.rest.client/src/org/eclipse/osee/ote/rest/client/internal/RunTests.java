package org.eclipse.osee.ote.rest.client.internal;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.ws.rs.core.MediaType;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.rest.client.Progress;
import org.eclipse.osee.ote.rest.client.ProgressWithCancel;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;
import org.eclipse.osee.ote.rest.model.OTETestRun;

import com.sun.jersey.api.client.WebResource;

public class RunTests implements ProgressWithCancel, Callable<ProgressWithCancel> {

   private URI uri;
   private OTETestRun tests;
   private Progress progress;
   private WebResourceFactory factory;
   private OTEJobStatus status;
   private String id;

   public RunTests(URI uri, OTETestRun tests, Progress progress, WebResourceFactory factory) {
      this.uri = uri;
      this.tests = tests;
      this.progress = progress;
      this.factory = factory;
   }

   public void doWork() throws Exception {
      try{
         status = sendCommand();
         id = status.getJobId();
         if(!status.isSuccess()){
            throw new Exception("Failed to submit the run command: " + status.getErrorLog());
         }
      } finally {
         
      }
   }
   
   @Override
   public boolean cancelSingle() {
      WebResource baseService;
      try {
         baseService = factory.createResource(uri);
         OTEJobStatus cancelStatus = baseService.path("ote").path("run").path(id).accept(MediaType.APPLICATION_JSON).put(OTEJobStatus.class);
         return cancelStatus.isSuccess();   
      } catch (Exception e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
         return false;
      }
   }
   
   private OTEJobStatus sendCommand() throws Exception {
      WebResource baseService = factory.createResource(uri);
      return baseService.path("ote").path("run").accept(MediaType.APPLICATION_JSON).post(OTEJobStatus.class, tests);      
   }

   @Override
   public boolean cancelAll() {
      WebResource baseService;
      try {
         baseService = factory.createResource(uri);
         OTEJobStatus cancelStatus = baseService.path("ote").path("run").path(id).accept(MediaType.APPLICATION_JSON).delete(OTEJobStatus.class);
         return cancelStatus.isSuccess();   
      } catch (Exception e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
         return false;
      }
   }
   
   @Override
   final public ProgressWithCancel call() throws Exception {
      try{
         doWork();
         progress.success();
      } catch (Throwable th){
         progress.fail(th);
      }
      return this;
   }

   @Override
   public void fail(String string) {
      progress.fail(string);
   }

   @Override
   public void fail(Throwable th) {
      progress.fail(th);
   }

   @Override
   public void setUnitsOfWork(int totalUnitsOfWork) {
      progress.setUnitsOfWork(totalUnitsOfWork);
   }

   @Override
   public void setUnitsWorked(int unitsWorked) {
      progress.setUnitsWorked(unitsWorked);
   }

   @Override
   public void success() {
      progress.success();
   }

}

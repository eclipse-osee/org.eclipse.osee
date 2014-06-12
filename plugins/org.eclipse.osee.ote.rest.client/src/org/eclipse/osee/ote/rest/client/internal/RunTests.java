package org.eclipse.osee.ote.rest.client.internal;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.ote.rest.client.Progress;
import org.eclipse.osee.ote.rest.client.ProgressWithCancel;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;
import org.eclipse.osee.ote.rest.model.OTETestRun;
public class RunTests implements ProgressWithCancel, Callable<ProgressWithCancel> {

   private final URI uri;
   private final OTETestRun tests;
   private final Progress progress;
   private final JaxRsClient factory;
   private OTEJobStatus status;
   private String id;

   public RunTests(URI uri, OTETestRun tests, Progress progress, JaxRsClient factory) {
      this.uri = uri;
      this.tests = tests;
      this.progress = progress;
      this.factory = factory;
   }

   public void doWork() throws Exception {
      status = sendCommand();
      id = status.getJobId();
      if (!status.isSuccess()) {
         throw new Exception("Failed to submit the run command: " + status.getErrorLog());
      }
   }

   @Override
   public boolean cancelSingle() {
      WebTarget baseService;
      try {
         baseService = factory.target(uri);
         OTEJobStatus cancelStatus =
            baseService.path("ote").path("run").path(id).request(MediaType.APPLICATION_JSON).put(Entity.json(""),
               OTEJobStatus.class);
         return cancelStatus.isSuccess();
      } catch (Exception e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
         return false;
      }
   }

   private OTEJobStatus sendCommand() throws Exception {
      WebTarget baseService = factory.target(uri);
      return baseService.path("ote").path("run").request(MediaType.APPLICATION_JSON).post(Entity.json(tests),
         OTEJobStatus.class);
   }

   @Override
   public boolean cancelAll() {
      WebTarget baseService;
      try {
         baseService = factory.target(uri);
         OTEJobStatus cancelStatus =
            baseService.path("ote").path("run").path(id).request(MediaType.APPLICATION_JSON).delete(OTEJobStatus.class);
         return cancelStatus.isSuccess();
      } catch (Exception e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
         return false;
      }
   }

   @Override
   final public ProgressWithCancel call() throws Exception {
      try {
         doWork();
         progress.success();
      } catch (Throwable th) {
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

package org.eclipse.osee.ote;

import java.io.IOException;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.ote.message.event.send.OteEventMessageCallable;
import org.eclipse.osee.ote.message.event.send.OteEventMessageFuture;
import org.eclipse.osee.ote.remote.messages.JobStatus;
import org.eclipse.osee.ote.remote.messages.SerializedConfigurationAndResponse;
import org.eclipse.osee.ote.remote.messages.SerializedOTEJobStatus;

class WaitForCompletion implements OteEventMessageCallable<SerializedConfigurationAndResponse, SerializedOTEJobStatus> {

   private SubProgressMonitor monitor;
   private int lastUnitsWorked = 0;
   private boolean firstTime = true;
   private JobStatus status;

   WaitForCompletion(SubProgressMonitor monitor) {
      this.monitor = monitor;
   }

   @Override
   public void call(SerializedConfigurationAndResponse transmitted, SerializedOTEJobStatus recieved, OteEventMessageFuture<?, ?> future) {
      
      try {
         this.status = recieved.getObject();
         reportStatus(status, future);
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
   }

   private synchronized void reportStatus(JobStatus status, OteEventMessageFuture<?, ?> future) {
      if(monitor != null){
         if(monitor.isCanceled() || status.isJobComplete()){
            monitor.done();
            future.complete();
            return;
         }
         if(firstTime){
            monitor.beginTask("Configure Test Server", status.getTotalUnitsOfWork());
            firstTime = false;
         } else {
            monitor.worked(status.getUnitsWorked() - lastUnitsWorked);
            lastUnitsWorked = status.getUnitsWorked();
         }
      } else {
         if(status.isJobComplete()){
            future.complete();
            return;
         }
      }
   }

   @Override
   public void timeout(SerializedConfigurationAndResponse transmitted) {
      System.out.println("timed out");
   }
   
   public JobStatus getStatus(){
      return this.status;
   }
   
}

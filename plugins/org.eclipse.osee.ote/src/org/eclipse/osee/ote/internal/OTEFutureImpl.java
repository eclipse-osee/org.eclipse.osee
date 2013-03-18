package org.eclipse.osee.ote.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.osee.ote.OTEConfigurationStatus;
import org.eclipse.osee.ote.OTEFuture;

public class OTEFutureImpl implements OTEFuture<OTEConfigurationStatus> {

   private final Future<OTEConfigurationStatus> submit;
   private OTEConfigurationStatus oteConfigurationStatus;
   
   public OTEFutureImpl(Future<OTEConfigurationStatus> submit) {
      this.submit = submit;
   }

   public OTEFutureImpl(OTEConfigurationStatus oteConfigurationStatus) {
      this.submit = null;
      this.oteConfigurationStatus = oteConfigurationStatus;
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      if(submit == null){
         return false;
      }
      return submit.cancel(mayInterruptIfRunning);
   }

   @Override
   public OTEConfigurationStatus get() throws InterruptedException, ExecutionException {
      if(submit == null){
         return oteConfigurationStatus;
      }
      return submit.get();
   }

   @Override
   public OTEConfigurationStatus get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      if(submit == null){
         return oteConfigurationStatus;
      }
      return submit.get(timeout, unit);
   }

   @Override
   public boolean isCancelled() {
      if(submit == null){
         return false;
      }
      return submit.isCancelled();
   }

   @Override
   public boolean isDone() {
      if(submit == null){
         return true;
      }
      return submit.isDone();
   }

}

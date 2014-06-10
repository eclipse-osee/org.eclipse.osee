package org.eclipse.osee.ote.rest.client.internal;

import java.util.concurrent.Callable;
import org.eclipse.osee.ote.rest.client.Progress;

public abstract class BaseClientCallable<T extends Progress> implements Callable<T>{

   private T progress;
   
   public BaseClientCallable(T progress) {
      this.progress = progress;
   }

   @Override
   final public T call() throws Exception {
      try{
         doWork();
         progress.success();
      } catch (Throwable th){
         progress.fail(th);
      }
      return progress;
   }

   public abstract void doWork() throws Exception;

}

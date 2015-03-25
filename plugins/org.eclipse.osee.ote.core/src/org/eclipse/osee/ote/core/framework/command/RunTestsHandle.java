package org.eclipse.osee.ote.core.framework.command;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.osee.framework.jdk.core.util.GUID;

public class RunTestsHandle implements ICommandHandle, Serializable {

   private static final long serialVersionUID = 3643208660033506154L;
   
   private final transient Future<ITestCommandResult> result;
   private final transient ITestContext context;
   private final transient RunTests command;
   private final String guid;

   public RunTestsHandle(Future<ITestCommandResult> result, ITestContext context, RunTests command) {
      this.result = result;
      this.context = context;
      this.command = command;
      this.guid = GUID.create();
   }

   @Override
   public boolean cancelAll(boolean mayInterruptIfRunning) {
      return command.cancel();
   }

   @Override
   public boolean cancelSingle(boolean mayInterruptIfRunning) {
      return command.cancelSingle();
   }

   @Override
   public ITestCommandResult get() {
      try {
         return result.get();
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (ExecutionException e) {
         e.printStackTrace();
      }
      return null;
   }

   @Override
   public boolean isCancelled() {
      return false;
   }

   @Override
   public boolean isDone() {
      return command.isRunning();
   }

   @Override
   public String getCommandKey() {
      return guid;
   }

 

}

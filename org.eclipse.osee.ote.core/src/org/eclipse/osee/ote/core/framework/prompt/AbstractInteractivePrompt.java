/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.framework.prompt;

import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Ken J. Aguilar
 */
public abstract class AbstractInteractivePrompt<T> extends AbstractRemotePrompt implements IPromptHandle {

   private final TestScript script;
   protected Exception exception;
   protected T response;

   public AbstractInteractivePrompt(IServiceConnector connector, TestScript script, String id, String message) throws UnknownHostException {
      super(connector, id, message);
      this.script = script;
   }

   public T open(Executor executor) throws InterruptedException, Exception {
      response = null;
      exception = null;
      if (executor != null) {
         // run the prompt in the background
         executor.execute(new Runnable() {

            public void run() {
               try {
                  doPrompt();
               } catch (Exception e) {
                  OseeLog.log(TestEnvironment.class,
                        Level.SEVERE, "exception while performing prompt", e);
                  // the thread that activated the prompt will be waiting on the script object's notifyAll() to
                  // be called. If an exception occurs this may not happen so we should do it here
                  endPrompt(null, new Exception("exception while performing prompt", e));
               }
            }
         });
      } else {
         doPrompt();
      }
      return waitForResponse(script, false);
   }

   protected void endPrompt(T response, Exception exception) {

      script.getTestEnvironment().getScriptCtrl().setExecutionUnitPause(false);
      script.getTestEnvironment().getScriptCtrl().setScriptPause(false);
      synchronized (script) {
         this.response = response;
         this.exception = exception;
         script.notifyAll();
      }
   }

   protected abstract void doPrompt() throws Exception;

   protected T waitForResponse(TestScript script, boolean executionUnitPause) throws InterruptedException, Exception {
      synchronized (script) {
         script.getTestEnvironment().getScriptCtrl().setScriptPause(true);
         script.getTestEnvironment().getScriptCtrl().setExecutionUnitPause(executionUnitPause);
         script.getTestEnvironment().getScriptCtrl().unlock();
         try {
            script.wait();
         } finally {
            script.getTestEnvironment().getScriptCtrl().lock();
         }
         if (exception != null) {
            throw exception;
         }
         return response;
      }
   }

   /**
    * @return the script
    */
   public TestScript getScript() {
      return script;
   }

}

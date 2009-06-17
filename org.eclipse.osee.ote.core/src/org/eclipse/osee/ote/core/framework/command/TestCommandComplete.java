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
package org.eclipse.osee.ote.core.framework.command;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestCommandComplete implements Callable<ITestCommandResult> {

   private ICommandHandle handle;
   private TestEnvironment env;
   private Future<ITestCommandResult> future;
   private ITestServerCommand cmd;

   /**
    * @param future
    */
   public TestCommandComplete(TestEnvironment env, ITestServerCommand cmd, Future<ITestCommandResult> future) {
      this.future = future;
      this.cmd = cmd;
      this.env = env;
   }

   /**
    * @param handle
    */
   public TestCommandComplete(TestEnvironment env, ICommandHandle handle) {
      this.env = env;
      this.handle = handle;
   }

   /* (non-Javadoc)
    * @see java.util.concurrent.Callable#call()
    */
   public ITestCommandResult call() throws Exception {
      ITestCommandResult result;
      try {
         result = future.get(30, TimeUnit.SECONDS);
         ICommandHandle handle = cmd.createCommandHandle(future, env);
         env.testEnvironmentCommandComplete(handle);
      } catch (Throwable th) {
         result = new TestCommandResult(TestCommandStatus.FAIL, new Exception("Failed to retrieve command result", th));
      }
      return result;
   }

}

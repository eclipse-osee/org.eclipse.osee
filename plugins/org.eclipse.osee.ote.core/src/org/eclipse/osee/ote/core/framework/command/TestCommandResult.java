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

public class TestCommandResult implements ITestCommandResult {

   private static final long serialVersionUID = -7905997520977718355L;

   public static final ITestCommandResult CANCEL = new TestCommandResult(TestCommandStatus.CANCEL);
   public static final ITestCommandResult SUCCESS = new TestCommandResult(TestCommandStatus.SUCCESS);
   public static final ITestCommandResult FAIL = new TestCommandResult(TestCommandStatus.FAIL);

   private final TestCommandStatus status;
   private Throwable th;

   public TestCommandResult(TestCommandStatus status) {
      this.status = status;
   }

   /**
    * @param fail2
    * @param exception
    */
   public TestCommandResult(TestCommandStatus status, Throwable th) {
      this.status = status;
      this.th = th;
   }

   public TestCommandStatus getStatus() {
      return status;
   }

   public Throwable getThrowable() {
      return th;
   }

}

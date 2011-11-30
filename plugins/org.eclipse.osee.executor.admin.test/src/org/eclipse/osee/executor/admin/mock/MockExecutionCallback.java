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
package org.eclipse.osee.executor.admin.mock;

import org.eclipse.osee.executor.admin.ExecutionCallback;

/**
 * @author Roberto E. Escobar
 */
public class MockExecutionCallback<T> implements ExecutionCallback<T> {

   private boolean wasOnCancelled;
   private boolean wasOnSuccess;
   private boolean wasOnFailure;
   private Throwable throwable;
   private T result;
   private final long waitTime;

   public MockExecutionCallback(long waitTime) {
      this.waitTime = waitTime;
      wasOnCancelled = false;
      wasOnSuccess = false;
      wasOnFailure = false;
      throwable = null;
      result = null;
   }

   private void waitIfNeeded() {
      if (waitTime > 0) {
         try {
            Thread.sleep(waitTime);
         } catch (InterruptedException ex) {
            // Do nothing;
         }
      }
   }

   @Override
   public void onCancelled() {
      waitIfNeeded();
      this.wasOnCancelled = true;
   }

   @Override
   public void onSuccess(T result) {
      waitIfNeeded();
      this.result = result;
      this.wasOnSuccess = true;
   }

   @Override
   public void onFailure(Throwable throwable) {
      waitIfNeeded();
      this.wasOnFailure = true;
      this.throwable = throwable;
   }

   public boolean wasOnCancelled() {
      return wasOnCancelled;
   }

   public boolean wasOnSuccess() {
      return wasOnSuccess;
   }

   public boolean wasOnFailure() {
      return wasOnFailure;
   }

   public Throwable getThrowable() {
      return throwable;
   }

   public T getResult() {
      return result;
   }

}
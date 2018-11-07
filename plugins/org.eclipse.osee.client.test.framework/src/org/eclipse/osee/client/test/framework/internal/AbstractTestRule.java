/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.test.framework.internal;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractTestRule implements MethodRule {

   private static final Object lock = new Object();
   private static volatile boolean isFirstTest = true;

   @Override
   public Statement apply(final Statement base, final FrameworkMethod method, Object target) {
      return new Statement() {
         @Override
         public void evaluate() throws Throwable {
            checkPreconditions(method);
            onTestStarting(method);
            try {
               base.evaluate();
               onTestSuccess(method);
            } catch (Throwable t) {
               onTestFailed(t, method);
               throw t;
            } finally {
               onTestFinished(method);
            }
         }
      };
   }

   private void checkPreconditions(FrameworkMethod method) throws Throwable {
      synchronized (lock) {
         if (isFirstTest) {
            isFirstTest = false;
            onFirstTest(method);
         }
      }
   }

   /**
    * Run one time for an entire test set classes and methods
    */
   public void onFirstTest(FrameworkMethod method) throws Throwable {
      //
   }

   /**
    * Invoked when a test method is about to start
    */
   public void onTestStarting(FrameworkMethod method) throws Throwable {
      //
   }

   /**
    * Invoked when a test method succeeds
    */
   public void onTestSuccess(FrameworkMethod method) {
      //
   }

   /**
    * Invoked when a test method fails
    */
   public void onTestFailed(Throwable e, FrameworkMethod method) {
      //
   }

   /**
    * Invoked when a test method finishes (whether passing or failing)
    */
   public void onTestFinished(FrameworkMethod method) {
      //
   }

}

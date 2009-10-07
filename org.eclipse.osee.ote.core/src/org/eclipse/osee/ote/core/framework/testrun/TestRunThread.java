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
package org.eclipse.osee.ote.core.framework.testrun;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.OseeTestThread;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.MethodResultImpl;
import org.eclipse.osee.ote.core.framework.ResultBuilder;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.internal.Activator;

public class TestRunThread extends OseeTestThread {

   private final TestScript test;
   private final ITestRunListenerProvider listenerProvider;
   private final ITestRunListenerDataProvider dataProvider;
   private final IPropertyStore propertyStore;
   private volatile boolean abort = false;
   private final ReentrantLock lock = new ReentrantLock();
   private final ResultBuilder rb = new ResultBuilder(false);

   /**
    * @param propertyStore
    * @param test2
    * @param env
    * @param listenerProvider
    * @param dataProvider
    */
   public TestRunThread(IPropertyStore propertyStore, TestScript test, TestEnvironment env, ITestRunListenerProvider listenerProvider, ITestRunListenerDataProvider dataProvider) {
      super(test.getClass().getSimpleName(), env);
      this.test = test;
      this.listenerProvider = listenerProvider;
      this.dataProvider = dataProvider;
      this.propertyStore = propertyStore;
   }

   @Override
   protected void run() throws Exception {
      try {
         rb.append(listenerProvider.notifyPreRun(dataProvider.createOnPreRun(propertyStore, test)));
         //todo we need to make it so that the setup and teardown test casees get added to the list.... from the getTestCases() method
         //we also need to make sure script initialization gets added through a prerun listener
         if (rb.isReturnStatusOK()) {
            List<TestCase> testCases = test.getTestCases();
            for (TestCase testCase : testCases) {
               if (abort) {
                  addAbortResult(null);
                  break;
               }
               rb.append(listenerProvider.notifyPreTestCase(dataProvider.createOnPreTestCase(propertyStore, test,
                     testCase)));
               lock.lock();
               try {
                  testCase.baseDoTestCase(getEnvironment());
                  if (Thread.interrupted()) {
                     throw new InterruptedException("Thread probably aborted");
                  }

               } catch (Throwable ex) {
                  if (abort) {
                     addAbortResult(null);
                  } else {
                     abort = true;
                     this.test.setAborted(true);
                     MethodResultImpl methodresult = new MethodResultImpl(ReturnCode.ABORTED);
                     methodresult.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, ex));
                     rb.append(methodresult);
                     OseeLog.log(
                           Activator.class,
                           Level.SEVERE,
                           "Exception running Test Case [" + testCase != null ? testCase.getClass().getName() : "uknown (null test case)" + "]",
                           ex);
                  }
               } finally {
                  lock.unlock();
               }
               rb.append(listenerProvider.notifyPostTestCase(dataProvider.createOnPostTestCase(propertyStore, test,
                     testCase)));
            }
         }
      } finally {
         rb.append(listenerProvider.notifyPostRun(dataProvider.createOnPostRun(propertyStore, test)));
      }
   }

   /**
    * 
    */
   private void addAbortResult(Throwable th) {
      if (rb.isReturnStatusOK()) {
         MethodResultImpl methodresult = new MethodResultImpl(ReturnCode.ABORTED);
         if (th == null) {
            methodresult.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, "USER ABORTED"));
         } else {
            methodresult.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, th));
         }
         rb.append(methodresult);
      }
   }

   public boolean abort() {
      abort = true;
      this.test.setAborted(true);
      if (Thread.currentThread() == this.getThread()) {
         throw new TestException("", Level.SEVERE);
      }
      // 
      if (lock.isLocked()) {
         // test case is in process
         this.interrupt();
      }
      try {
         this.join(60000);

         if (this.isAlive()) {
            OseeLog.reportStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE,
                  "Waited 60s for test to abort but the thread did not die."));
            return false;
         }
      } catch (InterruptedException ex) {
         OseeLog.reportStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE,
               "Failed to wait for abort to complete successfully.", ex));
         return false;
      }
      return true;
   }

   public boolean abort(Throwable th, boolean wait) {
      if (abort) {
         return true;
      }
      abort = true;
      this.test.setAborted(true);
      addAbortResult(th);
      if (Thread.currentThread() == this.getThread()) {
         throw new TestException("", Level.SEVERE);
      }
      // 
      if (lock.isLocked()) {
         // test case is in process
         this.interrupt();
      }
      return true;
   }

   public IMethodResult getResult() {
      return rb.get();
   }

}

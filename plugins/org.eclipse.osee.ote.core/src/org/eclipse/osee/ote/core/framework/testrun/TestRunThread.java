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
import org.eclipse.osee.ote.properties.OtePropertiesCore;

public class TestRunThread extends OseeTestThread {

   private final TestScript test;
   private final ITestRunListenerProvider listenerProvider;
   private final ITestRunListenerDataProvider dataProvider;
   private final IPropertyStore propertyStore;
   private volatile boolean abort = false;
   private final ResultBuilder rb = new ResultBuilder(false);

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
         if (rb.isReturnStatusOK()) {
            List<TestCase> testCases = test.getTestCases();
            for (int i = 0; i < testCases.size(); i++) {
               if (abort) {
                  Thread.interrupted();//clear the interrupted flag so that cleanup can occur without exception
                  addAbortResult(null);
                  i = testCases.size() - 1;//set to the last test case - TearDown
               }
               TestCase testCase = testCases.get(i);
               if (testCase == null) {
                  continue;
               }
               
               rb.append(listenerProvider.notifyPreTestCase(dataProvider.createOnPreTestCase(propertyStore, test,
                  testCase)));
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
                     Throwable clientSideThrowable = ex;
                     if (!ex.getClass().getName().startsWith("java")) {
                        String msg = ex.getClass().getName();
                        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
                           msg += ": "+ex.getMessage();
                        }
                        Throwable removeThisOnceWeGetRidOfSerializationOnAssociatedException = new Throwable(msg);
                        removeThisOnceWeGetRidOfSerializationOnAssociatedException.setStackTrace(ex.getStackTrace());
                        clientSideThrowable = removeThisOnceWeGetRidOfSerializationOnAssociatedException;
                     }
                     methodresult.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, clientSideThrowable));
                     rb.append(methodresult);
                     OseeLog.log(
                        Activator.class,
                        Level.SEVERE,
                        "Exception running Test Case [" + testCase != null ? testCase.getClass().getName() : "unknown (null test case)" + "]",
                        ex);
                  }
               } 
               rb.append(listenerProvider.notifyPostTestCase(dataProvider.createOnPostTestCase(propertyStore, test, testCase)));
            }
         }
      } finally {
         Thread.interrupted();//clear the interrupted flag so that cleanup can occur without exception
         rb.append(listenerProvider.notifyPostRun(dataProvider.createOnPostRun(propertyStore, test)));
         if (getEnvironment().getScriptCtrl().isLocked()) {
            getEnvironment().getScriptCtrl().unlock();
         }
      }
   }

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
      if(OtePropertiesCore.abortMultipleInterrupt.getBooleanValue()){
         int count = 0;
         do{
            this.interrupt();
            try{
               this.join(10);
            } catch (InterruptedException ex){
            }
            count++;
         } while (this.isAlive() && count < 200);
      } else {
         this.interrupt();
         try{
            this.join(1000*60);
         } catch (InterruptedException ex){
         }
      }
      if (this.isAlive()) {
         OseeLog.reportStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE,
               "Waited 60s for test to abort but the thread did not die."));
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
      this.interrupt();
      return true;
   }

   public IMethodResult getResult() {
      return rb.get();
   }

}

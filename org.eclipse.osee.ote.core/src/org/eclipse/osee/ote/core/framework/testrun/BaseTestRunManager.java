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

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.MethodResultImpl;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.internal.Activator;

public class BaseTestRunManager implements ITestRunManager {

   private final ITestFactory testFactory;
   private final ITestRunListenerProviderFactory testRunListenerProviderFactory;

   private TestScript test;
   private TestRunThread testRunThread;
   private ITestRunListenerDataProvider dataProvider;
   private ITestRunListenerProvider listenerProvider;
   private boolean aborted;

   public BaseTestRunManager(ITestFactory testFactory, ITestRunListenerProviderFactory testRunListenerProviderFactory) {
      this.testFactory = testFactory;
      this.testRunListenerProviderFactory = testRunListenerProviderFactory;
   }

   public boolean abort() {
      if (test != null) {
         test.abort();
      }
      aborted = true;
      if (testRunThread != null) {
         return testRunThread.abort();
      }
      return true;
   }

   public IMethodResult run(IPropertyStore propertyStore, TestEnvironment environment) {
      IMethodResult result = new MethodResultImpl(ReturnCode.OK);
      if (aborted) {
         aborted = false;
         MethodResultImpl methodresult = new MethodResultImpl(ReturnCode.ABORTED);
         methodresult.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, "USER ABORTED"));
         result = methodresult;
         return result;
      }
      try {
         testRunThread = new TestRunThread(propertyStore, test, environment, listenerProvider, dataProvider);
         testRunThread.start();
         testRunThread.join();
         result = testRunThread.getResult();
      } catch (Exception ex) {
         MethodResultImpl methodresult = new MethodResultImpl(ReturnCode.ERROR);
         methodresult.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, ex));
         result = methodresult;
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } finally {
         aborted = false;
         testRunThread = null;
      }
      return result;
   }

   public TestScript getTest() {
      return test;
   }

   public IMethodResult dispose() {
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);
      try {
         this.test.disposeTest();
         this.dataProvider = null;
         this.listenerProvider.clear();
         this.listenerProvider = null;
         this.test = null;
      } catch (Exception ex) {
         result = new MethodResultImpl(ReturnCode.ERROR);
         result.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, ex));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return result;
   }

   public IMethodResult initialize(TestEnvironment env, IPropertyStore propertyStore) {
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);
      try {
         aborted = false;
         this.dataProvider = testRunListenerProviderFactory.createListenerDataProvider();
         this.listenerProvider = testRunListenerProviderFactory.createRunListenerProvider();
         this.test = testFactory.createInstance(env, propertyStore);
         this.test.setListenerProvider(listenerProvider);
      } catch (Exception ex) {
         result = new MethodResultImpl(ReturnCode.ERROR);
         result.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, ex));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return result;
   }

   public boolean abort(Throwable th, boolean wait) {
      if (test != null) {
         test.abort();
      }
      aborted = true;
      if (testRunThread != null) {
         return testRunThread.abort(th, wait);
      }
      return true;
   }

   public boolean isAborted() {
      return aborted;
   }
}

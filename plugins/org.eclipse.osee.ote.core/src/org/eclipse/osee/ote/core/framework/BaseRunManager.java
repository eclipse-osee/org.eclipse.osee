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
package org.eclipse.osee.ote.core.framework;

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.testrun.ITestResultCollector;
import org.eclipse.osee.ote.core.framework.testrun.ITestResultCollectorFactory;
import org.eclipse.osee.ote.core.framework.testrun.ITestRunManager;

public class BaseRunManager implements IRunManager {

   private final ITestRunManager testRunManager;
   private final ITestLifecycleListenerProvider lifecycleListenerProvider;
   private final ITestResultCollectorFactory resultCollectorFactory;
   private ITestResultCollector resultCollector;

   public BaseRunManager(ITestRunManager testRunManager, ITestLifecycleListenerProvider lifecycleListenerProvider, ITestResultCollectorFactory resultCollectorFactory) {
      this.lifecycleListenerProvider = lifecycleListenerProvider;
      this.testRunManager = testRunManager;
      this.resultCollectorFactory = resultCollectorFactory;
   }

   @Override
   public boolean addListener(ITestLifecycleListener listener) {
      return lifecycleListenerProvider.addListener(listener);
   }

   @Override
   public void clearAllListeners() {
      lifecycleListenerProvider.clear();
   }

   @Override
   public boolean removeListener(ITestLifecycleListener listener) {
      return lifecycleListenerProvider.removeListener(listener);
   }

   @Override
   public IMethodResult run(TestEnvironment env, IPropertyStore propertyStore) {
      try {
         this.resultCollector = resultCollectorFactory.createCollector();
         this.resultCollector.initialize(propertyStore, env);
         ResultBuilder rb = new ResultBuilder(true);
         rb.append(lifecycleListenerProvider.notifyPreInstantiation(propertyStore, env));
         if (rb.isReturnStatusOK()) {
            rb.append(testRunManager.initialize(env, propertyStore));
            if (rb.isReturnStatusOK()) {
               try {
                  rb.append(lifecycleListenerProvider.notifyPostInstantiation(propertyStore, testRunManager.getTest(),
                     env));
               } catch (Throwable th) {
                  MethodResultImpl result = new MethodResultImpl(ReturnCode.ERROR);
                  result.addStatus(new BaseStatus(this.getClass().getName(), Level.SEVERE, th));
                  rb.append(result);
               }
               if (rb.isReturnStatusOK()) {
                  try {
                     rb.append(testRunManager.run(propertyStore, env));
                  } catch (Throwable th) {
                     MethodResultImpl result = new MethodResultImpl(ReturnCode.ERROR);
                     result.addStatus(new BaseStatus(this.getClass().getName(), Level.SEVERE, th));
                     rb.append(result);
                  }
               }
               rb.append(lifecycleListenerProvider.notifyPreDispose(propertyStore, testRunManager.getTest(), env));
               rb.append(testRunManager.dispose());
               rb.append(lifecycleListenerProvider.notifyPostDispose(propertyStore, env));
            }
         }
         this.resultCollector.dispose(env);
         this.resultCollector = null;
         if (!rb.isReturnStatusOK()) {
            OseeLog.log(BaseRunManager.class, Level.SEVERE, rb.toString());
         }
         return rb.get();
      } catch (Throwable th) {
         th.printStackTrace();
         MethodResultImpl status = new MethodResultImpl(ReturnCode.ERROR);
         status.addStatus(new BaseStatus(BaseRunManager.class.getName(), Level.SEVERE, th));
         return status;
      }
   }

   @Override
   public boolean abort() {
      return testRunManager.abort();
   }

   @Override
   public boolean abort(Throwable th, boolean wait) {
      return testRunManager.abort(th, wait);
   }

   @Override
   public boolean isAborted() {
      return testRunManager.isAborted();
   }

   @Override
   public TestScript getCurrentScript() {
      return testRunManager.getTest();
   }
}

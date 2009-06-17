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
      if(test != null){
         test.abort();
      }
      aborted = true;
      if (testRunThread != null) {
         return testRunThread.abort();
      }
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.testrun.ITestRunManager#run(org.eclipse.osee.ote.core.framework.data.IPropertyStore)
    */
   public IMethodResult run(IPropertyStore propertyStore, TestEnvironment environment)  {
	   IMethodResult result = MethodResultImpl.OK;
	   if( aborted )
	   {
		   aborted = false;
		   MethodResultImpl methodresult = new MethodResultImpl();
		   methodresult.setReturnCode(ReturnCode.ABORTED);
		   methodresult.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, "USER ABORTED"));
		   result = methodresult;
		   return result;
	   }
	   try {
		   testRunThread = new TestRunThread(propertyStore, test, environment, listenerProvider, dataProvider);
		   testRunThread.start();
		   testRunThread.join();
		   result = testRunThread.getResult();
	   } catch (Exception e) {
		   MethodResultImpl methodresult = new MethodResultImpl();
		   methodresult.setReturnCode(ReturnCode.ERROR);
		   methodresult.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, e));
		   result = methodresult;
		   logException(e, "Exception setting up run thread:");
	   } finally {
		   aborted = false;
		   testRunThread = null;
	   }
	   return result;
   }

   public TestScript getTest() {
      return test;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.testrun.ITestRunManager#dispose()
    */
   public IMethodResult dispose() {
	   MethodResultImpl result = MethodResultImpl.OK;
	  try{
	      this.test.disposeTest();
	      this.dataProvider = null;
	      this.listenerProvider.clear();
	      this.listenerProvider = null;
	      this.test = null;
	  } catch (Exception e){
		  result = new MethodResultImpl();
    	  result.setReturnCode(ReturnCode.ERROR);
    	  result.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, e));
    	  logException(e, "Exception in dispose:");
	  }
	  return result;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.testrun.ITestRunManager#initialize(org.eclipse.osee.ote.core.framework.data.IPropertyStore)
    */
   public IMethodResult initialize(TestEnvironment env, IPropertyStore propertyStore) {
	   MethodResultImpl result = MethodResultImpl.OK;
	   try{
		   aborted = false;
		   this.dataProvider = testRunListenerProviderFactory.createListenerDataProvider();
		   this.listenerProvider = testRunListenerProviderFactory.createRunListenerProvider();
		   this.test = testFactory.createInstance(env, propertyStore);
		   this.test.setListenerProvider(listenerProvider);
	   } catch (Exception e){
		   result = new MethodResultImpl();
		   result.setReturnCode(ReturnCode.ERROR);
		   result.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, e));
		   logException(e, "Exception initializing script:");
	   }
	   return result;
   }

   /**
    * @param e
    */
   private void logException(Exception e, String message) {
      OseeLog.log(this.getClass().getName(), 
            "org.eclipse.osee.ote.core", 
            Level.SEVERE, message, e);
   }

   public boolean abort(Throwable th, boolean wait) {
      if(test != null){
         test.abort();
      }
      aborted = true;
      if (testRunThread != null) {
         return testRunThread.abort(th, wait);
      }
      return true;
   }
   
   public boolean isAborted(){
     return aborted; 
   }
}

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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.MethodResultImpl;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.framework.event.IEventData;

public class BaseTestRunListenerProvider implements ITestRunListenerProvider {

	List<ITestRunListener> listeners;

	public BaseTestRunListenerProvider() {
		listeners = new CopyOnWriteArrayList<ITestRunListener>();
	}

	public boolean addTestRunListener(ITestRunListener listener) {
		return listeners.add(listener);
	}

	public boolean removeTestRunListener(ITestRunListener listener) {
		return listeners.remove(listener);
	}

	public IMethodResult notifyPostRun(IEventData eventData) {
		MethodResultImpl result = MethodResultImpl.OK;
		boolean failed = false;
		for (ITestRunListener listener : listeners) {
		   try{
		      result = collectStatus(result, listener.postRun(eventData));
		   } catch (Throwable th){
            failed = true;
            result.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, th));
         }
		}
		if(failed){
         result.setReturnCode(ReturnCode.ERROR);
      }
		return result;
	}

	public IMethodResult notifyPostTestCase(IEventData eventData) {
		MethodResultImpl result = MethodResultImpl.OK;
		boolean failed = false;
		for (ITestRunListener listener : listeners) {
			try{
			   result = collectStatus(result, listener.postTestCase(eventData));
   		 } catch (Throwable th){
             failed = true;
             result.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, th));
          }
		}
		if(failed){
         result.setReturnCode(ReturnCode.ERROR);
      }
		return result;
	}

	public IMethodResult notifyPreRun(IEventData eventData) {
		MethodResultImpl result = MethodResultImpl.OK;
		boolean failed = false;
		for (ITestRunListener listener : listeners) {
		   try{
		      result = collectStatus(result, listener.preRun(eventData));
		   } catch (Throwable th){
		      failed = true;
		      result.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, th));
		   }
		}
		if(failed){
		   result.setReturnCode(ReturnCode.ERROR);
		}
		return result;
	}

	public IMethodResult notifyPreTestCase(IEventData eventData) {
		MethodResultImpl result = MethodResultImpl.OK;
		boolean failed = false;
		for (ITestRunListener listener : listeners) {
		   try{
		      result = collectStatus(result, listener.preTestCase(eventData));
		   } catch (Throwable th){
            failed = true;
            result.addStatus(new BaseStatus(TestEnvironment.class.getName(), Level.SEVERE, th));
         }
		}
		if(failed){
         result.setReturnCode(ReturnCode.ERROR);
      }
		return result;
	}

	private MethodResultImpl collectStatus(MethodResultImpl result,
			IMethodResult listenerResult) {
		if (listenerResult.getReturnCode() != ReturnCode.OK) {
			if (result == MethodResultImpl.OK) {
				result = new MethodResultImpl();
			}
			result.setReturnCode(listenerResult.getReturnCode());
			result.addStatus(listenerResult.getStatus());
		}
		return result;
	}

	/**
	 * Clearing out the listeners.
	 */
   public void clear() {
      listeners.clear();
   }
}

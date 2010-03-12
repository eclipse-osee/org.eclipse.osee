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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.event.IEventData;
import org.eclipse.osee.ote.core.framework.event.IEventDataProvider;

public class BaseTestLifecycleListenerProvider implements ITestLifecycleListenerProvider {

   private final IEventDataProvider eventProvider;
   private final List<ITestLifecycleListener> listeners;

   public BaseTestLifecycleListenerProvider(IEventDataProvider eventProvider) {
      this.eventProvider = eventProvider;
      listeners = new CopyOnWriteArrayList<ITestLifecycleListener>();
   }

   public void clear() {
      listeners.clear();
   }

   public boolean addListener(ITestLifecycleListener listener) {
      return listeners.add(listener);
   }

   public IMethodResult notifyPostDispose(IPropertyStore propertyStore, TestEnvironment env) {
      IEventData eventData = eventProvider.getEventData(propertyStore, null);
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);
      for (ITestLifecycleListener listener : listeners) {
    	  result = collectStatus(result, listener.postDispose(eventData, env));
      }
      return result;
   }

   public IMethodResult notifyPostInstantiation(IPropertyStore propertyStore, TestScript test, TestEnvironment env)  {
      IEventData eventData = eventProvider.getEventData(propertyStore, test);
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);
      for (ITestLifecycleListener listener : listeners) {
    	  result = collectStatus(result, listener.postInstantiation(eventData, env));
      }
      return result;
   }

   public IMethodResult notifyPreDispose(IPropertyStore propertyStore, TestScript test, TestEnvironment env)   {
      IEventData eventData = eventProvider.getEventData(propertyStore, test);
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);
      for (ITestLifecycleListener listener : listeners) {
    	  result = collectStatus(result, listener.preDispose(eventData, env));
      }
      return result;
   }

   public IMethodResult notifyPreInstantiation(IPropertyStore propertyStore, TestEnvironment env)   {
      IEventData eventData = eventProvider.getEventData(propertyStore, null);
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);

      for (ITestLifecycleListener listener : listeners) {
    	  result = collectStatus(result, listener.preInstantiation(eventData, env));
      }
      return result;
   }

   public boolean removeListener(ITestLifecycleListener listener) {
      return listeners.remove(listener);
   }

   private MethodResultImpl collectStatus(MethodResultImpl result,
			IMethodResult listenerResult) {
		if (listenerResult.getReturnCode() != ReturnCode.OK) {
			if (result.getReturnCode() == ReturnCode.OK) {
				result = new MethodResultImpl(ReturnCode.OK);
			}
			result.setReturnCode(listenerResult.getReturnCode());
			result.addStatus(listenerResult.getStatus());
		}
		return result;
	}
}

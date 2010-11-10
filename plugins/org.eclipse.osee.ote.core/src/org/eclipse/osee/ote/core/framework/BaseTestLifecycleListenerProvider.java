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
   private int listenerCountAtPreinit = 0;

   public BaseTestLifecycleListenerProvider(IEventDataProvider eventProvider) {
      this.eventProvider = eventProvider;
      listeners = new CopyOnWriteArrayList<ITestLifecycleListener>();
   }

   @Override
   public void clear() {
      listeners.clear();
   }

   @Override
   public boolean addListener(ITestLifecycleListener listener) {
      return listeners.add(listener);
   }

   @Override
   public IMethodResult notifyPostDispose(IPropertyStore propertyStore, TestEnvironment env) {
      IEventData eventData = eventProvider.getEventData(propertyStore, null);
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);
      for (ITestLifecycleListener listener : listeners) {
         result = collectStatus(result, listener.postDispose(eventData, env));
      }

      int newSize = listeners.size();
      if( this.listenerCountAtPreinit != newSize)
      {
         System.err.printf("+++++++++++++++++++++++++++++++++++++++++There are now %s listeners when there were %s to start with\n", listenerCountAtPreinit, newSize );
         for (ITestLifecycleListener listener : listeners) {
            System.err.println("Listener: " + listener.getClass().getName());
         }
         System.err.println("-----------------------------------------------------------------------------------------------------");
      }
      return result;
   }

   @Override
   public IMethodResult notifyPostInstantiation(IPropertyStore propertyStore, TestScript test, TestEnvironment env) {
      IEventData eventData = eventProvider.getEventData(propertyStore, test);
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);
      for (ITestLifecycleListener listener : listeners) {
         result = collectStatus(result, listener.postInstantiation(eventData, env));
      }
      return result;
   }

   @Override
   public IMethodResult notifyPreDispose(IPropertyStore propertyStore, TestScript test, TestEnvironment env) {
      IEventData eventData = eventProvider.getEventData(propertyStore, test);
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);
      for (ITestLifecycleListener listener : listeners) {
         result = collectStatus(result, listener.preDispose(eventData, env));
      }
      return result;
   }

   @Override
   public IMethodResult notifyPreInstantiation(IPropertyStore propertyStore, TestEnvironment env) {
      IEventData eventData = eventProvider.getEventData(propertyStore, null);
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);

      for (ITestLifecycleListener listener : listeners) {
         result = collectStatus(result, listener.preInstantiation(eventData, env));
      }

      this.listenerCountAtPreinit = listeners.size();
      return result;
   }

   @Override
   public boolean removeListener(ITestLifecycleListener listener) {
      return listeners.remove(listener);
   }

   private MethodResultImpl collectStatus(MethodResultImpl result, IMethodResult listenerResult) {
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

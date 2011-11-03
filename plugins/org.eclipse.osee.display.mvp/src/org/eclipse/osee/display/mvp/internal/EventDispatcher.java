/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.mvp.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.eclipse.osee.display.mvp.event.annotation.EndPoint;
import org.eclipse.osee.display.mvp.event.annotation.RouteTo;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class EventDispatcher implements InvocationHandler {

   private static final String TO_STRING_METHOD_NAME = "toString";
   private static final String SEND_METHOD_EVENT_PREFIX = "send";
   private static final String HANDLER_METHOD_EVENT_PREFIX = "on";

   private final Log logger;
   private final String name;
   private final Subscribers subscribers;

   public EventDispatcher(Log logger, String name, Subscribers subscribers) {
      this.logger = logger;
      this.name = name;
      this.subscribers = subscribers;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Object toReturn = null;
      String methodName = method.getName();
      if (!TO_STRING_METHOD_NAME.equals(methodName)) {
         RouteTo routeToPath = method.getAnnotation(RouteTo.class);
         logger.trace("Event - name[%s] routeTo[%s]", methodName, routeToPath);

         Class<?>[] subscriberTypes = routeToPath.value();
         for (Class<?> subscriberType : subscriberTypes) {
            Object handler = subscribers.findSubscriber(subscriberType);
            dispatch(subscriberType, handler, method, args);
         }
      } else {
         toReturn = toString();
      }
      return toReturn;
   }

   private void dispatch(Class<?> handlerType, Object handler, Method method, Object[] args) {
      if (handler != null) {
         String eventName = method.getName();
         Method handlerMethod = null;
         String eventHandlerMethodName = null;
         try {
            eventHandlerMethodName = getEventMethodName(eventName);
            handlerMethod = getEventHandlerMethod(handler, eventHandlerMethodName);
         } catch (Throwable t) {
            logger.warn("Method [%s] with arguments [%s] was not found for handler [%s] for event [%s]",
               eventHandlerMethodName, args, handler.getClass().getName(), eventName);
         }

         try {
            if (handlerMethod != null) {
               handlerMethod.invoke(handler, args);
            }
         } catch (Throwable throwable) {
            logger.error(throwable, "Invocation Error - event [%s] to handler [%s]", eventName, handlerType.getName());
         }
      } else {
         logger.warn("Handler [%s] not registered", handlerType.getName());
      }
   }

   private String getEventMethodName(String eventName) {
      String toReturn = eventName;
      if (toReturn.startsWith(SEND_METHOD_EVENT_PREFIX)) {
         toReturn = toReturn.substring(SEND_METHOD_EVENT_PREFIX.length());
      }
      return toReturn;
   }

   private Method getEventHandlerMethod(Object handler, String toFind) {
      Method toReturn = null;
      Class<?> handlerClass = handler.getClass();
      Method[] methods = handlerClass.getMethods();
      for (Method method : methods) {
         EndPoint marker = method.getAnnotation(EndPoint.class);
         if (marker != null) {
            String methodName = method.getName();
            if (methodName.startsWith(HANDLER_METHOD_EVENT_PREFIX)) {
               methodName = methodName.substring(HANDLER_METHOD_EVENT_PREFIX.length());
            }
            if (toFind.equalsIgnoreCase(methodName)) {
               toReturn = method;
               break;
            }
         }
      }
      return toReturn;
   }

   @Override
   public String toString() {
      return "EventDispatcher [name=" + name + "]";
   }

}

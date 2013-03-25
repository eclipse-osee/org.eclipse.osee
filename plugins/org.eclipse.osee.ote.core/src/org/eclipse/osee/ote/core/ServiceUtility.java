/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
/**
 * @author Michael P. Masterson
 */
public class ServiceUtility {
   public static Class<ServiceUtility> getClazz(){
      return ServiceUtility.class;
   }

   public static <T> T getService(Class<T> clazz){
      return getService(clazz, true);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static <T> T[] getServices(Class<T> clazz) throws InvalidSyntaxException{
      ServiceReference[] serviceReferences = getContext().getServiceReferences(clazz.getName(), null);
      T[] data = (T[])new Object[serviceReferences.length];
      for(int i = 0; i < serviceReferences.length; i ++){
         data[i] = (T)getContext().getService(serviceReferences[i]);
      }
      return data;
   }

   public static BundleContext getContext(){
	  Bundle bundle = FrameworkUtil.getBundle(getClazz());
	  if(bundle == null){
		  return null;
	  }
      return bundle.getBundleContext();
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static Object getService(String clazz) {
      BundleContext context = getContext();
      if(context == null){
       OseeLog.log(ServiceUtility.class, Level.SEVERE, "Unable to get service " + clazz);
         return null;
      }
      ServiceReference serviceReference = context.getServiceReference(clazz);
      if(serviceReference == null){
       OseeLog.log(ServiceUtility.class, Level.SEVERE, "Unable to get service " + clazz);
         return null;
      }
      Object obj = getContext().getService(serviceReference);
      if(obj == null){
        OseeLog.log(ServiceUtility.class, Level.SEVERE, "Unable to get service " + clazz);
      }
      return obj; 
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static <T> T getService(Class<T> clazz, boolean logit) {
      BundleContext context = getContext();
      if(context == null){
         if(logit){
            OseeLog.log(ServiceUtility.class, Level.SEVERE, "Unable to get service " + clazz.getName());
         }
         return null;
      }
      ServiceReference serviceReference = context.getServiceReference(clazz.getName());
      if(serviceReference == null){
         if(logit){
            OseeLog.log(ServiceUtility.class, Level.SEVERE, "Unable to get service " + clazz.getName());
         }
         return null;
      }
      T obj = (T)getContext().getService(serviceReference);
      if(obj == null && logit){
         OseeLog.log(ServiceUtility.class, Level.SEVERE, "Unable to get service " + clazz.getName());
      }
      return obj;
   }
   
   public static <T> T getService(Class<T> clazz, int waitTimeMs) {
      int count = 1;
      if(waitTimeMs > 50){
         count = waitTimeMs/50;
      }
      
      T obj = getService(clazz, false);
      if(obj == null){
         for(int i = 0; i < count && obj == null; i++){
            try{
               Thread.sleep(50);
            } catch (InterruptedException ex){
            }
            obj = getService(clazz, false);
         }
      }
      return obj;
   }
}


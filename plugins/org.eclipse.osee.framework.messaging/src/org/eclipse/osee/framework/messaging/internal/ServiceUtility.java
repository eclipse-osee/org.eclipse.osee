/*
 * Created on Jul 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

package org.eclipse.osee.framework.messaging.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class ServiceUtility {
	
   public static ConsoleDebugSupport getConsoleDebugSupport(){
	   return getService(ConsoleDebugSupport.class);
   }
	
   private static Class<ServiceUtility> getClazz(){
      return ServiceUtility.class;
   }

   private static <T> T getService(Class<T> clazz){
      BundleContext context = getContext();
      if(context == null){
         return null;
      }
      ServiceReference serviceReference = context.getServiceReference(clazz.getName());
      if(serviceReference == null){
         return null;
      }
      return (T)getContext().getService(serviceReference);
   }

   private static <T> T[] getServices(Class<T> clazz) throws InvalidSyntaxException{
      ServiceReference[] serviceReferences = getContext().getServiceReferences(clazz.getName(), null);
      T[] data = (T[])new Object[serviceReferences.length];
      for(int i = 0; i < serviceReferences.length; i ++){
         data[i] = (T)getContext().getService(serviceReferences[i]);
      }
      return data;
   }

   public static BundleContext getContext(){
      return FrameworkUtil.getBundle(getClazz()).getBundleContext();
   }
}


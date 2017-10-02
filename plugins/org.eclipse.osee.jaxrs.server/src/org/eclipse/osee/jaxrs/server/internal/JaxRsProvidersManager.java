/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsProvidersManager extends JaxRsDynamicServiceManager<Object> {

   @Override
   public void register(JaxRsApplicationRegistry registry, ServiceReference<Object> reference) {
      String componentName = JaxRsUtils.getComponentName(reference);
      Bundle bundle = reference.getBundle();
      Object provider = bundle.getBundleContext().getService(reference);
      if (isJaxRsProvider(provider)) {
         registry.registerProvider(componentName, bundle, provider);
      }
   }

   @Override
   public void deregister(JaxRsApplicationRegistry registry, ServiceReference<Object> reference) {
      String componentName = JaxRsUtils.getComponentName(reference);
      registry.deregisterProvider(componentName);
   }

   private boolean isJaxRsProvider(Object service) {
      return service != null && (hasRegisterableAnnotation(service) || //
         service instanceof Feature || service instanceof DynamicFeature);
   }

   private boolean hasRegisterableAnnotation(Object service) {
      boolean result = hasJaxRsAnnotation(service.getClass());
      if (!result) {
         Class<?>[] interfaces = service.getClass().getInterfaces();
         for (Class<?> type : interfaces) {
            result = result || hasJaxRsAnnotation(type);
         }
      }
      return result;
   }

   private boolean hasJaxRsAnnotation(Class<?> type) {
      return type.isAnnotationPresent(Provider.class);
   }

}

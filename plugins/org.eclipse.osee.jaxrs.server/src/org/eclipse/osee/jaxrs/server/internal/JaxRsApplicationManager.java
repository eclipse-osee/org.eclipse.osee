/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jaxrs.server.internal;

import java.util.Map;
import javax.ws.rs.core.Application;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsApplicationManager extends JaxRsDynamicServiceManager<Application> {

   private JaxRsConfiguration config;

   // for ReviewOsgiXml public void setJaxRsApplicationRegistry(JaxRsApplicationRegistry registry)
   // for ReviewOsgiXml public void addObject(ServiceReference<T> reference)
   // for ReviewOsgiXml public void removeObject(ServiceReference<T> reference)
   // for ReviewOsgiXml public void addApplication(ServiceReference<T> reference)
   // for ReviewOsgiXml public void removeApplication(ServiceReference<T> reference)
   // for ReviewOsgiXml public void unsetJaxRsApplicationRegistry(JaxRsApplicationRegistry registry) {

   @Override
   public void update(Map<String, Object> props) {
      super.update(props);
      config = JaxRsConfiguration.fromProperties(props).build();
      JaxRsApplicationRegistry registry = getRegistry();
      if (registry != null) {
         registry.configure(config);
      }
   }

   @Override
   public void register(JaxRsApplicationRegistry registry, ServiceReference<Application> reference) {
      String componentName = JaxRsUtils.getComponentName(reference);
      Bundle bundle = reference.getBundle();
      Application application = bundle.getBundleContext().getService(reference);
      registry.register(componentName, bundle, application);
   }

   @Override
   public void deregister(JaxRsApplicationRegistry registry, ServiceReference<Application> reference) {
      String componentName = JaxRsUtils.getComponentName(reference);
      registry.deregister(componentName);
   }

}

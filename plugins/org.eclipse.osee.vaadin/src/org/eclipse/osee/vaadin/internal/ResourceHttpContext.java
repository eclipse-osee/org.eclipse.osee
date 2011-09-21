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
package org.eclipse.osee.vaadin.internal;

import java.net.URL;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public class ResourceHttpContext implements HttpContext {

   private static final String VAADIN_BUNDLE = "com.vaadin";
   private static final String HTTP_CONTEXT = "http.context";

   private HttpService httpService;
   private String contextName;
   private Bundle resourceBundle;

   public void setHttpService(HttpService httpService) {
      this.httpService = httpService;
   }

   public void start(BundleContext bundleContext, Map<String, String> properties) throws Exception {
      resourceBundle = getVaadinBundle(bundleContext);
      contextName = properties.get(HTTP_CONTEXT);
      contextName = ApplicationUtils.normalize(contextName);

      httpService.registerResources(contextName, contextName, this);
   }

   private Bundle getVaadinBundle(BundleContext bundleContext) {
      Bundle vaadinBundle = null;
      for (Bundle bundle : bundleContext.getBundles()) {
         if (VAADIN_BUNDLE.equals(bundle.getSymbolicName())) {
            vaadinBundle = bundle;
            break;
         }
      }
      return vaadinBundle;
   }

   public void stop() {
      httpService.unregister(contextName);
      contextName = null;
      httpService = null;
      resourceBundle = null;
   }

   @Override
   public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) {
      // Assume the container has already performed authentication
      return true;
   }

   @Override
   public URL getResource(String name) {
      return resourceBundle.getResource(name);
   }

   @Override
   public String getMimeType(String name) {
      return null;
   }
}

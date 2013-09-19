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
package org.eclipse.osee.rest.admin.internal;

import javax.ws.rs.core.Application;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * @author Roberto E. Escobar
 */

public class RestComponentFactory {

   public ServletContainer createContainer(Log logger, ServiceReference<Application> reference) throws Exception {
      Bundle bundle = reference.getBundle();
      Application application = bundle.getBundleContext().getService(reference);
      RestServiceUtils.checkValid(application);

      ApplicationAdapter adapter = new ApplicationAdapter(application);

      if (hasExtendedWadl(bundle)) {
         adapter.getProperties().put(ResourceConfig.PROPERTY_WADL_GENERATOR_CONFIG,
            new BundleWadlGeneratorConfig(logger, bundle));
      }

      ServletContainer container = new ServletContainer(adapter);
      return container;
   }

   public HttpContext createHttpContext(ServiceReference<Application> reference) {
      Bundle bundle = reference.getBundle();
      return new BundleHttpContext(bundle);
   }

   private boolean hasExtendedWadl(Bundle bundle) {
      return hasEntry(bundle, "REST-INF/application-doc.xml") && hasEntry(bundle, "REST-INF/application-grammars.xml") && hasEntry(
         bundle, "REST-INF/resourcedoc.xml");
   }

   private boolean hasEntry(Bundle bundle, String path) {
      return bundle.getEntry(path) != null;
   }
}

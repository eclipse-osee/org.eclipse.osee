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
package org.eclipse.osee.rest.admin.internal.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.jaxrs.ApplicationInfo;
import org.eclipse.osee.rest.admin.internal.ObjectProvider;
import org.eclipse.osee.rest.admin.internal.RestServiceUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
@Path("application-details")
public class ApplicationsResource {

   private final ObjectProvider<Iterable<Bundle>> provider;

   public ApplicationsResource(ObjectProvider<Iterable<Bundle>> provider) {
      super();
      this.provider = provider;
   }

   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public List<ApplicationInfo> getApplicationsInfo() {
      List<ApplicationInfo> toReturn = new ArrayList<ApplicationInfo>();
      Iterable<Bundle> bundles = provider.get();
      for (Bundle bundle : bundles) {
         Dictionary<String, String> headers = bundle.getHeaders();

         String bundleName = headers.get(Constants.BUNDLE_SYMBOLICNAME);
         String bundleVersion = headers.get(Constants.BUNDLE_VERSION);

         ServiceReference<?>[] references = bundle.getRegisteredServices();
         for (ServiceReference<?> reference : references) {
            String[] object = (String[]) reference.getProperty("objectClass");
            String clazzType = Arrays.deepToString(object);
            if (clazzType.contains(Application.class.getSimpleName())) {
               String componentName = RestServiceUtils.getComponentName(reference);
               String contextName = RestServiceUtils.getContextName(reference);

               ApplicationInfo info = new ApplicationInfo();
               info.setBundleName(bundleName);
               info.setVersion(bundleVersion);
               info.setApplicationName(componentName);
               info.setUri(contextName);
               toReturn.add(info);
            }

         }
      }
      return toReturn;
   }
}

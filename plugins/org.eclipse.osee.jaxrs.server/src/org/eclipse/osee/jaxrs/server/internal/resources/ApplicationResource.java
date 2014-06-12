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
package org.eclipse.osee.jaxrs.server.internal.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.jaxrs.ApplicationInfo;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitable;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitor;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * @author Roberto E. Escobar
 */
@Path("application-details")
public class ApplicationResource {

   private final JaxRsVisitable visitable;

   public ApplicationResource(JaxRsVisitable visitable) {
      super();
      this.visitable = visitable;
   }

   @PermitAll
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public List<ApplicationInfo> getApplicationsInfo(final @Context UriInfo uriInfo) {
      final List<ApplicationInfo> toReturn = new ArrayList<ApplicationInfo>();

      visitable.accept(new JaxRsVisitor() {
         @Override
         public void onApplication(String applicationContext, String componentName, Bundle bundle, Application application) {
            Dictionary<String, String> headers = bundle.getHeaders();

            String bundleName = headers.get(Constants.BUNDLE_SYMBOLICNAME);
            String bundleVersion = headers.get(Constants.BUNDLE_VERSION);

            ApplicationInfo info = new ApplicationInfo();
            info.setBundleName(bundleName);
            info.setVersion(bundleVersion);
            info.setApplicationName(componentName);

            String absolutePath = getServletPath();

            URI build = UriBuilder.fromPath(absolutePath).path(applicationContext).queryParam("_wadl").build();
            String path = build.toASCIIString();
            info.setUri(path);
            toReturn.add(info);
         }

         private String getServletPath() {
            String absolutePath = uriInfo.getAbsolutePath().toASCIIString();
            absolutePath = absolutePath.replaceAll("/jaxrs-admin/application-details", "");
            return absolutePath;
         }

      });
      Collections.sort(toReturn, new Comparator<ApplicationInfo>() {

         @Override
         public int compare(ApplicationInfo o1, ApplicationInfo o2) {
            return o1.getApplicationName().compareTo(o2.getApplicationName());
         }

      });
      return toReturn;
   }
}

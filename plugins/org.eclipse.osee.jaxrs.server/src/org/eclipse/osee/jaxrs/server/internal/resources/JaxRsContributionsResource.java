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

package org.eclipse.osee.jaxrs.server.internal.resources;

import java.net.URI;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import org.eclipse.osee.jaxrs.JaxRsContributionInfo;
import org.eclipse.osee.jaxrs.server.internal.JaxRsResourceManager;
import org.eclipse.osee.jaxrs.server.internal.JaxRsResourceManager.JaxRsResourceVisitor;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitable;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitor;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * @author Roberto E. Escobar
 */
@Path("contributions")
public class JaxRsContributionsResource {

   private final JaxRsVisitable visitable;
   private final JaxRsResourceManager resourceManager;

   public JaxRsContributionsResource(JaxRsVisitable visitable, JaxRsResourceManager resourceManager) {
      super();
      this.visitable = visitable;
      this.resourceManager = resourceManager;
   }

   private String key(String bundleName, String bundleVersion) {
      return String.format("%s:%s", bundleName, bundleVersion);
   }

   private JaxRsContributionInfo getOrCreateInfo(Map<String, JaxRsContributionInfo> map, String bundleName, String bundleVersion) {
      String key = key(bundleName, bundleVersion);
      JaxRsContributionInfo info = map.get(key);
      if (info == null) {
         info = new JaxRsContributionInfo();
         info.setBundleName(bundleName);
         info.setVersion(bundleVersion);
         map.put(key, info);
      }
      return info;
   }

   private String getServletPath(UriInfo uriInfo) {
      String absolutePath = uriInfo.getAbsolutePath().toASCIIString();
      absolutePath = absolutePath.replaceAll("/jaxrs-admin/contributions", "");
      return absolutePath;
   }

   @PermitAll
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Collection<JaxRsContributionInfo> getContributionDetails(final @Context UriInfo uriInfo) {
      final Map<String, JaxRsContributionInfo> contribs = new HashMap<>();
      visitable.accept(new JaxRsVisitor() {
         @Override
         public void onApplication(String applicationContext, String componentName, Bundle bundle, Application application) {
            Dictionary<String, String> headers = bundle.getHeaders();

            String bundleName = headers.get(Constants.BUNDLE_SYMBOLICNAME);
            String bundleVersion = headers.get(Constants.BUNDLE_VERSION);

            JaxRsContributionInfo contrib = getOrCreateInfo(contribs, bundleName, bundleVersion);

            ApplicationInfo info = new ApplicationInfo();
            info.setName(componentName);

            String absolutePath = getServletPath(uriInfo);
            URI build = UriBuilder.fromPath(absolutePath).path(applicationContext).queryParam("_wadl").build();
            String path = build.toASCIIString();
            info.setUri(path);

            contrib.getApplications().add(info);
         }

         @Override
         public void onProvider(String componentName, Bundle bundle, Object provider) {
            Dictionary<String, String> headers = bundle.getHeaders();

            String bundleName = headers.get(Constants.BUNDLE_SYMBOLICNAME);
            String bundleVersion = headers.get(Constants.BUNDLE_VERSION);

            JaxRsContributionInfo contrib = getOrCreateInfo(contribs, bundleName, bundleVersion);
            contrib.getProviders().add(componentName);
         }

      });

      resourceManager.accept(new JaxRsResourceVisitor() {

         @Override
         public void onResource(Bundle bundle, Collection<String> resources) {
            Dictionary<String, String> headers = bundle.getHeaders();
            String bundleName = headers.get(Constants.BUNDLE_SYMBOLICNAME);
            String bundleVersion = headers.get(Constants.BUNDLE_VERSION);

            JaxRsContributionInfo contrib = getOrCreateInfo(contribs, bundleName, bundleVersion);

            String absolutePath = getServletPath(uriInfo);

            Set<String> staticResources = contrib.getStaticResources();
            for (String resource : resources) {
               String uri = UriBuilder.fromPath(absolutePath).path(resource).build().toASCIIString();
               staticResources.add(uri);
            }
         }
      });
      return contribs.values();
   }

}

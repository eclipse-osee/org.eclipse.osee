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
package org.eclipse.osee.template.engine.internal;

import java.net.URI;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.JaxRsTemplateContribution;
import org.eclipse.osee.jaxrs.JaxRsTemplateInfo;
import org.eclipse.osee.jaxrs.JaxRsTemplateLink;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.template.engine.internal.TemplateRegistry.TemplateVisitor;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * @author Roberto E. Escobar
 */
@Path("templates")
public class TemplateContributions {

   private final TemplateRegistry registry;

   public TemplateContributions(TemplateRegistry registry) {
      this.registry = registry;
   }

   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Collection<JaxRsTemplateContribution> getTemplateContributions(@Context final HttpHeaders httpHeaders, @Context final UriInfo uriInfo) {
      final Map<String, JaxRsTemplateContribution> contribs = new HashMap<>();
      registry.accept(new TemplateVisitor() {

         @Override
         public void onTemplate(Bundle bundle, ResourceToken template) {
            Dictionary<String, String> headers = bundle.getHeaders();

            String bundleName = headers.get(Constants.BUNDLE_SYMBOLICNAME);
            String bundleVersion = headers.get(Constants.BUNDLE_VERSION);

            JaxRsTemplateContribution contrib = getOrCreateInfo(contribs, bundleName, bundleVersion);

            JaxRsTemplateLink info = new JaxRsTemplateLink();
            info.setName(template.getName());
            info.setUuid(template.getGuid());

            MediaType mediaType = getMediaType(httpHeaders);
            String uri = getDetailsUri(uriInfo, template.getName(), mediaType);
            info.setDetails(uri);

            contrib.getTemplates().add(info);
         }
      });
      return contribs.values();
   }

   @Path("{template-id}")
   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public JaxRsTemplateInfo getTemplateInfo(@PathParam("template-id") String templateId) {
      String viewId = templateId;
      if (!Strings.isNumeric(templateId)) {
         int index = viewId.lastIndexOf('_');
         if (index > 0) {
            viewId = String.format("%s.%s", viewId.substring(0, index), viewId.substring(index + 1, viewId.length()));
         }
      }
      ResourceToken template = registry.resolveTemplate(viewId, MediaType.WILDCARD_TYPE);
      if (template == null) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "Unable to find template [%s]", templateId);
      }

      JaxRsTemplateInfo info = new JaxRsTemplateInfo();
      info.setName(template.getName());
      info.setUuid(template.getGuid());
      info.getAttributes().addAll(registry.getAttributes(template));
      return info;
   }

   private MediaType getMediaType(HttpHeaders httpHeaders) {
      MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
      List<MediaType> mediaTypes = httpHeaders.getAcceptableMediaTypes();
      if (mediaTypes == null || mediaTypes.isEmpty()) {
         mediaType = MediaType.APPLICATION_XML_TYPE;
      } else {
         mediaType = mediaTypes.iterator().next();
      }
      return mediaType;
   }

   private String getDetailsUri(UriInfo uriInfo, String name, MediaType mediaType) {
      String key = name;
      int index = key.lastIndexOf(".");
      if (index > 0) {
         key = String.format("%s_%s.%s", key.substring(0, index), Lib.getExtension(name), mediaType.getSubtype());
      }
      URI build = uriInfo.getRequestUriBuilder().path(key).build();
      return build.toASCIIString();
   }

   private JaxRsTemplateContribution getOrCreateInfo(Map<String, JaxRsTemplateContribution> map, String bundleName, String bundleVersion) {
      String key = key(bundleName, bundleVersion);
      JaxRsTemplateContribution info = map.get(key);
      if (info == null) {
         info = new JaxRsTemplateContribution();
         info.setBundleName(bundleName);
         info.setVersion(bundleVersion);
         map.put(key, info);
      }
      return info;
   }

   private String key(String bundleName, String bundleVersion) {
      return String.format("%s:%s", bundleName, bundleVersion);
   }

}
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.JaxRsResourceManager;
import org.eclipse.osee.jaxrs.server.internal.JaxRsResourceManager.Resource;

/**
 * @author Roberto E. Escobar
 */
@PreMatching
@Priority(Priorities.USER)
@Provider
public class JaxRsStaticResourceRequestFilter implements ContainerRequestFilter {

   private static final String GET_METHOD = "GET";
   private static final String HEAD_METHOD = "HEAD";

   private JaxRsResourceManager manager;

   @Context
   private ServletContext servletContext;

   public void setJaxRsResourceManager(JaxRsResourceManager manager) {
      this.manager = manager;
   }

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException {
      Request request = requestContext.getRequest();
      String method = request.getMethod();
      if (GET_METHOD.equals(method) || HEAD_METHOD.equals(method)) {
         Resource resource = findResource(requestContext);
         if (resource != null) {
            MultivaluedMap<String, String> headers = requestContext.getHeaders();
            List<MediaType> mediaTypes = requestContext.getAcceptableMediaTypes();
            Response response = newResponse(servletContext, headers, mediaTypes, resource);
            requestContext.abortWith(response);
         }
      }
   }

   private Resource findResource(ContainerRequestContext requestContext) {
      UriInfo uriInfo = requestContext.getUriInfo();
      String path = uriInfo.getAbsolutePath().getPath();

      Resource resource = manager.getResource(path);
      if (resource == null) {
         if (!hasExtension(path)) {
            List<MediaType> mediaTypes = getMediaTypesToSearch(requestContext);
            for (MediaType mediaType : mediaTypes) {
               String resourcePath = addExtension(path, mediaType);
               if (Strings.isValid(resourcePath)) {
                  resource = manager.getResource(resourcePath);
                  if (resource != null) {
                     break;
                  }
               }
            }
         }
      }
      return resource;
   }

   private List<MediaType> getMediaTypesToSearch(ContainerRequestContext requestContext) {
      List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
      MediaType mediaType = requestContext.getMediaType();
      if (mediaType != null) {
         acceptableMediaTypes.add(mediaType);
      }
      acceptableMediaTypes.addAll(requestContext.getAcceptableMediaTypes());
      return acceptableMediaTypes;
   }

   private boolean hasExtension(String path) {
      String extension = null;
      if (Strings.isValid(path)) {
         int index = path.lastIndexOf("/");
         String toProcess = path;
         if (index > 0 && index + 1 < path.length()) {
            toProcess = path.substring(index + 1);
         }
         extension = Lib.getExtension(toProcess);
      }
      return Strings.isValid(extension);
   }

   private String addExtension(String path, MediaType mediaType) {
      String extension = mediaType.getSubtype();
      if ("plain".equals(extension)) {
         extension = "txt";
      } else if (extension.contains("+")) {
         int index = extension.lastIndexOf("+");
         if (index > 0 && index + 1 < extension.length()) {
            extension = extension.substring(index + 1);
         }
      } else if (extension.contains(".")) {
         extension = Lib.getExtension(extension);
      }
      String toReturn = null;
      if (Strings.isValid(extension)) {
         StringBuilder builder = new StringBuilder(path);
         builder.append(".");
         builder.append(extension);
         toReturn = builder.toString();
      }
      return toReturn;
   }

   private Response newResponse(ServletContext servletContext, MultivaluedMap<String, String> headers, List<MediaType> acceptableMediaTypes, Resource resource) throws IOException {
      final URLConnection connection = resource.getUrl().openConnection();

      long lastModified = connection.getLastModified();
      int contentLength = connection.getContentLength();

      String etag = null;
      if (lastModified != -1 && contentLength != -1) {
         etag = String.format("W/\"%s-%s\"", contentLength, lastModified);
      }

      String ifNoneMatch = headers.getFirst(HttpHeaders.IF_NONE_MATCH);
      if (ifNoneMatch != null && etag != null && ifNoneMatch.indexOf(etag) != -1) {
         return Response.notModified().build();
      }

      ResponseBuilder builder = Response.ok();
      if (contentLength != -1) {
         builder.header(HttpHeaders.CONTENT_LENGTH, contentLength);
      }

      String contentType = null;
      if (servletContext != null) {
         String externalForm = resource.getUrl().toExternalForm();
         contentType = servletContext.getMimeType(externalForm);
      }

      if (contentType != null) {
         builder.type(contentType);
      } else {
         if (!acceptableMediaTypes.isEmpty()) {
            builder.type(acceptableMediaTypes.get(0));
         }
      }

      if (lastModified > 0) {
         builder.lastModified(new Date(lastModified));
      }

      if (etag != null) {
         builder.tag(etag);
      }

      StreamingOutput output = new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws IOException, WebApplicationException {
            InputStream inputStream = null;
            try {
               inputStream = connection.getInputStream();
               Lib.inputStreamToOutputStream(inputStream, outputStream);
            } finally {
               Lib.close(inputStream);
            }
         }
      };
      builder.entity(output);
      return builder.build();
   }

}

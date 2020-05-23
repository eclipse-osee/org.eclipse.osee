/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLConnection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class ResourcesEndpointImpl implements ResourcesEndpoint {

   private final IResourceManager resourceManager;

   @Context
   private UriInfo uriInfo;
   @Context
   private HttpHeaders httpHeaders;

   public ResourcesEndpointImpl(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void setUriInfo(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   public UriInfo getUriInfo() {
      return uriInfo;
   }

   public HttpHeaders getHeaders() {
      return httpHeaders;
   }

   public void setHeaders(HttpHeaders httpHeaders) {
      this.httpHeaders = httpHeaders;
   }

   @Override
   public Response getResource(String path, boolean decompressOnAquire, boolean compressOnAcquire) {
      IResourceLocator locator = null;
      String uriLocator = asUriLocator(getHeaders(), path);
      try {
         locator = resourceManager.getResourceLocator(uriLocator);
      } catch (Exception ex) {
         throw new OseeWebApplicationException(ex, Status.BAD_REQUEST, "Unable to locate [%s]", path);
      }

      PropertyStore options = new PropertyStore();
      options.put(StandardOptions.DecompressOnAquire.name(), String.valueOf(decompressOnAquire));
      options.put(StandardOptions.CompressOnAcquire.name(), String.valueOf(compressOnAcquire));

      final IResource resource = resourceManager.acquire(locator, options);
      if (resource == null) {
         throw new OseeWebApplicationException(Status.NOT_FOUND, "Unable to locate [%s]", path);
      } else {
         ResponseBuilder builder = Response.ok(new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
               InputStream inputStream = resource.getContent();
               try {
                  Lib.inputStreamToOutputStream(inputStream, output);
               } finally {
                  Lib.close(inputStream);
               }
            }
         });

         String contentType = getContentType(resource);
         if (Strings.isValid(contentType)) {
            builder.header(HttpHeaders.CONTENT_TYPE, contentType);
         }
         builder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getName());
         return builder.build();
      }
   }

   @Override
   public Response saveResource(InputStream inputStream, String protocol, String resourceId, String resourceName, boolean overwriteAllowed, boolean compressOnSave) {
      boolean isCompressed = false;

      if (!Strings.isValid(resourceName)) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "resource name cannot be null");
      }

      String resource = resourceName;
      String extension = Lib.getExtension(resourceName);
      if (!Strings.isValid(extension)) {
         MediaType mediaType = getAcceptableMediaType(httpHeaders);
         if (mediaType != null) {
            extension = getFileExtension(mediaType);
            if (Strings.isValid(extension)) {
               resource = String.format("%s.%s", resourceName, extension);
            }
         }
      }

      if (Strings.isValid(extension)) {
         isCompressed = extension.contains("zip") || extension.contains("tar");
      } else {
         HttpHeaders httpHeaders = getHeaders();
         String contentType = httpHeaders.getHeaderString(HttpHeaders.CONTENT_TYPE);
         if (Strings.isValid(contentType)) {
            isCompressed = contentType.contains("zip") || contentType.contains("tar");
         }
      }

      IResourceLocator tempLocator = resourceManager.generateResourceLocator(protocol, resourceId, resource);
      IResource tempResource = newResource(tempLocator, isCompressed, new BufferedInputStream(inputStream));

      PropertyStore options = new PropertyStore();
      options.put(StandardOptions.CompressOnSave.name(), compressOnSave);
      options.put(StandardOptions.Overwrite.name(), overwriteAllowed);

      IResourceLocator locator = resourceManager.save(tempLocator, tempResource, options);

      UriInfo uriInfo = getUriInfo();
      URI location = getResourceLocation(uriInfo, locator);
      return Response.created(location).build();
   }

   private URI getResourceLocation(UriInfo uriInfo, IResourceLocator locator) {
      UriBuilder builder = uriInfo.getBaseUriBuilder();
      URI location = builder.path("resources").path(locator.getProtocol()).path(locator.getRawPath()).build();
      return location;
   }

   @Override
   public Response deleteResource(String path) {
      IResourceLocator locator = null;

      String uriLocator = asUriLocator(getHeaders(), path);
      try {
         locator = resourceManager.getResourceLocator(uriLocator);
      } catch (Exception ex) {
         throw new OseeWebApplicationException(ex, Status.BAD_REQUEST, "Unable to locate [%s]", path);
      }

      boolean modified = false;
      int status = resourceManager.delete(locator);
      if (status == IResourceManager.OK) {
         modified = true;
      }
      return OrcsRestUtil.asResponse(modified);
   }

   private String asUriLocator(HttpHeaders headers, String itemPath) {
      StringBuilder builder = new StringBuilder();
      if (Strings.isValid(itemPath)) {
         int firstSlash = itemPath.indexOf('/');
         if (firstSlash > 0 && firstSlash + 1 < itemPath.length()) {
            String protocol = itemPath.substring(0, firstSlash);
            builder.append(protocol);
            builder.append("://");
            String path = itemPath.substring(firstSlash + 1);
            builder.append(path);
         }
      }
      return builder.toString();
   }

   private MediaType getAcceptableMediaType(HttpHeaders headers) {
      MediaType mediaType = headers.getMediaType();
      if (mediaType == null) {
         for (MediaType type : headers.getAcceptableMediaTypes()) {
            String subtype = type.getSubtype();
            if (Strings.isValid(subtype)) {
               mediaType = type;
               break;
            }
         }
      }
      return mediaType;
   }

   private String getFileExtension(MediaType mediaType) {
      String extension;
      if (MediaType.TEXT_PLAIN_TYPE.isCompatible(mediaType)) {
         extension = "txt";
      } else {
         extension = mediaType.getSubtype();
      }
      return extension;
   }

   private String getContentType(IResource resource) {
      String contentType = null;
      InputStream inputStream = null;
      try {
         inputStream = resource.getContent();
         contentType = URLConnection.guessContentTypeFromStream(inputStream);
      } catch (IOException ex) {
         // Do nothing;
      } finally {
         Lib.close(inputStream);
      }
      if (contentType == null) {
         contentType = URLConnection.guessContentTypeFromName(resource.getLocation().toASCIIString());
         if (contentType == null) {
            contentType = "application/*";
         }
      }
      return contentType;
   }

   private static IResource newResource(final IResourceLocator locator, final boolean isCompressed, final InputStream stream) {
      return new IResource() {

         @Override
         public InputStream getContent() {
            return stream;
         }

         @Override
         public URI getLocation() {
            return locator.getLocation();
         }

         @Override
         public String getName() {
            String path = locator.getLocation().toASCIIString();
            int index = path.lastIndexOf("/");
            if (index != -1 && index + 1 < path.length()) {
               path = path.substring(index + 1, path.length());
            }
            return path;
         }

         @Override
         public boolean isCompressed() {
            return isCompressed;
         }
      };
   }

}

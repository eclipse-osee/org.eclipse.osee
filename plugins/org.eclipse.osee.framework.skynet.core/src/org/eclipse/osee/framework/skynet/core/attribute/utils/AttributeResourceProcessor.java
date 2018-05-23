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
package org.eclipse.osee.framework.skynet.core.attribute.utils;

import java.io.InputStream;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DataStore;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class AttributeResourceProcessor {

   private final Attribute<?> attribute;

   public AttributeResourceProcessor(Attribute<?> attribute) {
      this.attribute = attribute;
   }

   public String createStorageName() {
      return BinaryContentUtils.getStorageName(attribute);
   }

   private ResourcesEndpoint getResourcesEndpoint() {
      OseeClient client = ServiceUtil.getOseeClient();
      return client.getResourcesEndpoint();
   }

   public void saveResource(GammaId gammaId, String name, DataStore dataStore) {
      ResourcesEndpoint endpoint = getResourcesEndpoint();

      InputStream inputStream = null;
      try {
         inputStream = dataStore.getInputStream();

         String resourceId = gammaId.getIdString();

         boolean overwriteAllowed = false;
         boolean compressOnSave = false;

         StringBuilder builder = new StringBuilder();
         builder.append(name);

         String extension = dataStore.getExtension();
         if (Strings.isValid(extension)) {
            builder.append(".");
            builder.append(extension);
         }
         String resourceName = builder.toString();

         Response response = endpoint.saveResource(inputStream, BinaryContentUtils.ATTRIBUTE_RESOURCE_PROTOCOL,
            resourceId, resourceName, overwriteAllowed, compressOnSave);
         String location = BinaryContentUtils.getAttributeLocation(response);
         dataStore.setLocator(location);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      } finally {
         Lib.close(inputStream);
      }
   }

   public void acquire(DataStore dataStore) {
      ResourcesEndpoint endpoint = getResourcesEndpoint();
      String path = BinaryContentUtils.asResourcePath(dataStore.getLocator());
      try {
         Response response = endpoint.getResource(path, false, false);
         if (Status.OK.getStatusCode() == response.getStatus()) {
            InputStream inputStream = null;
            try {
               inputStream = response.readEntity(InputStream.class);
               byte[] data = Lib.inputStreamToBytes(inputStream);

               String extension = "";
               String contentType = "text/plain";
               String encoding = "UTF-8";
               MediaType mediaType = response.getMediaType();
               if (mediaType != null) {
                  contentType = mediaType.getType();
                  if (contentType.contains("zip")) {
                     encoding = "ISO-8859-1";
                     extension = "zip";
                  }
               }

               if (!Strings.isValid(extension)) {
                  String contentDisposition = response.getHeaderString(HttpHeaders.CONTENT_DISPOSITION);
                  if (Strings.isValid(contentDisposition)) {
                     int index = contentDisposition.indexOf("filename=");
                     int parseAt = index + "filename=".length();
                     if (index > 0 && contentDisposition.length() > parseAt) {
                        String resourceName = contentDisposition.substring(parseAt);
                        extension = Lib.getExtension(resourceName);
                     }
                  }
               }
               dataStore.setContent(data, extension, contentType, encoding);
            } finally {
               Lib.close(inputStream);
            }
         }
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   public void purge(DataStore dataStore) {
      ResourcesEndpoint endpoint = getResourcesEndpoint();
      String path = BinaryContentUtils.asResourcePath(dataStore.getLocator());
      try {
         Response response = endpoint.deleteResource(path);
         if (Status.OK.getStatusCode() == response.getStatus()) {
            dataStore.clear();
         }
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

}
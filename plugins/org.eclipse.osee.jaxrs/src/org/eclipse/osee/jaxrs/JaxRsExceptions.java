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

package org.eclipse.osee.jaxrs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsExceptions {

   private JaxRsExceptions() {
      // Utility class
   }

   public static boolean isErrorResponse(Response response) {
      String header = response.getHeaderString(JaxRsConstants.OSEE_ERROR_REPONSE_HEADER);
      return Boolean.valueOf(header);
   }

   public static OseeCoreException asOseeException(Response response) {
      String message = "";
      String httpStatus = "";
      if (response == null) {
         message = "Error mapping response exception - response was null";
      } else {
         if (response.hasEntity()) {
            if (response.getStatus() == Response.Status.NOT_MODIFIED.getStatusCode()) {
               return null;
            }
            Object entity = response.getEntity();
            try {
               if (isErrorResponse(response)) {
                  message = readEntity(response, String.class);
               } else if (entity instanceof InputStream) {
                  MediaType mediaType = response.getMediaType();
                  message = readStream((InputStream) entity, mediaType);
               } else {
                  message = String.format("%s [%s]", getResponseString(response), entity);
               }
            } catch (Throwable th) {
               message = String.format("Error processing reponse error - %s [%s]", getResponseString(response),
                  th.getLocalizedMessage());
            }
         } else {
            message = getResponseString(response);
         }
         message = message + ".  HTTP Reason: " + response.getStatusInfo().getReasonPhrase();
         httpStatus = response.getStatusInfo().getReasonPhrase();
      }
      return new OseeCoreException(message + ". HTTP Status: " + httpStatus, (Throwable) null);
   }

   private static String getResponseString(Response response) {
      StringBuilder builder = new StringBuilder();
      builder.append("status[");
      int statusCode = response.getStatus();
      builder.append(statusCode);
      builder.append("]");

      Status status = Status.fromStatusCode(statusCode);
      if (status != null) {
         builder.append(" reason[");
         builder.append(status.getReasonPhrase());
         builder.append("]");
      }
      return builder.toString();
   }

   private static <T> T readEntity(Response response, Class<T> clazz) {
      return ((ResponseImpl) response).doReadEntity(clazz, clazz, clazz.getAnnotations());
   }

   private static String readStream(InputStream inputStream, MediaType mediaType) throws IOException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      Lib.inputStreamToOutputStream(inputStream, outputStream);
      return outputStream.toString("UTF-8");
   }
}
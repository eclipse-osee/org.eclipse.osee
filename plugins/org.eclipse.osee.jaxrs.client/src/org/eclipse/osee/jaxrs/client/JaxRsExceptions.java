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
package org.eclipse.osee.jaxrs.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.JaxRsConstants;

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

   public static OseeCoreException asOseeException(Throwable th) {
      OseeCoreException toReturn;
      if (th instanceof ResponseProcessingException) {
         toReturn = asOseeException((ResponseProcessingException) th);
      } else if (th instanceof WebApplicationException) {
         toReturn = asOseeException((WebApplicationException) th);
      } else if (th instanceof OseeCoreException) {
         toReturn = (OseeCoreException) th;
      } else {
         toReturn = new OseeCoreException(th.getCause(), th.getLocalizedMessage());
      }
      return toReturn;
   }

   private static OseeCoreException asOseeException(ResponseProcessingException ex) {
      Response response = ex.getResponse();
      OseeCoreException toReturn;
      if (response.hasEntity()) {
         toReturn = asOseeException(response);
      } else {
         Throwable cause = ex.getCause();
         String message = buildExceptionMessage(response.getStatus(), cause, ex.getMessage());
         toReturn = new OseeCoreException(cause, message);
      }
      return toReturn;
   }

   private static OseeCoreException asOseeException(WebApplicationException ex) {
      Response response = ex.getResponse();
      OseeCoreException toReturn;
      if (response.hasEntity()) {
         toReturn = asOseeException(response);
      } else {
         Throwable cause = ex.getCause();
         String message = buildExceptionMessage(response.getStatus(), cause, ex.getMessage());
         toReturn = new OseeCoreException(cause, message);
      }
      return toReturn;
   }

   private static String buildExceptionMessage(int statusCode, Throwable cause, String exMessage) {
      StringBuilder sb = new StringBuilder();
      sb.append("JAX-RS Client Exception caught - ").append(statusCode);
      String message = cause == null ? exMessage : cause.getMessage();
      if (message == null && cause != null) {
         message = "exception cause class: " + cause.getClass().getName();
      }
      if (message != null) {
         sb.append(", message: ").append(message);
      }
      return sb.toString();
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
      return new OseeCoreException(message + ". HTTP Status: " + httpStatus);
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
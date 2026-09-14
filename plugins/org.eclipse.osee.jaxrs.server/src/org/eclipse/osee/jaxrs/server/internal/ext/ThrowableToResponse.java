/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.jaxrs.server.internal.ext;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.newStatusType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.jaxrs.JaxRsConstants;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ExceptionRegistryOperations;

/**
 * Class provides a common static method for converting server side exceptions into {@link Response} objects for
 * {@link ExceptionMapper} implementations.
 *
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

class ThrowableToResponse {

   /**
    * Creates a {@link Response} object for the provided {@link Throwable}. The Exception Registry service will be
    * checked for an log exclusion entry before the {@link Throwable} is sent to the {@link Log} service. Excluded
    * exceptions will not be logged.
    *
    * @param throwable the server side exception to create a {@link Response} object for.
    * @param uriInfo the URL of the server REST API call that resulted in the exception.
    * @param exceptionRegistryOperations a handle to the {@link ExceptionRegistryOperations} service.
    * @param logger a handle to the {@link Log} service.
    * @return the {@link Response} object for the specified exception.
    */

   static Response toResponse(Throwable throwable, UriInfo uriInfo,
      ExceptionRegistryOperations exceptionRegistryOperations, Log logger) {

      StatusType status;

      if (throwable instanceof OseeWebApplicationException) {
         OseeWebApplicationException webAppException = (OseeWebApplicationException) throwable;
         status = webAppException.getErrorStatus();
      } else if (throwable instanceof WebApplicationException) {
         WebApplicationException webAppException = (WebApplicationException) throwable;
         Response response = webAppException.getResponse();
         int statusCode = response.getStatus();

         status = Status.fromStatusCode(statusCode);
         if (status == null) {
            status = newStatusType(statusCode, Family.SERVER_ERROR, "unknown status code");
         }
      } else {
         status = Status.INTERNAL_SERVER_ERROR;
      }

      String url = uriInfo.getRequestUri().toASCIIString();
      String message = url + "\n" + Lib.exceptionToString(throwable);

      if (exceptionRegistryOperations.okToLog(throwable)) {
         if (isClientDisconnect(throwable)) {
            // Client closed the connection before the response finished sending (e.g. canceled download,
            // closed tab, request timeout). This is not a server error, so log quietly without a stack trace.
            logger.info("Client disconnected before response completed: %s", url);
         } else if (OseeProperties.isInTest()) {
            // In tests, response-stream write failures (e.g. canceled or aborted report exports) are expected
            // noise. Log quietly. On a real server these still log at ERROR so genuine failures are surfaced.
            logger.info("Response streaming failed during test run: %s", url);
         } else {
            logger.errorNoFormat(throwable, url);
         }
      }

      //@formatter:off
      return
         Response
            .status(status)
            .entity(message)
            .header(JaxRsConstants.OSEE_ERROR_REPONSE_HEADER,Boolean.TRUE)
            .build();
      //@formatter:on

   }

   /**
    * Determines whether the given throwable (or any exception in its cause chain) represents a client
    * disconnect - i.e. the client closed the connection before the response was fully written. These are
    * detected by exception type name (e.g. Jetty's EofException) or by an IOException whose message
    * indicates an aborted, reset, or broken connection. Detection is by name/message to avoid a compile
    * time dependency on the servlet container.
    *
    * @param throwable the throwable to inspect.
    * @return true if the throwable chain indicates a client disconnect; false otherwise.
    */

   private static boolean isClientDisconnect(Throwable throwable) {
      for (Throwable cause = throwable; cause != null; cause = cause.getCause()) {
         String simpleName = cause.getClass().getSimpleName();
         if ("EofException".equals(simpleName) || "ClientAbortException".equals(simpleName)) {
            return true;
         }
         if (cause instanceof java.io.IOException) {
            String msg = cause.getMessage();
            if (msg != null) {
               String lower = msg.toLowerCase();
               if (lower.contains("connection was aborted") || lower.contains("connection reset")
                  || lower.contains("broken pipe") || lower.contains("aborted by")) {
                  return true;
               }
            }
         }
      }
      return false;
   }
}

/* EOF */

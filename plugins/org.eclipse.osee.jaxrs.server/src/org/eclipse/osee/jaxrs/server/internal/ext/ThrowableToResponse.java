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

   static Response toResponse(Throwable throwable, UriInfo uriInfo, ExceptionRegistryOperations exceptionRegistryOperations, Log logger) {

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
         logger.errorNoFormat(throwable, url);
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
}

/* EOF */

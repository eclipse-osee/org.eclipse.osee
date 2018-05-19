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
package org.eclipse.osee.jaxrs.server.internal.ext;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.newStatusType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.JaxRsConstants;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
   private final Log logger;
   @Context
   private UriInfo uriInfo;

   public GenericExceptionMapper(Log logger) {
      this.logger = logger;
   }

   @Override
   public final Response toResponse(Throwable throwable) {
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
      logger.error(throwable, url);

      return Response.status(status).entity(message).header(JaxRsConstants.OSEE_ERROR_REPONSE_HEADER,
         Boolean.TRUE).build();
   }
}
/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.rest.admin.internal;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.jaxrs.ErrorResponse;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

   private static final String SEE_HTTP_STATUS_CODES = "See HTTP Status codes";
   private static final String INTERNAL_SERVER_ERROR_TYPE = "Internal Server Error";
   private static final String APPLICATION_EXCEPTION_TYPE = "Web Application Exception";
   private static final String OSEE_APPLICATION_EXCEPTION_TYPE = "Osee Web Application Exception";

   private final Log logger;

   public GenericExceptionMapper(Log logger) {
      super();
      this.logger = logger;
   }

   @Override
   public Response toResponse(Throwable throwable) {
      boolean isError = false;
      String logMessage;

      OseeWebApplicationException exception;
      if (throwable instanceof OseeWebApplicationException) {
         logMessage = OSEE_APPLICATION_EXCEPTION_TYPE;
         exception = ((OseeWebApplicationException) throwable);
      } else if (throwable instanceof WebApplicationException) {
         logMessage = APPLICATION_EXCEPTION_TYPE;
         WebApplicationException webAppException = ((WebApplicationException) throwable);
         Response response = webAppException.getResponse();
         int statusCode = response.getStatus();
         String message = webAppException.getMessage();

         javax.ws.rs.core.Response.StatusType status = Status.fromStatusCode(statusCode);
         if (status == null) {
            status = newStatusType(statusCode, Family.SERVER_ERROR, SEE_HTTP_STATUS_CODES);
         }
         exception = new OseeWebApplicationException(throwable, status, message);
      } else {
         isError = true;
         logMessage = INTERNAL_SERVER_ERROR_TYPE;
         exception = new OseeWebApplicationException(throwable, Status.INTERNAL_SERVER_ERROR);
      }

      ErrorResponse errorResponse = exception.getErrorResponse();
      if (isError) {
         logger.error(throwable, "%s - [%s]", logMessage, errorResponse);
      } else {
         logger.info(throwable, "%s - [%s]", logMessage, errorResponse);
      }
      return exception.getResponse();
   }

   private javax.ws.rs.core.Response.StatusType newStatusType(final int code, final Family family, final String reason) {
      return new javax.ws.rs.core.Response.StatusType() {

         @Override
         public int getStatusCode() {
            return code;
         }

         @Override
         public Family getFamily() {
            return family;
         }

         @Override
         public String getReasonPhrase() {
            return reason;
         }

      };
   }
}

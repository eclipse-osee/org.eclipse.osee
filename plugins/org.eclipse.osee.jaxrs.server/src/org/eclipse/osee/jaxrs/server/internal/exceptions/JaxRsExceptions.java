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
package org.eclipse.osee.jaxrs.server.internal.exceptions;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.newStatusType;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.ErrorResponse;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsExceptions {

   private JaxRsExceptions() {
      // Utility class
   }

   /**
    * Create exception handling providers
    */
   public static List<?> newExceptionProviders(Log logger) {
      List<Object> providers = new ArrayList<>();
      providers.add(new ErrorResponseMessageBodyWriter());
      providers.add(new GenericExceptionMapper(logger));
      providers.add(new ForbiddenExceptionMapper(logger));
      providers.add(new NotAcceptableExceptionMapper(logger));
      providers.add(new NotAllowedExceptionMapper(logger));
      providers.add(new NotAuthorizedExceptionMapper(logger));
      providers.add(new NotFoundExceptionMapper(logger));
      providers.add(new NotSupportedExceptionMapper(logger));
      return providers;
   }

   @Provider
   public static class JaxRsExceptionMapper<T extends WebApplicationException> extends AbstractExceptionMapper<T> {

      private static final String APPLICATION_EXCEPTION_TYPE = "Web Application Exception";

      public JaxRsExceptionMapper(Log logger) {
         super(logger);
      }

      protected String getMessage(T ex) {
         return ex.getMessage();
      }

      @Override
      public OseeWebApplicationException asWebAppException(T ex) {
         Response response = ex.getResponse();
         String message = getMessage(ex);
         StatusType status = response.getStatusInfo();
         OseeWebApplicationException exception = new OseeWebApplicationException(ex.getCause(), status, message);
         getLogger().info(ex, "%s - [%s]", APPLICATION_EXCEPTION_TYPE, exception.getErrorResponse());
         return exception;
      }
   }

   @Provider
   public static class ForbiddenExceptionMapper extends JaxRsExceptionMapper<ForbiddenException> {
      public ForbiddenExceptionMapper(Log logger) {
         super(logger);
      }
   }

   @Provider
   public static class NotAcceptableExceptionMapper extends JaxRsExceptionMapper<NotAcceptableException> {
      public NotAcceptableExceptionMapper(Log logger) {
         super(logger);
      }
   }

   @Provider
   public static class NotAllowedExceptionMapper extends JaxRsExceptionMapper<NotAllowedException> {
      public NotAllowedExceptionMapper(Log logger) {
         super(logger);
      }
   }
   @Provider
   public static class NotAuthorizedExceptionMapper extends JaxRsExceptionMapper<NotAuthorizedException> {
      public NotAuthorizedExceptionMapper(Log logger) {
         super(logger);
      }
   }

   @Provider
   public static class NotFoundExceptionMapper extends JaxRsExceptionMapper<NotFoundException> {
      public NotFoundExceptionMapper(Log logger) {
         super(logger);
      }

      @Override
      protected String getMessage(NotFoundException ex) {
         String baseMessage = super.getMessage(ex);
         String message =
            String.format("%sUnable to find resource at [%s]", Strings.isValid(baseMessage) ? baseMessage + " - " : "",
               getUriInfo().getRequestUri().toASCIIString());
         return message;
      }
   }

   @Provider
   public static class NotSupportedExceptionMapper extends JaxRsExceptionMapper<NotSupportedException> {
      public NotSupportedExceptionMapper(Log logger) {
         super(logger);
      }
   }

   @Provider
   public static class GenericExceptionMapper extends AbstractExceptionMapper<Throwable> {

      private static final String SEE_HTTP_STATUS_CODES = "See HTTP Status codes";
      private static final String INTERNAL_SERVER_ERROR_TYPE = "Internal Server Error";
      private static final String APPLICATION_EXCEPTION_TYPE = "Web Application Exception";
      private static final String OSEE_APPLICATION_EXCEPTION_TYPE = "Osee Web Application Exception";

      public GenericExceptionMapper(Log logger) {
         super(logger);
      }

      @Override
      public OseeWebApplicationException asWebAppException(Throwable throwable) {
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

            StatusType status = Status.fromStatusCode(statusCode);
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
            getLogger().error(throwable, "%s - [%s]", logMessage, errorResponse);
         } else {
            getLogger().info(throwable, "%s - [%s]", logMessage, errorResponse);
         }
         return exception;
      }
   }
}

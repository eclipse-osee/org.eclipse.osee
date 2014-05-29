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
package org.eclipse.osee.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public class OseeWebApplicationException extends WebApplicationException {

   private static final long serialVersionUID = -1566145923396351675L;

   private static final String EXCEPTION_NOT_AVAILABLE = "N/A";
   private static final String DEFAULT_ERROR_MESSAGE =
      "Exception message unavaliable - both exception and message were null";

   private final StatusType errorStatus;
   private final String errorMessage;
   private final ErrorResponse errorResponse;

   public OseeWebApplicationException(Status status) {
      this(null, status);
   }

   public OseeWebApplicationException(Status status, String message, Object... args) {
      this(null, status, message, args);
   }

   public OseeWebApplicationException(Throwable cause, Status status) {
      this(cause, status, null);
   }

   public OseeWebApplicationException(Throwable cause, StatusType status, String message, Object... args) {
      super(cause, status.getStatusCode());
      this.errorStatus = status;
      if (message != null) {
         this.errorMessage = formatMessage(message, args);
      } else {
         this.errorMessage = null;
      }
      this.errorResponse = newErrorResponse(getCause(), errorStatus, errorMessage);
   }

   @Override
   public Response getResponse() {
      return Response.status(getErrorStatus()).type(MediaType.APPLICATION_JSON_TYPE).entity(getErrorResponse()).build();
   }

   public StatusType getErrorStatus() {
      return errorStatus;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public ErrorResponse getErrorResponse() {
      return errorResponse;
   }

   private static ErrorResponse newErrorResponse(Throwable throwable, StatusType errorStatus, String errorMsg) {
      ErrorResponse response = new ErrorResponse();

      String exceptionMsg = throwable != null ? Lib.exceptionToString(throwable) : EXCEPTION_NOT_AVAILABLE;
      response.setException(exceptionMsg);

      String errorMsgToSet = errorMsg;
      if (errorMsgToSet == null) {
         errorMsgToSet = throwable != null ? throwable.getLocalizedMessage() : DEFAULT_ERROR_MESSAGE;
      }
      response.setErrorMessage(errorMsgToSet);

      StatusType status = errorStatus != null ? errorStatus : Status.INTERNAL_SERVER_ERROR;
      response.setErrorCode(status.getStatusCode());
      response.setErrorReason(status.getReasonPhrase());
      response.setErrorType(status.getFamily().toString());
      return response;
   }

   private static String formatMessage(String message, Object... args) {
      String toReturn;
      try {
         toReturn = String.format(message, args);
      } catch (RuntimeException ex) {
         toReturn =
            String.format(
               "Exception message could not be formatted: [%s] with the following arguments [%s].  Cause [%s]",
               message, Collections.toString(",", args), ex.toString());
      }
      return toReturn;
   }
}

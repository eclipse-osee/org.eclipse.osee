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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public class OseeWebApplicationException extends WebApplicationException {
   private static final long serialVersionUID = -1566145923396351675L;
   private final StatusType errorStatus;
   private final String errorMessage;

   public OseeWebApplicationException(Status status, String message, Object... args) {
      this(null, status, message, args);
   }

   public OseeWebApplicationException(Throwable cause, StatusType status, String message, Object... args) {
      super(cause, status.getStatusCode());
      this.errorStatus = status;
      if (message != null) {
         this.errorMessage = formatMessage(message, args);
      } else {
         this.errorMessage = null;
      }
   }

   @Override
   public Response getResponse() {
      return Response.status(getErrorStatus()).entity(errorMessage).header(JaxRsConstants.OSEE_ERROR_REPONSE_HEADER,
         Boolean.TRUE).build();
   }

   public StatusType getErrorStatus() {
      return errorStatus;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   private static String formatMessage(String message, Object... args) {
      String toReturn = message;
      if (args != null && args.length > 0) {
         try {

            toReturn = String.format(message, args);
         } catch (RuntimeException ex) {
            toReturn = String.format(
               "Exception message could not be formatted: [%s] with the following arguments [%s].  Cause [%s]", message,
               Collections.toString(",", args), ex.toString());
         }
      }
      return toReturn;
   }
}

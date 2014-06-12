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
package org.eclipse.osee.jaxrs.server.internal.filters;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.authorization.admin.Authorization;
import org.eclipse.osee.authorization.admin.AuthorizationAdmin;
import org.eclipse.osee.authorization.admin.AuthorizationRequest;
import org.eclipse.osee.authorization.admin.AuthorizationRequestBuilder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class SecurityContextProviderImpl implements SecurityContextProvider {

   protected static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
   protected static final String AUTHORIZATION_HEADER = "Authorization";
   protected static final String DATE_HEADER = "Date";

   private final Log logger;
   private final AuthorizationAdmin authorizationAdmin;

   public SecurityContextProviderImpl(Log logger, AuthorizationAdmin authorizationAdmin) {
      super();
      this.logger = logger;
      this.authorizationAdmin = authorizationAdmin;
   }

   @Override
   public SecurityContext getSecurityContext(ContainerRequestContext request) {
      UriInfo uriInfo = request.getUriInfo();
      boolean isSecure = isSecure(uriInfo);
      Date requestDate = asDate(request.getHeaderString(DATE_HEADER));
      String authType = request.getHeaderString(AUTHORIZATION_HEADER);
      String path = uriInfo.getPath();
      String method = request.getMethod();

      AuthorizationRequest authRequest = AuthorizationRequestBuilder.newBuilder()//
      .secure(isSecure)//
      .date(requestDate) //
      .authorizationType(authType) //
      .method(method) //
      .path(path) //
      .build();

      Authorization authorized = authorizationAdmin.authorize(authRequest);
      return new SecurityContextImpl(authorized);
   }

   private boolean isSecure(UriInfo uriInfo) {
      return uriInfo.getAbsolutePath().toASCIIString().startsWith("https");
   }

   private Date asDate(String value) {
      Date toReturn = null;
      if (Strings.isValid(value)) {
         SimpleDateFormat format = new SimpleDateFormat(HTTP_DATE_FORMAT);
         try {
            toReturn = format.parse(value);
         } catch (ParseException ex) {
            logger.warn(ex, "Error parsing http request date [%s]", value);
         }
      }

      if (toReturn == null) {
         toReturn = new Date();
      }
      return toReturn;
   }

   private static final class SecurityContextImpl implements SecurityContext {

      private final Authorization authorization;

      public SecurityContextImpl(Authorization authorized) {
         super();
         this.authorization = authorized;
      }

      @Override
      public Principal getUserPrincipal() {
         if (authorization == null) {
            throw new InvalidAuthorizationHeaderException();
         }
         return authorization.getPrincipal();
      }

      @Override
      public boolean isUserInRole(String role) {
         if (authorization == null) {
            throw new InvalidAuthorizationHeaderException();
         }
         return authorization.isInRole(role);
      }

      @Override
      public boolean isSecure() {
         if (authorization == null) {
            throw new InvalidAuthorizationHeaderException();
         }
         return authorization.isSecure();
      }

      @Override
      public String getAuthenticationScheme() {
         if (authorization == null) {
            throw new InvalidAuthorizationHeaderException();
         }
         return authorization.getScheme();
      }
   }

   private static final class InvalidAuthorizationHeaderException extends OseeWebApplicationException {

      private static final long serialVersionUID = -7930846912940821509L;

      public static final String ERROR_MESSAGE =
         "Authorization Error - This could be due to missing properties in the header.";

      public InvalidAuthorizationHeaderException() {
         super(Status.UNAUTHORIZED, ERROR_MESSAGE);
      }

   }
}

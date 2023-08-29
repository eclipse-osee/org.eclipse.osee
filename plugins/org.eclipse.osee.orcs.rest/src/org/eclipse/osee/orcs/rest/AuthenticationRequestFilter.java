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

package org.eclipse.osee.orcs.rest;

import static org.eclipse.osee.framework.core.data.CoreActivityTypes.JAXRS_METHOD_CALL;
import static org.eclipse.osee.framework.core.data.CoreActivityTypes.JAXRS_METHOD_CALL_FILTER_ERROR;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.activity.ActivityConstants;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
@Provider
public class AuthenticationRequestFilter implements ContainerRequestFilter {

   private OrcsApi orcsApi;
   private JaxRsApi jaxRsApi;
   private ActivityLog activityLog;

   public void bindOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      jaxRsApi = orcsApi.jaxRsApi();
      activityLog = orcsApi.getActivityLog();
   }

   /**
    * Called before a resource method is executed
    */
   @Override
   public void filter(ContainerRequestContext requestContext) {
      boolean loadingData = requestContext.getUriInfo().getRequestUri().toString().contains(
         "/ide/session") || requestContext.getUriInfo().getRequestUri().toString().contains(
            "orcs/datastore/initialize") || requestContext.getUriInfo().getRequestUri().toString().contains(
               "osee/*") || requestContext.getUriInfo().getRequestUri().toString().contains(
                  "dispo/*") || requestContext.getUriInfo().getRequestUri().toString().contains("coverage/*");
      try {
         String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

         if (authHeader != null) {
            String jwtLoginKey = orcsApi.userService().getLoginKey();
            if (Strings.isValid(jwtLoginKey)) {
               String[] authArray = authHeader.split(" ");
               if (authArray.length == 2) {
                  String[] jwt = authArray[1].split("\\.");
                  Decoder urlDecoder = Base64.getUrlDecoder();
                  String payloadJson = new String(urlDecoder.decode(jwt[1]), StandardCharsets.UTF_8);

                  String loginId = jaxRsApi.readValue(payloadJson, jwtLoginKey).toLowerCase();
                  orcsApi.userService().setUserForCurrentThread(loginId);
               }
            } else if (!loadingData && authHeader.startsWith(OseeProperties.LOGIN_ID_AUTH_SCHEME)) {
               String loginId = authHeader.substring(OseeProperties.LOGIN_ID_AUTH_SCHEME.length()).toLowerCase();

               orcsApi.userService().setUserForCurrentThread(loginId);

            } else if (!loadingData) {
               //TODO: ensure web clients to use Basic scheme then remove
               if (Strings.isNumeric(authHeader)) {
                  orcsApi.userService().setUserForCurrentThread(UserId.valueOf(authHeader.toLowerCase()));
               }
            } else {
               //This is here because current auth isn't passing in windows login id at first layer
               String accountId = requestContext.getHeaderString("osee.account.id");
               String userId = requestContext.getHeaderString("osee.user.id");
               orcsApi.userService().setUserForCurrentThread(UserId.valueOf(userId.toLowerCase()));
               if (orcsApi.userService().getUser().isInvalid()) {
                  orcsApi.userService().setUserForCurrentThread(UserId.valueOf(accountId.toLowerCase()));
               }
            }

         }

      } catch (Exception ex) {
         orcsApi.getActivityLog().createThrowableEntry(CoreActivityTypes.OSEE_ERROR, ex);
      }
      if (!loadingData && orcsApi.userService().getUser().isInvalid()) {
         unauthorized(requestContext);
      }
      if (activityLog.isEnabled()) {
         try {
            String message = String.format("%s %s", requestContext.getMethod(), requestContext.getUriInfo().getPath());
            String clientStr = requestContext.getHeaderString("osee.client.id");
            Long clientId = Strings.isValid(clientStr) ? Long.valueOf(clientStr) : Id.SENTINEL;
            Long serverId = Long.valueOf(OseeClient.getPort());

            Long entryId = activityLog.createActivityThread(JAXRS_METHOD_CALL, orcsApi.userService().getUser(),
               serverId, clientId, message);

            requestContext.setProperty(ActivityConstants.HTTP_HEADER__ACTIVITY_ENTRY_ID, entryId);
         } catch (Throwable th) {
            activityLog.createThrowableEntry(JAXRS_METHOD_CALL_FILTER_ERROR, th);

         }
      }

   }

   private void unauthorized(ContainerRequestContext requestContext) {
      ResponseBuilder rp = Response.status(Response.Status.UNAUTHORIZED);
      requestContext.abortWith(rp.build());
   }
}
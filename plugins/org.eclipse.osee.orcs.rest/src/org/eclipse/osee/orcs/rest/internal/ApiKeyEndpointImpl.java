/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.orcs.rest.internal;

import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.ApiKeyApi;
import org.eclipse.osee.framework.core.data.ApiKey;
import org.eclipse.osee.framework.core.data.KeyScopeContainer;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.ApiKeyEndpoint;

public class ApiKeyEndpointImpl implements ApiKeyEndpoint {
   @Context
   private final ActivityLog activityLog;
   private final UserService userService;
   private final ApiKeyApi apiKeyApi;

   public ApiKeyEndpointImpl(OrcsApi orcsApi, ActivityLog activityLog, JdbcService jdbcService, ApiKeyApi apiKeyApi) {
      this.activityLog = activityLog;
      this.apiKeyApi = apiKeyApi;
      userService = orcsApi.userService();
   }

   @Override
   public Response getApiKeys() {
      List<ApiKey> apiKeys = apiKeyApi.getApiKeys(userService.getUser().getToken());

      return Response.status(Response.Status.OK).entity(apiKeys).build();
   }

   @Override
   public Response getKeyScopes() {
      List<KeyScopeContainer> keyScopes = apiKeyApi.getKeyScopes();

      return Response.status(Response.Status.OK).entity(keyScopes).build();
   }

   @Override
   public Response createApiKey(ApiKey apiKey) {

      Map<String, String> uidAndValue = apiKeyApi.createApiKey(apiKey, userService.getUser().getToken());

      return Response.status(Response.Status.OK).entity(uidAndValue).build();
   }

   @Override
   public Response revokeApiKey(long keyUID) {
      boolean revokeSuccess = apiKeyApi.revokeApiKey(keyUID);

      if (revokeSuccess) {
         return Response.ok("Api Key With {ID: " + keyUID + "} Successfully Revoked", MediaType.TEXT_PLAIN).build();
      } else {
         return Response.status(Response.Status.BAD_REQUEST).entity(
            "Failed To Revoke Api Key With {ID: " + keyUID + "}").type(MediaType.TEXT_PLAIN).build();
      }
   }
}
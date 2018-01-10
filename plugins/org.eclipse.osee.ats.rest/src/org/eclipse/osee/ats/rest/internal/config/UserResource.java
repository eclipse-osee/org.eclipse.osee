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
package org.eclipse.osee.ats.rest.internal.config;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Donald G. Dunne
 */
@Path("user")
public final class UserResource {

   private final IAtsUserService userService;

   public UserResource(IAtsUserService userService) {
      this.userService = userService;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public String get(@QueryParam("active") String activeStr) throws Exception {
      Active active = Active.Both;
      if (Strings.isValid(activeStr)) {
         active = Active.valueOf(activeStr);
      }
      JSONArray arr = new JSONArray();
      for (IAtsUser user : userService.getUsers(active)) {
         JSONObject obj = new JSONObject();
         obj.put("id", user.getUserId());
         obj.put("name", user.getName());
         obj.put("email", user.getEmail());
         obj.put("active", user.isActive());
         obj.put("accountId", user.getStoreObject().getId());
         arr.put(obj);
      }
      return arr.toString();
   }
}
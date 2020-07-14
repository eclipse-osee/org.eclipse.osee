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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.HashMap;
import java.util.LinkedList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.client.JaxRsApiImpl;

/**
 * @author Donald G. Dunne
 */
@Path("user")
public final class UserResource {

   private final IAtsUserService userService;
   private static JaxRsApi jaxRsApi;

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

      JaxRsApiImpl impl = new JaxRsApiImpl();
      impl.instantiate();
      String implJson = "";

      HashMap jsonArray = new HashMap();
      LinkedList jsonList = new LinkedList();
      for (AtsUser user : userService.getUsers(active)) {

         jsonArray.put("id", user.getUserId());
         jsonArray.put("name", user.getName());
         jsonArray.put("email", user.getEmail());
         jsonArray.put("active", user.isActive());
         jsonArray.put("accountId", user.getStoreObject().getId());
         String jsonString = impl.toJson(jsonArray);
         implJson = impl.toJson(jsonList);
         jsonList.add(jsonString);

      }

      return jsonList.toString();
   }
}
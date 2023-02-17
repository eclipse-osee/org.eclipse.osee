/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.jaxrs;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * @author Donald G. Dunne
 */
@Provider
public class OseeAccountClientRequestFilter implements ClientRequestFilter {
   private final UserService userService;

   public OseeAccountClientRequestFilter(UserService userService) {
      this.userService = userService;
   }

   @Override
   public void filter(ClientRequestContext context) {
      UserToken user = userService.getUserIfLoaded();
      String authValue;
      if (user.getLoginIds().isEmpty()) {
         authValue = user.getIdString();
      } else {
         authValue = OseeProperties.LOGIN_ID_AUTH_SCHEME + user.getLoginIds().get(0);
      }
      context.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, authValue);
   }
}
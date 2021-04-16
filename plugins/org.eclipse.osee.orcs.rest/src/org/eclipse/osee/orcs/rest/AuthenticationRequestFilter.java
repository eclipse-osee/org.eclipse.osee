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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
@Provider
public class AuthenticationRequestFilter implements ContainerRequestFilter {

   private OrcsApi orcsApi;
   private JaxRsApi jaxRsApi;

   public void bindOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      jaxRsApi = orcsApi.jaxRsApi();
   }

   /**
    * Called before a resource method is executed
    */
   @Override
   public void filter(ContainerRequestContext requestContext) {
      try {
         String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

         if (authHeader != null) {
            String[] authArray = authHeader.split(" ");
            if (authArray.length == 2) {
               String[] jwt = authArray[1].split("\\.");
               Decoder urlDecoder = Base64.getUrlDecoder();
               String payloadJson = new String(urlDecoder.decode(jwt[1]), StandardCharsets.UTF_8);

               String loginId = jaxRsApi.readValue(payloadJson, "activecac");
               orcsApi.userService().setUserForCurrentThread(loginId);
            }
         }
      } catch (Exception ex) {
         orcsApi.getActivityLog().createThrowableEntry(CoreActivityTypes.OSEE_ERROR, ex);
      }
   }
}
/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan T. Baldwin
 */
@Provider
public class AuthenticationResponseFilter implements ContainerResponseFilter {

   private OrcsApi orcsApi;

   public void bindOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * Called after a resource method is executed
    */
   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
      orcsApi.userService().removeUserFromCurrentThread();
   }
}
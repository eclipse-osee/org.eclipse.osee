/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.core;

import org.eclipse.osee.framework.core.data.UserService;

/**
 * @author Ryan D. Brooks
 */
public abstract class OseeApiBase implements OseeApi {

   private JaxRsApi jaxRsApi;
   private OrcsTokenService tokenService;
   private UserService userService;

   public void setOrcsTokenService(OrcsTokenService tokenService) {
      this.tokenService = tokenService;
   }

   public void setJaxRsApi(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
   }

   public void bindUserService(UserService userService) {
      this.userService = userService;
   }

   @Override
   public JaxRsApi jaxRsApi() {
      return jaxRsApi;
   }

   @Override
   public OrcsTokenService tokenService() {
      return tokenService;
   }

   @Override
   public UserService userService() {
      return userService;
   }
}
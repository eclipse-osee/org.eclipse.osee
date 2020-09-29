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

/**
 * @author Ryan D. Brooks
 */
public abstract class OseeApiBase implements OseeApi {

   private JaxRsApi jaxRsApi;
   private OrcsTokenService tokenService;

   public void setOrcsTokenService(OrcsTokenService tokenService) {
      this.tokenService = tokenService;
   }

   public void setJaxRsApi(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
   }

   @Override
   public JaxRsApi jaxRsApi() {
      return jaxRsApi;
   }

   @Override
   public OrcsTokenService tokenService() {
      return tokenService;
   }

}
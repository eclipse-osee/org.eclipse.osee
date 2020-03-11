/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core;

/**
 * @author Ryan D. Brooks
 */
public class OseeApiBase implements OseeApi {

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
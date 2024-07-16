/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.access;

import org.eclipse.osee.framework.core.OseeApiBase;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class OseeApiImpl extends OseeApiBase {

   // for ReviewOsgiXml public void setOrcsTokenService(OrcsTokenService tokenService)
   // for ReviewOsgiXml public void setJaxRsApi(JaxRsApi jaxRsApi)
   IAccessControlService accessControlService;
   private OrcsApi orcsApi;

   public OseeApiImpl() {
      // for jas-rs
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public IAccessControlService getAccessControlService() {
      if (accessControlService == null) {
         accessControlService = new AccessControlServiceImpl(orcsApi);
      }
      return accessControlService;
   }

}

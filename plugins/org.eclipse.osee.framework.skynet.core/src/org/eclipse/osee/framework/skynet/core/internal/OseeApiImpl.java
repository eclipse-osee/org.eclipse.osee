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
package org.eclipse.osee.framework.skynet.core.internal;

import org.eclipse.osee.framework.core.OseeApiBase;
import org.eclipse.osee.framework.core.access.IAccessControlService;

/**
 * @author Donald G. Dunne
 */
public class OseeApiImpl extends OseeApiBase {

   // for ReviewOsgiXml public void setOrcsTokenService(OrcsTokenService tokenService)
   // for ReviewOsgiXml public void setJaxRsApi(JaxRsApi jaxRsApi)
   private IAccessControlService accessControlService;

   public void bindAccessControlService(IAccessControlService accessControlService) {
      this.accessControlService = accessControlService;
   }

   @Override
   public IAccessControlService getAccessControlService() {
      return accessControlService;
   }
}
/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal;

import org.eclipse.osee.framework.core.services.IAccessControlService;

public final class AccessControlHelper {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.access";

   private static AccessControlService accessControlService;

   public void setAccessControlService(IAccessControlService accessControlService) {
      if (accessControlService instanceof AccessControlServiceProxy) {
         AccessControlHelper.accessControlService =
            ((AccessControlServiceProxy) accessControlService).getProxiedObject();
      } else {
         AccessControlHelper.accessControlService = null;
      }
   }

   @Deprecated
   public static AccessControlService getAccessControlService() {
      return accessControlService;
   }

}

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
import org.eclipse.osee.framework.core.util.OsgiUtil;

public final class AccessControlHelper {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.access";

   private AccessControlHelper() {
      // Utility class
   }

   @Deprecated
   public static AccessControlService getAccessControlService() {
      IAccessControlService service = OsgiUtil.getService(AccessControlHelper.class, IAccessControlService.class);
      AccessControlService toReturn = null;
      if (service instanceof AccessControlServiceProxy) {
         toReturn = ((AccessControlServiceProxy) service).getProxiedObject();
      }
      return toReturn;
   }

}

/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.access.internal;

import org.eclipse.osee.framework.access.AccessControlServiceImpl;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.util.OsgiUtil;

/**
 * @author Roberto E. Escobar
 */
public final class AccessControlHelper {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.access";

   private AccessControlHelper() {
      // Utility class
   }

   @Deprecated
   public static AccessControlServiceImpl getAccessControlService() {
      IAccessControlService service = OsgiUtil.getService(AccessControlHelper.class, IAccessControlService.class);
      AccessControlServiceImpl toReturn = null;
      if (service instanceof AccessControlServiceProxy) {
         toReturn = ((AccessControlServiceProxy) service).getProxiedObject();
      }
      return toReturn;
   }

}

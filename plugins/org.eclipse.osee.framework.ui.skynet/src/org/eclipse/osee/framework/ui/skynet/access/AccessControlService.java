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

package org.eclipse.osee.framework.ui.skynet.access;

import org.eclipse.osee.framework.core.model.access.IAccessControlService;

/**
 * @author Donald G. Dunne
 */
public class AccessControlService {

   private static IAccessControlService accessService;

   public static IAccessControlService getAccessService() {
      return accessService;
   }

   public void setAccessService(IAccessControlService accessService) {
      AccessControlService.accessService = accessService;
   }

}

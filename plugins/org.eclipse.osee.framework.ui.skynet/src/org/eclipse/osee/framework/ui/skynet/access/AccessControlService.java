/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

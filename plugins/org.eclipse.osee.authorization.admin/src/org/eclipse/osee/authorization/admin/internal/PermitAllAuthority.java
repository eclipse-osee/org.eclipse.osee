/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.authorization.admin.internal;

import org.eclipse.osee.authorization.admin.Authority;
import org.eclipse.osee.authorization.admin.AuthorizationConstants;

/**
 * @author Roberto E. Escobar
 */
public class PermitAllAuthority implements Authority {

   @Override
   public String getScheme() {
      return AuthorizationConstants.PERMIT_ALL_AUTHORIZER_SCHEME;
   }

   @Override
   public boolean isInRole(String role) {
      return true;
   }

}

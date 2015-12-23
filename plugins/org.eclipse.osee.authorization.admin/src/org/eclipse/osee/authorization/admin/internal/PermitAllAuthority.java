/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

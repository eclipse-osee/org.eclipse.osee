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

package org.eclipse.osee.authentication.admin;

import org.eclipse.osee.authentication.admin.internal.AuthenticationUtil;

/**
 * @author Roberto E. Escobar
 */
public interface AuthenticationAdmin {

   public static final AuthenticatedUser ANONYMOUS_USER = AuthenticationUtil.newAnonymousUser();

   AuthenticatedUser authenticate(AuthenticationRequest authenticationRequest);

   boolean isSchemeAllowed(String scheme);

   Iterable<String> getAllowedSchemes();

   Iterable<String> getAvailableSchemes();

   String getDefaultScheme();

}

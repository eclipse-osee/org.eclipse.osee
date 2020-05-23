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

package org.eclipse.osee.jaxrs.server.security;

import org.eclipse.osee.account.admin.OseePrincipal;

/**
 * @author Roberto E. Escobar
 */
public interface JaxRsAuthenticator {
   OseePrincipal authenticate(String scheme, String username, String password);
}
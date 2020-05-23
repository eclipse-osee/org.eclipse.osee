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

import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public interface OAuthCodeGrant {

   long getUuid();

   long getSubjectId();

   long getClientId();

   long getIssuedAt();

   long getExpiresIn();

   String getCode();

   String getRedirectUri();

   String getClientCodeVerifier();

   String getAudience();

   List<String> getApprovedScopes();

}

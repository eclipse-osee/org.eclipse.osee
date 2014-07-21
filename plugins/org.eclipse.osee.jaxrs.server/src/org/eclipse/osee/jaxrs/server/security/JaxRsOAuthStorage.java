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
package org.eclipse.osee.jaxrs.server.security;

import org.apache.cxf.rs.security.oauth2.common.Client;

/**
 * @author Roberto E. Escobar
 */
public interface JaxRsOAuthStorage {

   Client getClient(String clientId);

   void storeCodeGrant(String encrypted);

   void removeCodeGrant(String code);

   String getAccessTokenByRefreshToken(String refreshToken);

   void storeAccessToken(String encryptedToken);

   void storeRefreshToken(String encryptedRefreshToken, String encryptedAccessToken);

   void removeRefreshToken(String refreshToken);

   void removeAccessToken(String tokenKey);

   String getPreauthorizedToken(String clientId, String subjectId, String grantType);

   String getCodeGrant(String code);

}
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

/**
 * @author Roberto E. Escobar
 */
public interface JaxRsOAuthStorage {

   OAuthCodeGrant getCodeGrant(String code);

   void storeCodeGrant(OAuthCodeGrant codeGrant);

   void removeCodeGrant(OAuthCodeGrant codeGrant);

   Iterable<OAuthToken> getAccessTokensByRefreshToken(String refreshToken);

   OAuthToken getPreauthorizedToken(long clientId, long subjectId, String grantType);

   void storeToken(OAuthToken... tokens);

   void relateTokens(OAuthToken refreshToken, OAuthToken accessToken);

   void removeToken(Iterable<OAuthToken> tokens);

   void removeTokenByKey(String tokenKey);

}
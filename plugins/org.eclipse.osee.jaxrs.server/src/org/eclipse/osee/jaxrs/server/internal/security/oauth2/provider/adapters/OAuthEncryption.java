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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters;

import javax.crypto.SecretKey;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.crypto.CryptoUtils;
import org.apache.cxf.rs.security.oauth2.utils.crypto.ModelEncryptionSupport;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OAuthEncryption {

   private static final String AES_CRYPTO_ALGO = "AES";

   public SecretKey decodeSecretKey(String encodedSecretKey, String secretKeyAlgorithm) {
      String secretKeyAlgorithmToUse = secretKeyAlgorithm;
      if (!Strings.isValid(secretKeyAlgorithmToUse)) {
         secretKeyAlgorithmToUse = AES_CRYPTO_ALGO;
      }
      return CryptoUtils.decodeSecretKey(encodedSecretKey, secretKeyAlgorithmToUse);
   }

   public String encryptCodeGrant(AuthorizationCode grant, SecretKey secretKey) {
      return ModelEncryptionSupport.encryptCodeGrant(grant, secretKey);
   }

   public String encryptAccessToken(AccessToken token, SecretKey secretKey) {
      return ModelEncryptionSupport.encryptAccessToken(token, secretKey);
   }

   public String encryptRefreshToken(RefreshOAuthToken refreshToken, SecretKey secretKey) {
      return ModelEncryptionSupport.encryptRefreshToken(refreshToken, secretKey);
   }

   public ServerAuthorizationCodeGrant decryptCodeGrant(OAuthDataProvider provider, String grant, SecretKey secretKey) {
      return ModelEncryptionSupport.decryptCodeGrant(provider, grant, secretKey);
   }

   public ServerAccessToken decryptAccessToken(OAuthDataProvider provider, String token, SecretKey secretKey) {
      ServerAccessToken accessToken = ModelEncryptionSupport.decryptAccessToken(provider, token, secretKey);
      return accessToken;
   }

   public RefreshToken decryptRefreshToken(OAuthDataProvider provider, String token, SecretKey secretKey) {
      return ModelEncryptionSupport.decryptRefreshToken(provider, token, secretKey);
   }
}
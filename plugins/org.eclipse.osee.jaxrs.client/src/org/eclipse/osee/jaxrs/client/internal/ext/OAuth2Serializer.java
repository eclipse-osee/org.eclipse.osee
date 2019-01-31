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
package org.eclipse.osee.jaxrs.client.internal.ext;

import static org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Util.getOAuthJSONProvider;
import static org.eclipse.osee.jaxrs.client.internal.ext.OAuth2Util.newException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import javax.crypto.SecretKey;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.provider.OAuthJSONProvider;
import org.apache.cxf.rs.security.oauth2.utils.crypto.CryptoUtils;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2Serializer {

   private static final String AES_CRYPTO_ALGO = "AES";

   public SecretKey decodeSecretKey(String encodedSecretKey, String secretKeyAlgorithm) {
      String secretKeyAlgorithmToUse = secretKeyAlgorithm;
      if (!Strings.isValid(secretKeyAlgorithmToUse)) {
         secretKeyAlgorithmToUse = AES_CRYPTO_ALGO;
      }
      return CryptoUtils.decodeSecretKey(encodedSecretKey, secretKeyAlgorithmToUse);
   }

   public String encryptAccessToken(ClientAccessToken token, SecretKey secretKey) {
      String json = toJson(token);
      return CryptoUtils.encryptSequence(json, secretKey);
   }

   public ClientAccessToken decryptAccessToken(String value, SecretKey secretKey) {
      String decrypted = CryptoUtils.decryptSequence(value, secretKey);
      return fromJson(decrypted);
   }

   public ClientAccessToken fromJson(String value) {
      OAuthJSONProvider provider = getOAuthJSONProvider();
      InputStream stream = null;
      try {
         stream = Lib.stringToInputStream(value);
         Map<String, String> map = provider.readJSONResponse(stream);
         return OAuthClientUtils.fromMapToClientToken(map);
      } catch (Exception ex) {
         throw newException(ex, "Error deserializing value to client access token");
      } finally {
         Lib.close(stream);
      }
   }

   public String toJson(ClientAccessToken token) {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      OAuthJSONProvider provider = getOAuthJSONProvider();
      try {
         provider.writeTo(token, null, null, null, null, null, os);
      } catch (Exception ex) {
         throw newException(ex, "Error serializing client access token");
      }
      return new String(os.toByteArray(), Strings.UTF_8);
   }
}
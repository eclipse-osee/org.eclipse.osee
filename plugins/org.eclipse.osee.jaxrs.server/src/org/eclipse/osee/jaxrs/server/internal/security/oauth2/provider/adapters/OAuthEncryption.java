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

import java.security.Key;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.crypto.CryptoUtils;
import org.apache.cxf.rs.security.oauth2.utils.crypto.KeyProperties;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.session.SessionData;

/**
 * @author Roberto E. Escobar
 */
public class OAuthEncryption {

   private static final String AES_CRYPTO_ALGO = "AES";
   private static final String SEP = "|";

   public SecretKey decodeSecretKey(String encodedSecretKey, String secretKeyAlgorithm) {
      String secretKeyAlgorithmToUse = secretKeyAlgorithm;
      if (!Strings.isValid(secretKeyAlgorithmToUse)) {
         secretKeyAlgorithmToUse = AES_CRYPTO_ALGO;
      }
      return CryptoUtils.decodeSecretKey(encodedSecretKey, secretKeyAlgorithmToUse);
   }

   public String encryptCodeGrant(AuthorizationCode grant, SecretKey secretKey) throws SecurityException {
      return encryptCodeGrant(grant, secretKey, null);
   }

   private static String encryptCodeGrant(ServerAuthorizationCodeGrant grant, Key secretKey, KeyProperties props) throws SecurityException {
      String tokenSequence = tokenizeCodeGrant(grant);

      return CryptoUtils.encryptSequence(tokenSequence, secretKey, props);
   }

   private static String tokenizeCodeGrant(ServerAuthorizationCodeGrant grant) {
      StringBuilder state = new StringBuilder();
      // 0: client id
      state.append(grant.getClient().getClientId());
      state.append(SEP);
      // 1: code
      state.append(tokenizeString(grant.getCode()));
      state.append(SEP);
      // 2: expiresIn
      state.append(grant.getExpiresIn());
      state.append(SEP);
      // 3: issuedAt
      state.append(grant.getIssuedAt());
      state.append(SEP);
      // 4: redirect URI
      state.append(tokenizeString(grant.getRedirectUri()));
      state.append(SEP);
      // 5: audience
      state.append(tokenizeString(grant.getAudience()));
      state.append(SEP);
      // 6: code verifier
      state.append(tokenizeString(grant.getClientCodeVerifier()));
      state.append(SEP);
      // 7: approved scopes
      state.append(grant.getApprovedScopes().toString());
      state.append(SEP);
      // 8: subject
      tokenizeUserSubject(state, grant.getSubject());

      return state.toString();
   }

   public String encryptSessionToken(SessionData session, SecretKey secretKey) {
      return encryptSessionToken(session, secretKey, null);
   }

   private String encryptSessionToken(SessionData session, SecretKey secretKey, KeyProperties props) {
      String tokenSequence = tokenizeSessionToken(session);
      return CryptoUtils.encryptSequence(tokenSequence, secretKey, props);
   }

   public String encryptAccessToken(AccessToken token, SecretKey secretKey) {
      return encryptAccessToken(token, secretKey, null);
   }

   private static String encryptAccessToken(ServerAccessToken token, Key secretKey, KeyProperties props) throws SecurityException {
      String tokenSequence = tokenizeServerToken(token);
      return CryptoUtils.encryptSequence(tokenSequence, secretKey, props);
   }

   public String encryptRefreshToken(RefreshOAuthToken refreshToken, SecretKey secretKey) {
      return encryptRefreshToken(refreshToken, secretKey, null);
   }

   private static String encryptRefreshToken(RefreshToken token, Key secretKey, KeyProperties props) throws SecurityException {
      String tokenSequence = tokenizeRefreshToken(token);

      return CryptoUtils.encryptSequence(tokenSequence, secretKey, props);
   }

   public ServerAuthorizationCodeGrant decryptCodeGrant(OAuthDataProvider provider, String grant, SecretKey secretKey) {
      return decryptCodeGrant(provider, grant, secretKey, null);
   }

   private static ServerAuthorizationCodeGrant decryptCodeGrant(OAuthDataProvider provider, String encodedData, Key key, KeyProperties props) throws SecurityException {
      String decryptedSequence = CryptoUtils.decryptSequence(encodedData, key, props);
      return recreateCodeGrant(provider, decryptedSequence);
   }

   private static ServerAuthorizationCodeGrant recreateCodeGrant(OAuthDataProvider provider, String decryptedSequence) throws SecurityException {
      return recreateCodeGrantInternal(provider, decryptedSequence);
   }

   private static ServerAuthorizationCodeGrant recreateCodeGrantInternal(OAuthDataProvider provider, String sequence) {
      String[] parts = getParts(sequence);
      ServerAuthorizationCodeGrant grant = new ServerAuthorizationCodeGrant(provider.getClient(parts[0]), parts[1],
         Long.valueOf(parts[2]), Long.valueOf(parts[3]));
      grant.setRedirectUri(getStringPart(parts[4]));
      grant.setAudience(getStringPart(parts[5]));
      grant.setClientCodeVerifier(getStringPart(parts[6]));
      grant.setApprovedScopes(parseSimpleList(parts[7]));
      grant.setSubject(recreateUserSubject(parts[8]));
      return grant;
   }

   private static UserSubject recreateUserSubject(String sequence) {
      UserSubject subject = null;
      if (!sequence.trim().isEmpty()) {
         String[] subjectParts = sequence.split("&");
         subject = new UserSubject(getStringPart(subjectParts[0]), getStringPart(subjectParts[1]));
         subject.setRoles(parseSimpleList(subjectParts[2]));
         subject.setProperties(parseSimpleMap(subjectParts[3]));
      }
      return subject;

   }

   private static Map<String, String> parseSimpleMap(String mapStr) {
      Map<String, String> props = new HashMap<>();
      List<String> entries = parseSimpleList(mapStr);
      for (String entry : entries) {
         String[] pair = entry.split("=");
         props.put(pair[0].trim(), pair[1]);
      }
      return props;
   }

   private static String prepareSimpleString(String str) {
      return str.trim().isEmpty() ? "" : str.substring(1, str.length() - 1);
   }

   private static List<String> parseSimpleList(String listStr) {
      return Collections.fromString(prepareSimpleString(listStr), ",");
   }

   private static String getStringPart(String str) {
      return " ".equals(str) ? null : str;
   }

   private static String[] getParts(String sequence) {
      return sequence.split("\\" + SEP);
   }

   public SessionData decryptSessionToken(String token, SecretKey secretKey) {
      String decryptedSequence = CryptoUtils.decryptSequence(token, secretKey, null);
      String[] parts = getParts(decryptedSequence);

      UserSubject recreateUserSubject = recreateUserSubject(parts[9]);

      SessionData toReturn = new SessionData(parts[0]);
      toReturn.setAccountActive(Boolean.getBoolean(parts[1]));
      toReturn.setExpiresIn(Long.valueOf(parts[2]));
      toReturn.setIssuedAt(Long.valueOf(parts[3]));
      toReturn.setAccountDisplayName(parts[4]);
      toReturn.setAccountEmail(parts[5]);
      toReturn.setAccountName(parts[6]);
      toReturn.setAccountUsername(parts[7]);
      toReturn.setAccountId(Long.valueOf(parts[8]));
      toReturn.setSubject(recreateUserSubject);
      return toReturn;
   }

   public ServerAccessToken decryptAccessToken(OAuthDataProvider provider, String token, SecretKey secretKey) {
      ServerAccessToken accessToken = decryptAccessToken(provider, token, secretKey, null);
      return accessToken;
   }

   private static ServerAccessToken decryptAccessToken(OAuthDataProvider provider, String encodedData, Key secretKey, KeyProperties props) throws SecurityException {
      String decryptedSequence = CryptoUtils.decryptSequence(encodedData, secretKey, props);
      return recreateAccessToken(provider, encodedData, decryptedSequence);
   }

   private static ServerAccessToken recreateAccessToken(OAuthDataProvider provider, String newTokenKey, String decryptedSequence) throws SecurityException {
      return recreateAccessToken(provider, newTokenKey, getParts(decryptedSequence));
   }

   private static ServerAccessToken recreateAccessToken(OAuthDataProvider provider, String newTokenKey, String[] parts) {

      final ServerAccessToken newToken = new ServerAccessToken(provider.getClient(parts[4]), parts[1],
         newTokenKey == null ? parts[0] : newTokenKey, Long.valueOf(parts[2]), Long.valueOf(parts[3])) {
         private static final long serialVersionUID = 7381031812625396582L;
      };

      newToken.setRefreshToken(getStringPart(parts[5]));
      newToken.setGrantType(getStringPart(parts[6]));
      newToken.setAudience(getStringPart(parts[7]));
      newToken.setParameters(parseSimpleMap(parts[8]));

      // Permissions
      if (!parts[9].trim().isEmpty()) {
         List<OAuthPermission> perms = new LinkedList<>();
         String[] allPermParts = parts[9].split("&");
         for (int i = 0; i + 4 < allPermParts.length; i = i + 5) {
            OAuthPermission perm = new OAuthPermission(allPermParts[i], allPermParts[i + 1]);
            perm.setDefault(Boolean.valueOf(allPermParts[i + 2]));
            perm.setHttpVerbs(parseSimpleList(allPermParts[i + 3]));
            perm.setUris(parseSimpleList(allPermParts[i + 4]));
            perms.add(perm);
         }
         newToken.setScopes(perms);
      }
      //UserSubject:
      newToken.setSubject(recreateUserSubject(parts[10]));

      return newToken;
   }

   public RefreshToken decryptRefreshToken(OAuthDataProvider provider, String token, SecretKey secretKey) {
      return decryptRefreshToken(provider, token, secretKey, null);
   }

   private static RefreshToken decryptRefreshToken(OAuthDataProvider provider, String encodedData, Key key, KeyProperties props) throws SecurityException {
      String decryptedSequence = CryptoUtils.decryptSequence(encodedData, key, props);
      return recreateRefreshToken(provider, encodedData, decryptedSequence);
   }

   private static RefreshToken recreateRefreshToken(OAuthDataProvider provider, String newTokenKey, String decryptedSequence) throws SecurityException {
      String[] parts = getParts(decryptedSequence);
      ServerAccessToken token = recreateAccessToken(provider, newTokenKey, parts);
      return new RefreshToken(token, newTokenKey, parseSimpleList(parts[parts.length - 1]));
   }

   private static String tokenizeRefreshToken(RefreshToken token) {
      String seq = tokenizeServerToken(token);
      return seq + SEP + token.getAccessTokens().toString();
   }

   private static String tokenizeSessionToken(SessionData session) {
      StringBuilder state = new StringBuilder();
      // 0: key
      state.append(tokenizeString(session.getGuid()));
      // 1: active
      state.append(SEP);
      state.append(session.getAccountActive());
      // 2: expiresIn
      state.append(SEP);
      state.append(session.getExpiresIn());
      // 3: issuedAt
      state.append(SEP);
      state.append(session.getIssuedAt());
      // 4: display name
      state.append(SEP);
      state.append(tokenizeString(session.getAccountDisplayName()));
      // 5: email
      state.append(SEP);
      state.append(tokenizeString(session.getAccountEmail()));
      // 6: name
      state.append(SEP);
      state.append(tokenizeString(session.getAccountName()));
      // 7: username
      state.append(SEP);
      state.append(tokenizeString(session.getAccountUsername()));
      // 8: id
      state.append(SEP);
      state.append(session.getAccountId());
      // 9: user subject
      state.append(SEP);
      tokenizeUserSubject(state, session.getSubject());

      return state.toString();
   }

   private static String tokenizeServerToken(ServerAccessToken token) {
      StringBuilder state = new StringBuilder();
      // 0: key
      state.append(tokenizeString(token.getTokenKey()));
      // 1: type
      state.append(SEP);
      state.append(tokenizeString(token.getTokenType()));
      // 2: expiresIn
      state.append(SEP);
      state.append(token.getExpiresIn());
      // 3: issuedAt
      state.append(SEP);
      state.append(token.getIssuedAt());
      // 4: client id
      state.append(SEP);
      state.append(tokenizeString(token.getClient().getClientId()));
      // 5: refresh token
      state.append(SEP);
      state.append(tokenizeString(token.getRefreshToken()));
      // 6: grant type
      state.append(SEP);
      state.append(tokenizeString(token.getGrantType()));
      // 7: audience
      state.append(SEP);
      state.append(tokenizeString(token.getAudience()));
      // 8: other parameters
      state.append(SEP);
      // {key=value, key=value}
      state.append(token.getParameters().toString());
      // 9: permissions
      state.append(SEP);
      if (token.getScopes().isEmpty()) {
         state.append(" ");
      } else {
         for (OAuthPermission p : token.getScopes()) {
            // 9.1
            state.append(tokenizeString(p.getPermission()));
            state.append(".");
            // 9.2
            state.append(tokenizeString(p.getDescription()));
            state.append(".");
            // 9.3
            state.append(p.isDefault());
            state.append(".");
            // 9.4
            state.append(p.getHttpVerbs().toString());
            state.append(".");
            // 9.5
            state.append(p.getUris().toString());
         }
      }
      state.append(SEP);
      // 10: user subject
      tokenizeUserSubject(state, token.getSubject());

      return state.toString();
   }

   private static void tokenizeUserSubject(StringBuilder state, UserSubject subject) {
      if (subject != null) {
         // 1
         state.append(tokenizeString(subject.getLogin()));
         state.append("&");
         // 2
         state.append(tokenizeString(subject.getId()));
         state.append("&");
         // 3
         String roles = tokenizeString(subject.getRoles().toString());
         state.append(roles.replaceAll(", ", ","));
         state.append("&");
         // 4
         state.append(tokenizeString(subject.getProperties().toString()));
      } else {
         state.append("&");
      }
   }

   private static String tokenizeString(String str) {
      return str != null ? str.trim() : " ";
   }
}
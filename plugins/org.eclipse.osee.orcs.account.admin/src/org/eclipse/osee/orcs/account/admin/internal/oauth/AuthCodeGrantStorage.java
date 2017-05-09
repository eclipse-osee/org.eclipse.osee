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
package org.eclipse.osee.orcs.account.admin.internal.oauth;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jaxrs.server.database.AbstractDatabaseStorage;
import org.eclipse.osee.jaxrs.server.security.OAuthCodeGrant;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class AuthCodeGrantStorage extends AbstractDatabaseStorage<OAuthCodeGrant> {

   private static final String SCOPES_SEPARATOR = ";";

   private static final String SELECT_AUTH_CODE_BY_CODE = "SELECT * FROM osee_oauth_authorization WHERE code = ?";

   private static final String INSERT_AUTH_CODE =
      "INSERT INTO osee_oauth_authorization (id, client_id, subject_id, issued_at, expires_in, code, redirect_uri, verifier, audience, approved_scopes) VALUES (?,?,?,?,?,?,?,?,?,?)";

   private static final String DELETE_AUTH_CODE_BY_ID = "DELETE FROM osee_oauth_authorization WHERE id = ?";

   public AuthCodeGrantStorage(Log logger, JdbcClient jdbcClient) {
      super(logger, jdbcClient);
   }

   @Override
   protected Object[] asInsert(OAuthCodeGrant data) {
      return new Object[] {
         data.getUuid(),
         data.getClientId(),
         data.getSubjectId(),
         data.getIssuedAt(),
         data.getExpiresIn(),
         data.getCode(),
         asVarcharOrNull(data.getRedirectUri()),
         asVarcharOrNull(data.getClientCodeVerifier()),
         asVarcharOrNull(data.getAudience()),
         asScopesStore(data.getApprovedScopes())};
   }

   @Override
   protected Object[] asUpdate(OAuthCodeGrant data) {
      return new Object[] {data.getUuid()};
   }

   @Override
   protected Object[] asDelete(OAuthCodeGrant data) {
      return new Object[] {data.getUuid()};
   }

   public OAuthCodeGrant getByCode(String code) {
      return selectOneOrNull(SELECT_AUTH_CODE_BY_CODE, code);
   }

   public void insert(OAuthCodeGrant data) {
      insertItems(INSERT_AUTH_CODE, data);
   }

   public void delete(OAuthCodeGrant data) {
      deleteItems(DELETE_AUTH_CODE_BY_ID, data);
   }

   public void delete(Iterable<OAuthCodeGrant> datas) {
      deleteItems(DELETE_AUTH_CODE_BY_ID, datas);
   }

   private Object asScopesStore(List<String> scopes) {
      String scopeData = null;
      if (scopes != null && !scopes.isEmpty()) {
         scopeData = Collections.toString(SCOPES_SEPARATOR, scopes);
      }
      return asVarcharOrNull(scopeData);
   }

   @Override
   protected OAuthCodeGrant readData(JdbcStatement chStmt) {
      final long uuid = chStmt.getLong("id");
      final long clientId = chStmt.getLong("client_id");
      final long subjectId = chStmt.getLong("subject_id");
      final long issuedAt = chStmt.getLong("issued_at");
      final long expiresIn = chStmt.getLong("expires_in");
      final String code = chStmt.getString("code");
      final String redirect_uri = chStmt.getString("redirect_uri");
      final String verifier = chStmt.getString("verifier");
      final String audience = chStmt.getString("audience");
      final List<String> approvedScopes = Collections.fromString(chStmt.getString("approved_scopes"), SCOPES_SEPARATOR);
      return new OAuthCodeGrant() {

         @Override
         public long getUuid() {
            return uuid;
         }

         @Override
         public long getSubjectId() {
            return subjectId;
         }

         @Override
         public long getClientId() {
            return clientId;
         }

         @Override
         public long getIssuedAt() {
            return issuedAt;
         }

         @Override
         public long getExpiresIn() {
            return expiresIn;
         }

         @Override
         public String getCode() {
            return code;
         }

         @Override
         public String getRedirectUri() {
            return redirect_uri;
         }

         @Override
         public String getClientCodeVerifier() {
            return verifier;
         }

         @Override
         public String getAudience() {
            return audience;
         }

         @Override
         public List<String> getApprovedScopes() {
            return approvedScopes;
         }

      };
   }

}

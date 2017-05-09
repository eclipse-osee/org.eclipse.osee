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
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ClientCredentialStorage extends AbstractDatabaseStorage<OAuthClientCredential> {

   private static final String SELECT_CLIENT_CREDENTIAL_BY_CLIENT_ID_AND_APPLICATION_ID =
      "SELECT * FROM osee_oauth_client_credential WHERE client_id= ? AND application_id = ?";

   private static final String SELECT_CLIENT_CREDENTIAL_BY_APPLICATION_ID =
      "SELECT * FROM osee_oauth_client_credential WHERE application_id = ?";

   private static final String SELECT_CLIENT_CREDENTIAL_BY_CLIENT_KEY =
      "SELECT * FROM osee_oauth_client_credential WHERE client_key = ?";

   private static final String INSERT_CLIENT_CREDENTIAL =
      "INSERT INTO osee_oauth_client_credential (client_id, application_id, subject_id, client_key, client_secret, client_cert) VALUES (?,?,?,?,?,?)";

   private static final String DELETE_CLIENT_CREDENTIAL_BY_CLIENT_ID =
      "DELETE FROM osee_oauth_client_credential WHERE client_id = ?";

   private static final String UPDATE_BY_CLIENT_CREDENTIAL =
      "UPDATE osee_oauth_client_credential SET subject_id = ?, client_key = ?, client_key = ?, client_secret = ?, client_cert = ? WHERE client_id = ? AND application_id = ?";

   public ClientCredentialStorage(Log logger, JdbcClient jdbcClient) {
      super(logger, jdbcClient);
   }

   @Override
   protected Object[] asInsert(OAuthClientCredential data) {
      return new Object[] {
         data.getClientId(),
         data.getApplicationId(),
         data.getSubjectId(),
         data.getClientKey(),
         asVarcharOrNull(data.getClientSecret()),
         asVarcharOrNull(asCertString(data.getClientCertificates()))};
   }

   @Override
   protected Object[] asUpdate(OAuthClientCredential data) {
      return new Object[] {
         data.getSubjectId(),
         data.getClientKey(),
         asVarcharOrNull(data.getClientSecret()),
         asVarcharOrNull(asCertString(data.getClientCertificates())),
         data.getClientId(),
         data.getApplicationId()};
   }

   @Override
   protected Object[] asDelete(OAuthClientCredential data) {
      return new Object[] {data.getClientId()};
   }

   public void update(OAuthClientCredential data) {
      updateItems(UPDATE_BY_CLIENT_CREDENTIAL, data);
   }

   public OAuthClientCredential getByClientKey(String clientKey) {
      return selectOneOrNull(SELECT_CLIENT_CREDENTIAL_BY_CLIENT_KEY, clientKey);
   }

   public OAuthClientCredential getByApplicationId(long applicationId) {
      return selectOneOrNull(SELECT_CLIENT_CREDENTIAL_BY_APPLICATION_ID, applicationId);
   }

   public OAuthClientCredential getByClientIdAndApplicationId(long clientId, long applicationId) {
      return selectOneOrNull(SELECT_CLIENT_CREDENTIAL_BY_CLIENT_ID_AND_APPLICATION_ID, clientId, applicationId);
   }

   public void insert(OAuthClientCredential data) {
      insertItems(INSERT_CLIENT_CREDENTIAL, data);
   }

   public void delete(OAuthClientCredential data) {
      deleteItems(DELETE_CLIENT_CREDENTIAL_BY_CLIENT_ID, data);
   }

   public void delete(Iterable<OAuthClientCredential> datas) {
      deleteItems(DELETE_CLIENT_CREDENTIAL_BY_CLIENT_ID, datas);
   }

   @Override
   protected OAuthClientCredential readData(JdbcStatement chStmt) {
      final long clientId = chStmt.getLong("client_id");
      final long applicationId = chStmt.getLong("application_id");
      final long subjectId = chStmt.getLong("subject_id");

      final String clientKey = chStmt.getString("client_key");
      final String clientSecret = chStmt.getString("client_secret");
      final String clientCert = chStmt.getString("client_cert");

      List<String> certs = Collections.fromString(clientCert, ";");
      return newCredential(clientId, applicationId, subjectId, clientKey, clientSecret, certs);
   }

   private String asCertString(List<String> certs) {
      return certs != null ? org.eclipse.osee.framework.jdk.core.util.Collections.toString(";", certs) : null;
   }

   public OAuthClientCredential newCredential(final long clientId, final long applicationId, final long subjectId, final String clientKey, final String clientSecret, final List<String> clientCert) {
      return new OAuthClientCredential() {

         @Override
         public long getClientId() {
            return clientId;
         }

         @Override
         public long getApplicationId() {
            return applicationId;
         }

         @Override
         public long getSubjectId() {
            return subjectId;
         }

         @Override
         public String getClientKey() {
            return clientKey;
         }

         @Override
         public String getClientSecret() {
            return clientSecret;
         }

         @Override
         public List<String> getClientCertificates() {
            return clientCert;
         }

      };
   }

}

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

package org.eclipse.osee.orcs.account.admin.internal.oauth;

import static org.eclipse.osee.framework.core.enums.OAuthOseeTypes.OAuthClient;
import com.google.common.io.ByteSource;
import java.util.Map;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.OAuthOseeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.jaxrs.server.security.OAuthClient;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class ClientStorage {

   private final OrcsApi orcsApi;
   private final BranchId storageBranch;

   public ClientStorage(OrcsApi orcsApi, BranchId storageBranch) {
      this.orcsApi = orcsApi;
      this.storageBranch = storageBranch;
   }

   private BranchId getBranch() {
      return storageBranch;
   }

   private QueryBuilder newQuery() {
      return orcsApi.getQueryFactory().fromBranch(getBranch());
   }

   private TransactionBuilder newTransaction(OseePrincipal principal, String comment) {
      UserId author = principal == null ? SystemUser.OseeSystem : UserId.valueOf(principal.getGuid());
      TransactionFactory transactionFactory = orcsApi.getTransactionFactory();
      return transactionFactory.createTransaction(getBranch(), author, comment);
   }

   public ResultSet<ArtifactReadable> getClientByApplicationId(ArtifactId applicationId) {
      return newQuery().andId(applicationId).getResults();
   }

   public ResultSet<ArtifactReadable> getClientByClientGuid(String guid) {
      return newQuery().andGuid(guid).getResults();
   }

   public ResultSet<ArtifactReadable> getClientByClientId(ArtifactId id) {
      return newQuery().andId(id).getResults();
   }

   public boolean exists(Long id) {
      return newQuery().andUuid(id).exists();
   }

   public ArtifactId insert(OseePrincipal principal, OAuthClient data) {
      TransactionBuilder tx = newTransaction(principal, "Create OAuth Client");
      ArtifactId artId =
         tx.createArtifact(OAuthClient, data.getApplicationName(), ArtifactId.valueOf(data.getClientUuid()));
      txSetClient(tx, artId, data);
      tx.commit();
      return artId;
   }

   public void update(OseePrincipal principal, OAuthClient data) {
      ArtifactId artId = ArtifactId.valueOf(data.getClientUuid());

      TransactionBuilder tx = newTransaction(principal, "Update OAuth Client");
      tx.setName(artId, data.getApplicationName());
      txSetClient(tx, artId, data);
      tx.commit();
   }

   private void txSetClient(TransactionBuilder tx, ArtifactId artId, OAuthClient data) {
      //@formatter:off
      tx.setSoleAttributeFromString(artId, CoreAttributeTypes.Description, data.getApplicationDescription());
      tx.setSoleAttributeFromString(artId, OAuthOseeTypes.OAuthClientWebsiteUri, data.getApplicationWebUri());
      tx.setSoleAttributeFromString(artId, OAuthOseeTypes.OAuthClientLogoUri, data.getApplicationLogoUri());

      tx.setSoleAttributeValue(artId, OAuthOseeTypes.OAuthClientIsConfidential, data.isConfidential());

      tx.setAttributesFromStrings(artId, OAuthOseeTypes.OAuthClientAuthorizedAudience, data.getRegisteredAudiences());
      tx.setAttributesFromStrings(artId, OAuthOseeTypes.OAuthClientAuthorizedGrantType, data.getAllowedGrantTypes());
      tx.setAttributesFromStrings(artId, OAuthOseeTypes.OAuthClientAuthorizedRedirectUri, data.getRedirectUris());
      tx.setAttributesFromStrings(artId, OAuthOseeTypes.OAuthClientAuthorizedScope, data.getRegisteredScopes());
      //@formatter:on

      ByteSource supplier = data.getApplicationLogoSupplier();
      if (supplier != null) {
         try {
            tx.setSoleAttributeValue(artId, CoreAttributeTypes.ImageContent, supplier.openStream());
         } catch (Exception ex) {
            throw new OseeCoreException(ex, "Error reading logo data for [%s]", artId);
         }
      }

      Map<String, String> props = data.getProperties();
      String json = JsonUtil.toJson(props);
      tx.setSoleAttributeValue(artId, OAuthOseeTypes.OAuthClientProperties, json);
   }

   public void delete(OseePrincipal principal, OAuthClient data) {
      ArtifactId artId = ArtifactId.valueOf(data.getClientUuid());

      TransactionBuilder tx = newTransaction(principal, "Delete OAuth Client");
      tx.deleteArtifact(artId);
      tx.commit();
   }

   public OAuthClient newClient(ArtifactReadable artifact, OAuthClientCredential credential) {
      return new ClientArtifact(artifact, credential, orcsApi.jaxRsApi());
   }
}
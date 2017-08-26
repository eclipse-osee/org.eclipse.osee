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

import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_CLIENT;
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_CLIENT_AUTHORIZED_AUDIENCE;
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_CLIENT_AUTHORIZED_GRANT_TYPE;
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_CLIENT_AUTHORIZED_REDIRECT_URI;
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_CLIENT_AUTHORIZED_SCOPE;
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_CLIENT_IS_CONFIDENTIAL;
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_CLIENT_LOGO_URI;
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_CLIENT_PROPERTIES;
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_CLIENT_WEBSITE_URI;
import static org.eclipse.osee.orcs.account.admin.internal.oauth.OAuthTypes.OAUTH_TYPES;
import com.google.common.io.InputSupplier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.server.security.OAuthClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public class ClientStorage {

   private final Log logger;
   private final GsonBuilder builder;
   private final OrcsApi orcsApi;
   private final BranchId storageBranch;

   public ClientStorage(Log logger, GsonBuilder builder, OrcsApi orcsApi, BranchId storageBranch) {
      super();
      this.logger = logger;
      this.builder = builder;
      this.orcsApi = orcsApi;
      this.storageBranch = storageBranch;
   }

   private BranchId getBranch() {
      return storageBranch;
   }

   private int idToInt(long uuid) {
      return Long.valueOf(uuid).intValue();
   }

   private QueryBuilder newQuery() {
      QueryFactory queryFactory = orcsApi.getQueryFactory();
      return queryFactory.fromBranch(getBranch());
   }

   private ArtifactReadable getAuthorById(long authorId) {
      ArtifactReadable author;
      if (authorId > -1L) {
         int artId = idToInt(authorId);
         author = newQuery().andUuid(artId).getResults().getExactlyOne();
      } else {
         author = getSystemUser();
      }
      return author;
   }

   @SuppressWarnings("unchecked")
   private ArtifactReadable getSystemUser() {
      return newQuery().andId(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   private TransactionBuilder newTransaction(OseePrincipal principal, String comment) {
      long authorId = principal != null ? principal.getGuid() : -1L;
      ArtifactReadable author = getAuthorById(authorId);

      TransactionFactory transactionFactory = orcsApi.getTransactionFactory();
      return transactionFactory.createTransaction(getBranch(), author, comment);
   }

   public ResultSet<ArtifactReadable> getClientByApplicationId(long applicationId) {
      int id = idToInt(applicationId);
      return newQuery().andIsOfType(OAUTH_CLIENT).andUuid(id).getResults();
   }

   public ResultSet<ArtifactReadable> getClientByClientGuid(String guid) {
      return newQuery().andIsOfType(OAUTH_CLIENT).andGuid(guid).getResults();
   }

   public ResultSet<ArtifactReadable> getClientByClientUuid(Long uuid) {
      return newQuery().andIsOfType(OAUTH_CLIENT).andUuid(uuid).getResults();
   }

   public boolean exists(Long uuid) {
      return newQuery().andIsOfType(OAUTH_CLIENT).andUuid(uuid).getCount() > 0;
   }

   public ArtifactId insert(OseePrincipal principal, OAuthClient data) {
      TransactionBuilder tx = newTransaction(principal, "Create OAuth Client");
      ArtifactId artId = tx.createArtifact(OAUTH_CLIENT, data.getApplicationName(), data.getGuid());
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
      tx.setSoleAttributeFromString(artId, OAUTH_CLIENT_WEBSITE_URI, data.getApplicationWebUri());
      tx.setSoleAttributeFromString(artId, OAUTH_CLIENT_LOGO_URI, data.getApplicationLogoUri());

      tx.setSoleAttributeValue(artId, OAUTH_CLIENT_IS_CONFIDENTIAL, data.isConfidential());

      tx.setAttributesFromStrings(artId, OAUTH_CLIENT_AUTHORIZED_AUDIENCE, data.getRegisteredAudiences());
      tx.setAttributesFromStrings(artId, OAUTH_CLIENT_AUTHORIZED_GRANT_TYPE, data.getAllowedGrantTypes());
      tx.setAttributesFromStrings(artId, OAUTH_CLIENT_AUTHORIZED_REDIRECT_URI, data.getRedirectUris());
      tx.setAttributesFromStrings(artId, OAUTH_CLIENT_AUTHORIZED_SCOPE, data.getRegisteredScopes());
      //@formatter:on

      InputSupplier<InputStream> supplier = data.getApplicationLogoSupplier();
      if (supplier != null) {
         try {
            tx.setAttributesFromValues(artId, CoreAttributeTypes.ImageContent, supplier.getInput());
         } catch (Exception ex) {
            throw new OseeCoreException(ex, "Error reading logo data for [%s]", artId);
         }
      }

      Map<String, String> props = data.getProperties();
      Gson gson = builder.create();
      String json = gson.toJson(props);
      tx.setSoleAttributeValue(artId, OAUTH_CLIENT_PROPERTIES, json);
   }

   public void delete(OseePrincipal principal, OAuthClient data) {
      ArtifactId artId = ArtifactId.valueOf(data.getClientUuid());

      TransactionBuilder tx = newTransaction(principal, "Delete OAuth Client");
      tx.deleteArtifact(artId);
      tx.commit();
   }

   private ResultSet<ArtifactReadable> getOAuthTypesDefinition() throws OseeCoreException {
      return newQuery().andUuid(OAUTH_TYPES.getUuid()).andTypeEquals(OAUTH_TYPES.getArtifactType()).getResults();
   }

   public ArtifactId storeTypes(InputSupplier<? extends InputStream> resource) {
      TransactionBuilder tx = newTransaction(null, "Initialize OAuth Type Definitions");
      ArtifactId artifactId = tx.createArtifact(OAUTH_TYPES);
      InputStream stream = null;
      try {
         stream = resource.getInput();
         tx.setSoleAttributeFromStream(artifactId, CoreAttributeTypes.UriGeneralStringData, stream);
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      } finally {
         Lib.close(stream);
      }
      tx.commit();

      reloadTypes();
      return artifactId;
   }

   private void reloadTypes() {
      orcsApi.getOrcsTypes().invalidateAll();
   }

   public boolean typesExist() {
      boolean result = false;
      try {
         result = !getOAuthTypesDefinition().isEmpty();
      } catch (OseeCoreException ex) {
         logger.warn(ex, "Error checking for OAuth Types");
      }
      return result;
   }

   public OAuthClient newClient(ArtifactReadable artifact, OAuthClientCredential credential) {
      return new ClientArtifact(builder, artifact, credential);
   }

}

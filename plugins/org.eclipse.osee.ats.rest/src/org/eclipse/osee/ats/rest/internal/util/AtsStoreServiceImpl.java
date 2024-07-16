/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreServiceImpl implements IAtsStoreService {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;

   private final JdbcService jdbcService;

   public AtsStoreServiceImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
      this.jdbcService = atsApi.getJdbcService();
   }

   @Override
   public JdbcService getJdbcService() {
      return jdbcService;
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, AtsUser asUser) {
      return createAtsChangeSet(comment, atsApi.getAtsBranch(), asUser);

   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, BranchToken branch, AtsUser asUser) {
      return new AtsChangeSet(atsApi, atsApi.getAttributeResolver(), orcsApi, atsApi.getLogFactory(), comment, asUser,
         branch);
   }

   public QueryBuilder getQuery() {
      return orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch());
   }

   @Override
   public List<IAtsWorkItem> reload(Collection<IAtsWorkItem> inWorkWorkflows) {
      List<IAtsWorkItem> workItems = new ArrayList<>(inWorkWorkflows.size());
      List<ArtifactId> ids = AtsObjects.toArtifactIds(inWorkWorkflows);
      Iterator<ArtifactReadable> arts = getQuery().andIds(ids).getResults().iterator();
      while (arts.hasNext()) {
         workItems.add(atsApi.getWorkItemService().getWorkItem(arts.next()));
      }
      return workItems;
   }

   @Override
   public boolean isDeleted(IAtsObject atsObject) {
      return ((ArtifactReadable) atsApi.getQueryService().getArtifact(atsObject)).isDeleted();
   }

   @Override
   public boolean isAttributeTypeValid(IAtsObject atsObject, AttributeTypeToken attributeType) {
      return isAttributeTypeValid(atsObject.getStoreObject(), attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(ArtifactId artifact, AttributeTypeToken attributeType) {
      return ((ArtifactReadable) artifact).isAttributeTypeValid(attributeType);
   }

   @Override
   public ArtifactTypeToken getArtifactType(ArtifactId artifact) {
      if (artifact instanceof ArtifactReadable) {
         return ((ArtifactReadable) artifact).getArtifactType();
      }
      return getQuery().andId(artifact).includeDeletedArtifacts().asArtifactToken().getArtifactType();
   }

   @Override
   public ArtifactTypeToken getArtifactType(ArtifactId artifact, BranchId branch) {
      if (artifact instanceof ArtifactReadable) {
         return ((ArtifactReadable) artifact).getArtifactType();
      }
      return orcsApi.getQueryFactory().fromBranch(branch).andId(
         artifact).includeDeletedArtifacts().asArtifactToken().getArtifactType();
   }

   @Override
   public void executeChangeSet(String comment, IAtsObject atsObject) {
      executeChangeSet(comment, Collections.singleton(atsObject));
   }

   @Override
   public void executeChangeSet(String comment, Collection<? extends IAtsObject> atsObjects) {
      IAtsChangeSet changes = createAtsChangeSet(comment, atsApi.getUserService().getCurrentUser());
      for (IAtsObject atsObject : atsObjects) {
         changes.add(atsObject);
      }
      changes.execute();
   }

   @Override
   public Collection<AttributeTypeGeneric<?>> getAttributeTypes() {
      return orcsApi.tokenService().getAttributeTypes();
   }

   /**
    * return false cause server always loads workItems fresh
    */
   @Override
   public boolean isChangedInDb(IAtsWorkItem workItem) {
      return false;
   }

   @Override
   public ArtifactTypeToken getArtifactType(IAtsObject atsObject) {
      return getArtifactType(atsApi.getQueryService().getArtifact(atsObject.getStoreObject()));
   }

   @Override
   public void clearCaches(IAtsWorkItem workItem) {
      ((WorkItem) workItem).clearCaches();
   }

   @Override
   public Result setTransactionAssociatedArtifact(TransactionId trans, IAtsTeamWorkflow teamWf) {
      orcsApi.getTransactionFactory().setTransactionCommitArtifact(trans, teamWf.getStoreObject());
      return Result.TrueResult;
   }

   @Override
   public TransactionId getTransactionId(IAtsWorkItem workItem) {
      TransactionId transId = TransactionId.SENTINEL;
      ArtifactId artifact = atsApi.getQueryService().getArtifact(workItem.getId());
      if (artifact instanceof ArtifactReadable) {
         transId = ((ArtifactReadable) artifact).getTransaction();
      }
      return transId;
   }

   @Override
   public boolean isDeleted(ArtifactId artifact) {
      return ((ArtifactReadable) atsApi.getQueryService().getArtifact(artifact)).isDeleted();
   }

   @Override
   public CustomizeData getCustomizationByGuid(String customize_guid) {
      CustomizeData cust = null;
      ArtifactReadable customizeStoreArt = getQuery().and(CoreAttributeTypes.XViewerCustomization, customize_guid,
         QueryOption.CONTAINS_MATCH_OPTIONS).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      if (customizeStoreArt.isValid()) {
         for (String custXml : atsApi.getAttributeResolver().getAttributesToStringList(customizeStoreArt,
            CoreAttributeTypes.XViewerCustomization)) {
            if (custXml.contains(customize_guid)) {
               cust = new CustomizeData(custXml);
               break;
            }
         }
      }
      return cust;
   }

   @Override
   public boolean isProductionDb() {
      return jdbcService.getClient().getConfig().isProduction();
   }

   @Override
   public boolean isHistorical(IAtsObject atsObject) {
      return ((ArtifactReadable) atsApi.getQueryService().getArtifact(atsObject)).isHistorical();
   }

   @Override
   public boolean isHistorical(ArtifactId artifact) {
      return ((ArtifactReadable) atsApi.getQueryService().getArtifact(artifact)).isHistorical();
   }

   @Override
   public boolean isReadOnly(IAtsWorkItem workItem) {
      throw new UnsupportedOperationException("unsupported on server");
   }

   @Override
   public boolean isAccessControlWrite(IAtsWorkItem workItem) {
      return orcsApi.getAccessControlService().hasArtifactPermission(workItem.getStoreObject(), PermissionEnum.WRITE,
         null).isSuccess();
   }

   @Override
   public void reloadArts(Collection<ArtifactToken> artifacts) {
      // do needed on server, but don't exception
   }

   @Override
   public boolean isIdeClient() {
      return false;
   }

   @Override
   public Collection<ArtifactToken> getDescendants(ArtifactToken art) {
      throw new UnsupportedOperationException("unsupported on server");
   }

   @Override
   public String getSafeName(ArtifactToken art) {
      BranchToken branch = art.getBranch();
      if (branch.isInvalid() && art instanceof ArtifactReadable) {
         branch = ((ArtifactReadable) art).getBranch();
      }
      if (branch.isInvalid()) {
         throw new OseeArgumentException("Can't determine branch from art [%s]", art.toStringWithId());
      }
      return ((ArtifactReadable) atsApi.getQueryService().getArtifact(art, branch,
         DeletionFlag.INCLUDE_DELETED)).getSafeName();
   }

   @Override
   public String getSafeName(ArtifactToken art, BranchId branch) {
      if (branch.isInvalid()) {
         throw new OseeArgumentException("Can't determine branch from art [%s]", art.toStringWithId());
      }
      return ((ArtifactReadable) atsApi.getQueryService().getArtifact(art, branch,
         DeletionFlag.INCLUDE_DELETED)).getSafeName();
   }

   @Override
   public boolean isOfType(ArtifactId artifact, ArtifactTypeToken artType) {
      return ((ArtifactReadable) atsApi.getQueryService().getArtifact(artifact)).isOfType(artType);
   }

   /**
    * return false cause server always loads workItems fresh
    */
   @Override
   public boolean isChangedInDb(ArtifactId workItem) {
      return false;
   }

   @Override
   public XResultData clearAtsCachesAllServers() {
      throw new UnsupportedOperationException("unsupported on server");
   }

   @Override
   public void purgeArtifacts(List<ArtifactToken> artifacts) {
      throw new UnsupportedOperationException("unsupported on server");
   }

   @Override
   public void deleteArtifacts(List<ArtifactToken> artifacts) {
      IAtsChangeSet changes = atsApi.createChangeSet("Delete Artifacts");
      for (ArtifactToken art : artifacts) {
         if (!atsApi.getStoreService().isDeleted(art)) {
            changes.deleteArtifact(art);
         }
      }
      changes.executeIfNeeded();
   }

}
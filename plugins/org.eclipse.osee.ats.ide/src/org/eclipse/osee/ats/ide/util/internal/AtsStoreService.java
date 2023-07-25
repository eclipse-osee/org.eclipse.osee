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

package org.eclipse.osee.ats.ide.util.internal;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsDeleteManager;
import org.eclipse.osee.ats.ide.util.AtsDeleteManager.DeleteOption;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreService implements IAtsStoreService {
   private final IAtsUserService userService;
   private final JdbcService jdbcService;
   public final AtsApi atsApi;

   public AtsStoreService(AtsApi atsApi, IAtsUserService userService, JdbcService jdbcService) {
      this.atsApi = atsApi;
      this.userService = userService;
      this.jdbcService = jdbcService;
   }

   @Override
   public JdbcService getJdbcService() {
      return jdbcService;
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, AtsUser asUser) {
      return new AtsChangeSet(comment, atsApi.getAtsBranch(), asUser);
   }

   @Override
   public List<IAtsWorkItem> reload(Collection<IAtsWorkItem> workItems) {
      List<IAtsWorkItem> results = new ArrayList<>();
      try {
         List<Artifact> artifacts = new LinkedList<>();
         for (IAtsWorkItem workItem : workItems) {
            if (workItem.getStoreObject() != null && workItem.getStoreObject() instanceof Artifact) {
               artifacts.add((Artifact) atsApi.getQueryService().getArtifact(workItem));
            }
         }
         for (Artifact art : ArtifactQuery.reloadArtifacts(artifacts)) {
            if (!art.isDeleted()) {
               IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(art);
               if (workItem != null) {
                  results.add(workItem);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return results;
   }

   @Override
   public boolean isDeleted(IAtsObject atsObject) {
      if (atsObject.getStoreObject() instanceof Artifact) {
         return ((Artifact) atsObject.getStoreObject()).isDeleted();
      }
      Artifact art = ((Artifact) atsApi.getQueryService().getArtifact(atsObject));
      if (art != null) {
         return art.isDeleted();
      }
      return true;
   }

   @Override
   public boolean isAttributeTypeValid(IAtsObject atsObject, AttributeTypeToken attributeType) {
      return isAttributeTypeValid(atsApi.getQueryService().getArtifact(atsObject), attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(ArtifactId artifact, AttributeTypeToken attributeType) {
      return atsApi.getQueryService().getArtifact(artifact).isAttributeTypeValid(attributeType);
   }

   @Override
   public ArtifactTypeToken getArtifactType(ArtifactId artifact) {
      if (artifact instanceof Artifact) {
         return atsApi.getQueryService().getArtifact(artifact).getArtifactType();
      }
      return ArtifactQuery.getArtifactFromId(artifact, atsApi.getAtsBranch(),
         DeletionFlag.INCLUDE_DELETED).getArtifactType();
   }

   @Override
   public ArtifactTypeToken getArtifactType(ArtifactId artifact, BranchId branch) {
      if (artifact instanceof Artifact) {
         return atsApi.getQueryService().getArtifact(artifact, branch).getArtifactType();
      }
      return ArtifactQuery.getArtifactFromId(artifact, branch, DeletionFlag.INCLUDE_DELETED).getArtifactType();
   }

   @Override
   public void executeChangeSet(String comment, IAtsObject atsObject) {
      executeChangeSet(comment, Collections.singleton(atsObject));
   }

   @Override
   public void executeChangeSet(String comment, Collection<? extends IAtsObject> atsObjects) {
      IAtsChangeSet changes = createAtsChangeSet(comment, userService.getCurrentUser());
      for (IAtsObject atsObject : atsObjects) {
         changes.add(atsObject);
      }
      changes.execute();
   }

   @Override
   public Collection<AttributeTypeGeneric<?>> getAttributeTypes() {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(AttributeTypeManager.getAllTypes());
   }

   @Override
   public boolean isChangedInDb(IAtsWorkItem workItem) {
      return ArtifactQuery.isArtifactChangedViaTransaction((Artifact) atsApi.getQueryService().getArtifact(workItem));
   }

   @Override
   public boolean isChangedInDb(ArtifactId artifact) {
      return ArtifactQuery.isArtifactChangedViaTransaction((Artifact) atsApi.getQueryService().getArtifact(artifact));
   }

   @Override
   public ArtifactTypeToken getArtifactType(IAtsObject atsObject) {
      return getArtifactType(atsApi.getQueryService().getArtifact(atsObject));
   }

   @Override
   public Result setTransactionAssociatedArtifact(TransactionId trans, IAtsTeamWorkflow teamWf) {
      throw new UnsupportedOperationException();
   }

   @Override
   public TransactionId getTransactionId(IAtsWorkItem workItem) {
      TransactionId transId = TransactionId.SENTINEL;
      if (workItem.getStoreObject() instanceof Artifact) {
         transId = ((Artifact) workItem.getStoreObject()).getTransaction();
      } else {
         ArtifactId artifact = atsApi.getQueryService().getArtifact(workItem.getStoreObject());
         if (artifact instanceof Artifact) {
            transId = ((Artifact) artifact).getTransaction();
         }
      }
      return transId;
   }

   @Override
   public boolean isDeleted(ArtifactId artifact) {
      if (artifact instanceof Artifact) {
         return ((Artifact) artifact).isDeleted();
      }
      ArtifactToken art = atsApi.getQueryService().getArtifact(artifact);
      if (art != null) {
         return ((Artifact) atsApi.getQueryService().getArtifact(art)).isDeleted();
      }
      return true;
   }

   @Override
   public CustomizeData getCustomizationByGuid(String customize_guid) {
      CustomizeData cust = null;
      List<Artifact> customizeStoreArt = ArtifactQuery.getArtifactListFromAttributeKeywords(atsApi.getAtsBranch(),
         customize_guid, false, DeletionFlag.EXCLUDE_DELETED, true, CoreAttributeTypes.XViewerCustomization);
      if (!customizeStoreArt.isEmpty()) {
         for (String custXml : customizeStoreArt.iterator().next().getAttributesToStringList(
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
      return ClientSessionManager.isProductionDataStore();
   }

   @Override
   public boolean isHistorical(IAtsObject atsObject) {
      return ((Artifact) atsApi.getQueryService().getArtifact(atsObject)).isHistorical();
   }

   @Override
   public boolean isHistorical(ArtifactId artifact) {
      return ((Artifact) atsApi.getQueryService().getArtifact(artifact)).isHistorical();
   }

   @Override
   public void clearCaches(IAtsWorkItem workItem) {
      workItem.clearCaches();
   }

   @Override
   public boolean isReadOnly(IAtsWorkItem workItem) {
      return ((Artifact) workItem.getStoreObject()).isReadOnly();
   }

   @Override
   public boolean isAccessControlWrite(IAtsWorkItem workItem) {
      return ((AbstractWorkflowArtifact) workItem.getStoreObject()).isAccessControlWrite();
   }

   @Override
   public void reloadArts(Collection<ArtifactToken> artifacts) {
      ArtifactQuery.reloadArtifacts(artifacts);
   }

   @Override
   public boolean isIdeClient() {
      return true;
   }

   @Override
   public Collection<ArtifactToken> getDescendants(ArtifactToken art) {
      Set<ArtifactToken> arts = new HashSet<>();
      Artifact artifact = null;
      if (art instanceof Artifact) {
         artifact = (Artifact) art;
      } else {
         artifact = (Artifact) atsApi.getQueryService().getArtifact(art);
      }
      arts.add(artifact);
      for (Artifact child : artifact.getChildren()) {
         arts.addAll(getDescendants(child));
      }
      return arts;
   }

   @Override
   public String getSafeName(ArtifactToken art) {
      String safeName = "Unknown";
      if (art.isInvalid()) {
         safeName = "Sentinal";
      } else if (art instanceof Artifact) {
         safeName = ((Artifact) art).getName();
      } else {
         Artifact artifact =
            (Artifact) atsApi.getQueryService().getArtifact(art, atsApi.getAtsBranch(), DeletionFlag.INCLUDE_DELETED);
         if (artifact != null) {
            safeName = artifact.getName();
         }
      }
      return safeName;
   }

   @Override
   public String getSafeName(ArtifactToken art, BranchId branch) {
      String safeName = "unknown";
      if (art.isInvalid()) {
         safeName = "Sentinal";
      } else if (art instanceof Artifact) {
         safeName = ((Artifact) art).getName();
      } else if (branch.isInvalid()) {
         throw new OseeArgumentException("Can't determine branch from art [%s]", art.toStringWithId());
      } else {
         safeName =
            ((Artifact) atsApi.getQueryService().getArtifact(art, branch, DeletionFlag.INCLUDE_DELETED)).getSafeName();
      }
      return safeName;
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, BranchToken branch, AtsUser asUser) {
      return new AtsChangeSet(comment, branch, asUser);
   }

   @Override
   public boolean isOfType(ArtifactId art, ArtifactTypeToken artType) {
      Artifact artifact = null;
      if (art instanceof ArtifactToken) {
         BranchId branch = ((ArtifactToken) art).getBranch();
         if (branch.isValid()) {
            artifact = (Artifact) atsApi.getQueryService().getArtifact(art, branch);
         }
      }
      if (artifact == null) {
         artifact = (Artifact) atsApi.getQueryService().getArtifact(art);
      }
      return artifact.isOfType(artType);
   }

   @Override
   public XResultData clearAtsCachesAllServers() {
      XResultData rd = new XResultData();
      rd.log("Update Ats Caches All Servers");
      // Retrieve servers from OseeInfo
      String serversStr = OseeInfo.getValue(OseeProperties.OSEE_HEALTH_SERVERS_KEY);
      List<String> servers = new ArrayList<>();

      serversStr = serversStr.replaceAll(" ", "");
      for (String server : serversStr.split(",")) {
         servers.add(server);
      }
      if (servers.isEmpty()) {
         rd.logf("No %s configured.  ATS cache is not updated.  ", OseeProperties.OSEE_HEALTH_SERVERS_KEY);
      } else {
         rd.log("\n");
         for (String server : servers) {
            atsApi.getServerEndpoints().getConfigEndpoint().requestCacheReload();
            try {
               WebTarget target = AtsApiService.get().jaxRsApi().newTargetUrl(
                  String.format("http://%s%s", server, "/ats/config/clearcache"));
               Response response = target.request().get();
               if (response.getStatus() == HttpURLConnection.HTTP_OK || response.getStatus() == HttpURLConnection.HTTP_ACCEPTED) {
                  rd.logf("ATS server %s cache update was successful.\n", server);
               } else {
                  rd.logf("ERROR: ATS server %s cache update was not successful.\n", server);
               }
            } catch (Exception ex) {
               rd.logf("ERROR: ATS server %s cache update exception %s.\n", server, ex.getLocalizedMessage());
            }
         }
      }
      return rd;
   }

   @Override
   public void purgeArtifacts(List<ArtifactToken> artifacts) {
      Operations.executeWorkAndCheckStatus(
         new PurgeArtifacts(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(artifacts)));
   }

   @Override
   public void deleteArtifacts(List<ArtifactToken> artifacts) {
      AtsDeleteManager.handleDeletePurgeAtsObject(
         org.eclipse.osee.framework.jdk.core.util.Collections.castAll(artifacts), false, DeleteOption.Delete);
   }

}

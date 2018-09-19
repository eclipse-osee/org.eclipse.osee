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
package org.eclipse.osee.ats.util.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreService implements IAtsStoreService {
   private final IAtsUserService userService;
   private final JdbcService jdbcService;
   private final AtsApi atsApi;

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
   public IAtsChangeSet createAtsChangeSet(String comment, IAtsUser asUser) {
      return new AtsChangeSet(comment, asUser);
   }

   @Override
   public List<IAtsWorkItem> reload(Collection<IAtsWorkItem> workItems) {
      List<IAtsWorkItem> results = new ArrayList<>();
      try {
         List<Artifact> artifacts = new LinkedList<Artifact>();
         for (IAtsWorkItem workItem : workItems) {
            if (workItem.getStoreObject() != null && workItem.getStoreObject() instanceof Artifact) {
               artifacts.add((Artifact) workItem.getStoreObject());
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
         // do nothing
      }
      return results;
   }

   @Override
   public boolean isDeleted(IAtsObject atsObject) {
      return ((Artifact) AtsClientService.get().getQueryService().getArtifact(atsObject)).isDeleted();
   }

   /**
    * Uses artifact type inheritance to retrieve all TeamWorkflow artifact types
    */
   @Override
   public Set<IArtifactType> getTeamWorkflowArtifactTypes() {
      Set<IArtifactType> artifactTypes = new HashSet<>();
      getTeamWorkflowArtifactTypesRecursive(ArtifactTypeManager.getType(AtsArtifactTypes.TeamWorkflow), artifactTypes);
      return artifactTypes;
   }

   private static void getTeamWorkflowArtifactTypesRecursive(ArtifactType artifactType, Set<IArtifactType> allArtifactTypes) {
      allArtifactTypes.add(artifactType);
      for (IArtifactType child : artifactType.getFirstLevelDescendantTypes()) {
         getTeamWorkflowArtifactTypesRecursive(ArtifactTypeManager.getType(child), allArtifactTypes);
      }
   }

   @Override
   public boolean isAttributeTypeValid(IAtsObject atsObject, AttributeTypeId attributeType) {
      return isAttributeTypeValid(AtsClientService.get().getQueryService().getArtifact(atsObject), attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(ArtifactId artifact, AttributeTypeId attributeType) {
      return ((Artifact) artifact).isAttributeTypeValid(attributeType);
   }

   @Override
   public AttributeTypeId getAttributeType(String attrTypeName) {
      return AttributeTypeManager.getType(attrTypeName);
   }

   @Override
   public IArtifactType getArtifactType(ArtifactId artifact) {
      if (artifact instanceof Artifact) {
         return ((Artifact) artifact).getArtifactType();
      }
      return ArtifactQuery.getArtifactTokenFromId(atsApi.getAtsBranch(), artifact).getArtifactTypeId();
   }

   @Override
   public boolean isDateType(AttributeTypeId attributeType) {
      return AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType);
   }

   @Override
   public boolean isOfType(ArtifactId artifact, ArtifactTypeId... artifactType) {
      return ((Artifact) artifact).isOfType(artifactType);
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
   public IArtifactType getArtifactType(Long artTypeId) {
      return ArtifactTypeManager.getType(artTypeId);
   }

   @Override
   public Collection<AttributeTypeToken> getAttributeTypes() {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(AttributeTypeManager.getAllTypes());
   }

   @Override
   public boolean isChangedInDb(IAtsWorkItem workItem) {
      return ArtifactQuery.isArtifactChangedViaTransaction((Artifact) workItem.getStoreObject());
   }

   @Override
   public IArtifactType getArtifactType(IAtsObject atsObject) {
      return getArtifactType(AtsClientService.get().getQueryService().getArtifact(atsObject));
   }

   @Override
   public boolean isOfType(IAtsObject atsObject, IArtifactType artifactType) {
      return isOfType(AtsClientService.get().getQueryService().getArtifact(atsObject), artifactType);
   }

   @Override
   public boolean isArtifactTypeInheritsFrom(IArtifactType artifactType, IArtifactType baseArtifactType) {
      return ArtifactTypeManager.inheritsFrom(artifactType, baseArtifactType);
   }

   @Override
   public AttributeTypeToken getAttributeType(Long attrTypeId) {
      return AttributeTypeManager.getTypeById(attrTypeId);
   }

   @Override
   public Result setTransactionAssociatedArtifact(TransactionId trans, IAtsTeamWorkflow teamWf) {
      throw new UnsupportedOperationException();
   }

   @Override
   public TransactionId getTransactionId(IAtsWorkItem workItem) {
      TransactionId transId = TransactionId.SENTINEL;
      ArtifactId artifact = atsApi.getQueryService().getArtifact(workItem.getStoreObject());
      if (artifact instanceof Artifact) {
         transId = ((Artifact) artifact).getTransaction();
      }
      return transId;
   }

   @Override
   public boolean isDeleted(ArtifactId artifact) {
      ArtifactToken art = atsApi.getQueryService().getArtifact(artifact);
      if (art != null) {
         return ((Artifact) art).isDeleted();
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
      return ((Artifact) AtsClientService.get().getQueryService().getArtifact(atsObject)).isHistorical();
   }

   @Override
   public void clearCaches(IAtsWorkItem workItem) {
      ((WorkItem) workItem).clearCaches();
      ((AbstractWorkflowArtifact) workItem.getStoreObject()).clearCaches();
   }
}
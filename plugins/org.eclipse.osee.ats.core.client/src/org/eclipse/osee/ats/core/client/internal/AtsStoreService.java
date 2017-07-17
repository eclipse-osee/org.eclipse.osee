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
package org.eclipse.osee.ats.core.client.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.store.AtsChangeSet;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   private final IAtsWorkItemFactory workItemFactory;
   private final IAtsUserService userService;
   private final JdbcService jdbcService;

   public AtsStoreService(IAtsWorkItemFactory workItemFactory, IAtsUserService userService, JdbcService jdbcService) {
      this.workItemFactory = workItemFactory;
      this.userService = userService;
      this.jdbcService = jdbcService;
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
               IAtsWorkItem workItem = workItemFactory.getWorkItem(art);
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
      return AtsClientService.get().getArtifact(atsObject).isDeleted();
   }

   @Override
   public String getGuid(IAtsObject atsObject) {
      return AtsClientService.get().getArtifact(atsObject).getGuid();
   }

   /**
    * Uses artifact type inheritance to retrieve all TeamWorkflow artifact types
    */
   @Override
   public Set<IArtifactType> getTeamWorkflowArtifactTypes() throws OseeCoreException {
      Set<IArtifactType> artifactTypes = new HashSet<>();
      getTeamWorkflowArtifactTypesRecursive(ArtifactTypeManager.getType(AtsArtifactTypes.TeamWorkflow), artifactTypes);
      return artifactTypes;
   }

   private static void getTeamWorkflowArtifactTypesRecursive(ArtifactType artifactType, Set<IArtifactType> allArtifactTypes) throws OseeCoreException {
      allArtifactTypes.add(artifactType);
      for (IArtifactType child : artifactType.getFirstLevelDescendantTypes()) {
         getTeamWorkflowArtifactTypesRecursive(ArtifactTypeManager.getType(child), allArtifactTypes);
      }
   }

   @Override
   public boolean isAttributeTypeValid(IAtsObject atsObject, AttributeTypeId attributeType) {
      return isAttributeTypeValid(AtsClientService.get().getArtifact(atsObject), attributeType);
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
      return ((Artifact) artifact).getArtifactType();
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
      return ArtifactTypeManager.getTypeByGuid(artTypeId);
   }

   @Override
   public Map<Long, IArtifactType> getArtifactTypes(Collection<Long> artIds) {
      Map<Long, IArtifactType> artIdToType = new HashMap<>();
      jdbcService.getClient().runQuery(
         stmt -> artIdToType.put(stmt.getLong("art_id"), getArtifactType(stmt.getLong("art_type_id"))), String.format(
            ART_TYPE_FROM_ID_QUERY, org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", artIds)));
      return artIdToType;
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
      return getArtifactType(AtsClientService.get().getArtifact(atsObject));
   }

   @Override
   public boolean isOfType(IAtsObject atsObject, IArtifactType artifactType) {
      return isOfType(AtsClientService.get().getArtifact(atsObject), artifactType);
   }

   @Override
   public void clearCaches(IAtsWorkItem workItem) {
      ((WorkItem) workItem).clearCaches();
   }

   @Override
   public boolean isArtifactTypeInheritsFrom(IArtifactType artifactType, IArtifactType baseArtifactType) {
      return ArtifactTypeManager.inheritsFrom(artifactType, baseArtifactType);
   }

   @Override
   public AttributeTypeId getAttributeType(Long attrTypeId) {
      return AttributeTypeManager.getTypeByGuid(attrTypeId);
   }

   @Override
   public Result setTransactionAssociatedArtifact(TransactionId trans, IAtsTeamWorkflow teamWf) {
      throw new UnsupportedOperationException();
   }
}
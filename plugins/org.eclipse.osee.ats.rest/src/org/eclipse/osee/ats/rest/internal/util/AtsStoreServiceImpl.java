/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsStoreServiceImpl implements IAtsStoreService {

   private final IAttributeResolver attributeResolver;
   private final IAtsStateFactory stateFactory;
   private final IAtsLogFactory logFactory;
   private final IAtsNotifier notifier;
   private final IAtsServer atsServer;
   private final JdbcService jdbcService;

   public AtsStoreServiceImpl(IAttributeResolver attributeResolver, IAtsServer atsServer, IAtsStateFactory stateFactory, IAtsLogFactory logFactory, IAtsNotifier notifier, JdbcService jdbcService) {
      this.atsServer = atsServer;
      this.attributeResolver = attributeResolver;
      this.logFactory = logFactory;
      this.stateFactory = stateFactory;
      this.notifier = notifier;
      this.jdbcService = jdbcService;
   }

   @Override
   public IAtsChangeSet createAtsChangeSet(String comment, IAtsUser asUser) {
      return new AtsChangeSet(atsServer, attributeResolver, atsServer.getOrcsApi(), stateFactory, logFactory, comment,
         asUser, notifier);
   }

   @Override
   public List<IAtsWorkItem> reload(Collection<IAtsWorkItem> inWorkWorkflows) {
      List<IAtsWorkItem> workItems = new ArrayList<>(inWorkWorkflows.size());
      List<String> guids = AtsObjects.toGuids(inWorkWorkflows);
      Iterator<ArtifactReadable> arts =
         atsServer.getOrcsApi().getQueryFactory().fromBranch(atsServer.getAtsBranch()).andGuids(
            guids).getResults().iterator();
      while (arts.hasNext()) {
         workItems.add(atsServer.getWorkItemFactory().getWorkItem(arts.next()));
      }
      return workItems;
   }

   @Override
   public boolean isDeleted(IAtsObject atsObject) {
      return atsServer.getArtifact(atsObject).isDeleted();
   }

   @Override
   public String getGuid(IAtsObject atsObject) {
      return atsServer.getArtifact(atsObject).getGuid();
   }

   /**
    * Uses artifact type inheritance to retrieve all TeamWorkflow artifact types
    */
   @Override
   public Set<IArtifactType> getTeamWorkflowArtifactTypes() throws OseeCoreException {
      Set<IArtifactType> artifactTypes = new HashSet<>();
      artifactTypes.addAll(
         atsServer.getOrcsApi().getOrcsTypes().getArtifactTypes().getAllDescendantTypes(AtsArtifactTypes.TeamWorkflow));
      return artifactTypes;
   }

   @Override
   public boolean isAttributeTypeValid(IAtsObject atsObject, AttributeTypeId attributeType) {
      return isAttributeTypeValid(atsObject.getStoreObject(), attributeType);
   }

   @Override
   public boolean isAttributeTypeValid(ArtifactId artifact, AttributeTypeId attributeType) {
      return ((ArtifactReadable) artifact).isAttributeTypeValid(attributeType);
   }

   @Override
   public AttributeTypeId getAttributeType(String attrTypeName) {
      return atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().getByName(attrTypeName);
   }

   @Override
   public IArtifactType getArtifactType(ArtifactId artifact) {
      return ((ArtifactReadable) artifact).getArtifactType();
   }

   @Override
   public boolean isDateType(AttributeTypeId attributeType) {
      return atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().isDateType(attributeType);
   }

   @Override
   public boolean isOfType(ArtifactId artifact, ArtifactTypeId... artifactType) {
      return atsServer.getArtifact(artifact).isOfType(artifactType);
   }

   @Override
   public void executeChangeSet(String comment, IAtsObject atsObject) {
      executeChangeSet(comment, Collections.singleton(atsObject));
   }

   @Override
   public void executeChangeSet(String comment, Collection<? extends IAtsObject> atsObjects) {
      IAtsChangeSet changes = createAtsChangeSet(comment, atsServer.getUserService().getCurrentUser());
      for (IAtsObject atsObject : atsObjects) {
         changes.add(atsObject);
      }
      changes.execute();
   }

   @Override
   public IArtifactType getArtifactType(Long artTypeId) {
      return atsServer.getOrcsApi().getOrcsTypes().getArtifactTypes().get(artTypeId);
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
      return atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().getAll();
   }

   /**
    * return false cause server always loads workItems fresh
    */
   @Override
   public boolean isChangedInDb(IAtsWorkItem workItem) {
      return false;
   }

   @Override
   public IArtifactType getArtifactType(IAtsObject atsObject) {
      return getArtifactType(atsServer.getArtifact(atsObject.getStoreObject()));
   }

   @Override
   public boolean isOfType(IAtsObject atsObject, IArtifactType artifactType) {
      return isOfType(atsObject.getStoreObject(), artifactType);
   }

   @Override
   public void clearCaches(IAtsWorkItem workItem) {
      ((WorkItem) workItem).clearCaches();
   }

   @Override
   public boolean isArtifactTypeInheritsFrom(IArtifactType artifactType, IArtifactType baseArtifactType) {
      return atsServer.getOrcsApi().getOrcsTypes().getArtifactTypes().inheritsFrom(artifactType, baseArtifactType);
   }

   @Override
   public AttributeTypeId getAttributeType(Long attrTypeId) {
      return atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes().get(attrTypeId);
   }

   @Override
   public Result setTransactionAssociatedArtifact(TransactionId trans, IAtsTeamWorkflow teamWf) {
      atsServer.getOrcsApi().getTransactionFactory().setTransactionCommitArtifact(trans, teamWf.getStoreObject());
      return Result.TrueResult;
   }
}
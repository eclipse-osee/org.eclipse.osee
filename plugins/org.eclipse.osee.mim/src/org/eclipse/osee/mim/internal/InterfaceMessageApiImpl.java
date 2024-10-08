/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.accessor.types.ArtifactMatch;
import org.eclipse.osee.accessor.types.AttributeQuery;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageApiImpl implements InterfaceMessageApi {
   private ArtifactAccessor<InterfaceMessageToken> accessor;
   private final InterfaceNodeViewApi nodeApi;
   private final InterfaceConnectionViewApi connectionApi;
   private final List<FollowRelation> relations;
   private final List<FollowRelation> fullRelations;
   private final List<RelationTypeSide> affectedRelations;

   InterfaceMessageApiImpl(OrcsApi orcsApi, InterfaceNodeViewApi nodeApi, InterfaceConnectionViewApi connectionApi) {
      this.nodeApi = nodeApi;
      this.connectionApi = connectionApi;
      this.setAccessor(new InterfaceMessageAccessor(orcsApi));
      this.relations = createRelationTypeSideList();
      this.fullRelations = createFullRelationTypeSideList();
      this.affectedRelations = createAffectedRelations();
   }

   /**
    * NOTE: Only use this when performing a search. The page does not utilize this information, and is strictly just for
    * providing the ability to search the underlying information
    *
    * @return
    */
   private List<FollowRelation> createFullRelationTypeSideList() {
      return FollowRelation.followList(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage);
   }

   private List<AttributeTypeId> getMessageSearchAttributes() {
      List<AttributeTypeId> attributes = CoreArtifactTypes.InterfaceMessage.getValidAttributeTypes().stream().map(
         a -> AttributeTypeId.valueOf(a.getId())).collect(Collectors.toList());
      List<AttributeTypeToken> excluded = CoreArtifactTypes.Artifact.getValidAttributeTypes();
      excluded.removeAll(Arrays.asList(CoreAttributeTypes.Name, CoreAttributeTypes.Description));
      attributes.removeAll(excluded);
      return attributes;
   }

   private List<AttributeTypeId> getSubMessageSearchAttributes() {
      List<AttributeTypeId> attributes = CoreArtifactTypes.InterfaceSubMessage.getValidAttributeTypes().stream().map(
         a -> AttributeTypeId.valueOf(a.getId())).collect(Collectors.toList());
      List<AttributeTypeToken> excluded = CoreArtifactTypes.Artifact.getValidAttributeTypes();
      excluded.removeAll(Arrays.asList(CoreAttributeTypes.Name, CoreAttributeTypes.Description));
      attributes.removeAll(excluded);
      return attributes;
   }

   @Override
   public ArtifactAccessor<InterfaceMessageToken> getAccessor() {
      return accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   private void setAccessor(ArtifactAccessor<InterfaceMessageToken> accessor) {
      this.accessor = accessor;
   }

   private List<FollowRelation> createRelationTypeSideList() {
      return FollowRelation.followList(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage);
   }

   private List<RelationTypeSide> createAffectedRelations() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceConnectionMessage_Message);
      return relations;
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, AttributeQuery query) {
      return this.query(branch, query, false);
   }

   @Override
   public InterfaceMessageToken setUpMessage(BranchId branch, InterfaceMessageToken message) {
      Collection<InterfaceNode> pubNodes = this.nodeApi.getMessagePublisherNodes(branch, message.getArtifactId());
      Collection<InterfaceNode> subNodes = this.nodeApi.getMessageSubscriberNodes(branch, message.getArtifactId());
      message.getPublisherNodes().addAll(pubNodes);
      message.getSubscriberNodes().addAll(subNodes);
      return message;
   }

   @Override
   public Collection<InterfaceMessageToken> getAll(BranchId branch) {
      return this.getAll(branch, 0L, 0L);
   }

   @Override
   public Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId) {
      return this.getAllForConnection(branch, connectionId, 0L, 0L);
   }

   @Override
   public InterfaceMessageToken getRelatedToConnection(BranchId branch, ArtifactId connectionId, ArtifactId messageId,
      ArtifactId viewId) {
      try {
         List<FollowRelation> relations = Arrays.asList(
            FollowRelation.fork(CoreRelationTypes.InterfaceConnectionMessage_Connection,
               FollowRelation.followList(CoreRelationTypes.InterfaceConnectionTransportType_TransportType)),
            FollowRelation.follow(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage));

         InterfaceMessageToken message = this.setUpMessage(branch, this.getAccessor().getByRelation(branch, messageId,
            CoreRelationTypes.InterfaceConnectionMessage_Connection, connectionId, relations, viewId));
         ArtifactReadable connectionReadable = message.getArtifactReadable().getRelated(
            CoreRelationTypes.InterfaceConnectionMessage_Connection).getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
         if (connectionReadable.isValid()) {
            this.addHeader(message, new InterfaceConnection(connectionReadable));
         }
         return message;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceMessageToken.SENTINEL;
   }

   @Override
   public List<FollowRelation> getFollowRelationDetails() {
      return this.relations;
   }

   @Override
   public InterfaceMessageToken get(BranchId branch, ArtifactId messageId) {
      try {
         return this.setUpMessage(branch, this.getAccessor().get(branch, messageId, this.getFollowRelationDetails()));
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return InterfaceMessageToken.SENTINEL;
      }
   }

   @Override
   public Collection<InterfaceMessageToken> get(BranchId branch, Collection<ArtifactId> messageIds,
      List<FollowRelation> followRelations) {
      try {
         return this.getAccessor().get(branch, messageIds, followRelations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<>();
   }

   @Override
   public InterfaceMessageToken getWithRelations(BranchId branch, ArtifactId messageId,
      List<FollowRelation> followRelations, ArtifactId viewId) {
      try {
         return this.setUpMessage(branch, this.getAccessor().get(branch, messageId, followRelations, viewId));
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceMessageToken.SENTINEL;
   }

   @Override
   public InterfaceMessageToken getWithAllParentRelations(BranchId branch, ArtifactId messageId) {
      try {
         List<FollowRelation> parentRelations =
            FollowRelation.followList(CoreRelationTypes.InterfaceConnectionMessage_Connection,
               CoreRelationTypes.InterfaceConnectionTransportType_TransportType);
         return this.getAccessor().get(branch, messageId, parentRelations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceMessageToken.SENTINEL;
   }

   @Override
   public InterfaceSubMessageToken getMessageHeader(InterfaceMessageToken message) {
      String name = "M" + message.getInterfaceMessageNumber().getValue() + " Header";
      if (message.getPublisherNodes().size() > 0) {
         name = message.getPublisherNodes().get(0).getName().getValue() + " " + name;
      }
      InterfaceSubMessageToken messageHeader =
         new InterfaceSubMessageToken(0L, name, "", "0", message.getApplicability());
      messageHeader.setAutogenerated(true);
      return messageHeader;
   }

   @Override
   public Collection<InterfaceMessageToken> queryExact(BranchId branch, AttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, AttributeQuery query, boolean isExact) {
      return this.query(branch, query, isExact, 0L, 0L);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId) {
      try {
         return this.getAccessor().getAffectedArtifacts(branch, relatedId, affectedRelations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<ArtifactMatch>();
   }

   @Override
   public Collection<InterfaceMessageToken> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId, long pageNum,
      long pageSize) {
      return this.getAllForConnection(branch, connectionId, ArtifactId.SENTINEL, pageNum, pageSize,
         AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, AttributeQuery query, long pageNum, long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceMessageToken> queryExact(BranchId branch, AttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, AttributeQuery query, boolean isExact, long pageNum,
      long pageSize) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, this.getFollowRelationDetails(), isExact, pageNum,
            pageSize).stream().map(m -> this.setUpMessage(branch, m)).collect(Collectors.toList());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceMessageToken>();
   }

   @Override
   public Collection<InterfaceMessageToken> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId,
      AttributeTypeId orderByAttribute) {
      return this.getAllForConnection(branch, connectionId, ArtifactId.SENTINEL, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<InterfaceMessageToken> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      try {
         return this.getAccessor().getAll(branch, this.getFollowRelationDetails(), pageNum, pageSize,
            orderByAttribute).stream().map(m -> this.setUpMessage(branch, m)).collect(Collectors.toList());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceMessageToken>();
   }

   @Override
   public Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId,
      ArtifactId viewId, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      try {
         InterfaceConnection connection = this.connectionApi.get(branch, connectionId);
         List<InterfaceMessageToken> messages =
            this.getAccessor().getAllByRelation(branch, CoreRelationTypes.InterfaceConnectionMessage_Connection,
               connectionId, this.getFollowRelationDetails(), pageNum, pageSize, orderByAttribute, viewId).stream().map(
                  m -> this.setUpMessage(branch, m)).collect(Collectors.toList());
         messages.stream().forEach(m -> {
            this.addHeader(m, connection);
         });
         return messages;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceMessageToken>();
   }

   @Override
   public Collection<InterfaceMessageToken> getAllForConnectionAndFilter(BranchId branch, ArtifactId connectionId,
      String filter) {
      return this.getAllForConnectionAndFilter(branch, connectionId, filter, ArtifactId.SENTINEL, 0L, 0L,
         AttributeTypeId.SENTINEL);
   }

   @Override
   public int getAllForConnectionAndFilterCount(BranchId branch, ArtifactId connectionId, String filter) {
      int count = 0;
      List<AttributeTypeId> messageAttributes = getMessageSearchAttributes();
      List<AttributeTypeId> subMessageAttributes = getSubMessageSearchAttributes();
      try {
         count = this.getAccessor().getAllByRelationAndFilterAndCount(branch,
            CoreRelationTypes.InterfaceConnectionMessage_Connection, connectionId, filter, messageAttributes,
            this.fullRelations, subMessageAttributes);
      } catch (Exception ex) {
         System.out.println(ex);
         return -1;
      }
      return count;
   }

   @Override
   public int getAllForConnectionAndCount(BranchId branch, ArtifactId connectionId) {
      int count = 0;

      try {
         count = this.getAccessor().getAllByRelationAndCount(branch,
            CoreRelationTypes.InterfaceConnectionMessage_Connection, connectionId);
      } catch (Exception ex) {
         System.out.println(ex);
         return -1;
      }
      return count;
   }

   @Override
   public Collection<InterfaceMessageToken> getAllForConnectionAndFilter(BranchId branch, ArtifactId connectionId,
      String filter, ArtifactId viewId, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      List<InterfaceMessageToken> messages = new LinkedList<InterfaceMessageToken>();
      List<AttributeTypeId> messageAttributes = getMessageSearchAttributes();
      List<AttributeTypeId> subMessageAttributes = getSubMessageSearchAttributes();
      try {
         InterfaceConnection connection = this.connectionApi.get(branch, connectionId);
         messages = this.getAccessor().getAllByRelationAndFilter(branch,
            CoreRelationTypes.InterfaceConnectionMessage_Connection, connectionId, filter, messageAttributes,
            this.fullRelations, pageNum, pageSize, orderByAttribute, subMessageAttributes, viewId).stream().map(
               m -> this.setUpMessage(branch, m)).collect(Collectors.toList());

         messages.stream().forEach(m -> {
            this.addHeader(m, connection);
         });
         return messages;
      } catch (Exception ex) {
         System.out.println(ex);
         return messages;
      }
   }

   private void addHeader(InterfaceMessageToken m, InterfaceConnection connection) {
      if (m.getInterfaceMessageType().getValue().equals(
         connection.getTransportType().getMessageGenerationType().getValue()) && connection.getTransportType().getMessageGeneration().getValue()) {
         String position = connection.getTransportType().getMessageGenerationPosition().getValue();
         if (position.equals("LAST")) {
            ((List<InterfaceSubMessageToken>) m.getSubMessages()).add(getMessageHeader(m));
            return;
         }
         if (position.equals("") || Strings.isNotNumeric(position)) {
            ((List<InterfaceSubMessageToken>) m.getSubMessages()).add(0, getMessageHeader(m));
            return;
         }
         if (Long.valueOf(position) > m.getSubMessages().size()) {
            ((List<InterfaceSubMessageToken>) m.getSubMessages()).add(m.getSubMessages().size(), getMessageHeader(m));
            return;
         }
         ((List<InterfaceSubMessageToken>) m.getSubMessages()).add(Integer.valueOf(position), getMessageHeader(m));
      }
   }

   @Override
   public Collection<InterfaceMessageToken> getAllwithNoConnectionRelations(BranchId branch, String filter,
      long pageNum, long pageSize) {
      List<AttributeTypeId> messageAttributes = getMessageSearchAttributes();
      Collection<RelationTypeSide> rel = new LinkedList<RelationTypeSide>();
      rel.add(CoreRelationTypes.InterfaceConnectionMessage_Message);
      try {
         return this.getAccessor().getAllLackingRelationByFilter(branch, filter, messageAttributes, rel, pageNum,
            pageSize);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
      }
      return new LinkedList<InterfaceMessageToken>();
   }

   @Override
   public int getAllwithNoConnectionRelationsCount(BranchId branch, String filter) {
      List<AttributeTypeId> messageAttributes = getMessageSearchAttributes();
      Collection<RelationTypeSide> rel = new LinkedList<RelationTypeSide>();
      rel.add(CoreRelationTypes.InterfaceConnectionMessage_Message);
      return this.getAccessor().getAllLackingRelationByFilterAndCount(branch, filter, messageAttributes, rel);
   }
}

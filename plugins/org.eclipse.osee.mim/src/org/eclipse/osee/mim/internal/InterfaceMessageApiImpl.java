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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageApiImpl implements InterfaceMessageApi {
   private ArtifactAccessor<InterfaceMessageToken> accessor;
   private final InterfaceNodeViewApi nodeApi;
   private final InterfaceConnectionViewApi connectionApi;
   private final List<RelationTypeSide> relations;
   private final List<RelationTypeSide> fullRelations;
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
   private List<RelationTypeSide> createFullRelationTypeSideList() {
      return Arrays.asList(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage);
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

   private List<RelationTypeSide> createRelationTypeSideList() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage);
      return relations;
   }

   private List<RelationTypeSide> createAffectedRelations() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceConnectionMessage_Message);
      return relations;
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, false);
   }

   private InterfaceMessageToken setUpMessage(BranchId branch, InterfaceMessageToken message) {
      message.setInitiatingNode(nodeApi.getNodeForMessage(branch, ArtifactId.valueOf(message.getId())));
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
         return this.setUpMessage(branch,
            this.getAccessor().getByRelation(branch, messageId, CoreRelationTypes.InterfaceConnectionMessage_Connection,
               connectionId, this.getFollowRelationDetails(), viewId));
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceMessageToken.SENTINEL;
   }

   @Override
   public List<RelationTypeSide> getFollowRelationDetails() {
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
   public List<InterfaceMessageToken> getAllRelatedFromSubMessage(InterfaceSubMessageToken subMessage) {
      return subMessage.getArtifactReadable().getRelated(
         CoreRelationTypes.InterfaceMessageSubMessageContent_Message).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceMessageToken(a)).collect(
               Collectors.toList());
   }

   @Override
   public InterfaceMessageToken getWithAllParentRelations(BranchId branch, ArtifactId messageId) {
      try {
         List<RelationTypeSide> parentRelations = Arrays.asList(CoreRelationTypes.InterfaceConnectionMessage_Connection,
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
      String name = message.getInitiatingNode().getName() + " M" + message.getInterfaceMessageNumber() + " Header";
      InterfaceSubMessageToken messageHeader =
         new InterfaceSubMessageToken(0L, name, "", "0", message.getApplicability());
      messageHeader.setAutogenerated(true);
      return messageHeader;
   }

   @Override
   public Collection<InterfaceMessageToken> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
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
   public Collection<InterfaceMessageToken> query(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceMessageToken> queryExact(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, MimAttributeQuery query, boolean isExact,
      long pageNum, long pageSize) {
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
      if (m.getInterfaceMessageType().equals(
         connection.getTransportType().getMessageGenerationType()) && connection.getTransportType().isMessageGeneration()) {
         String position = connection.getTransportType().getMessageGenerationPosition();
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
}

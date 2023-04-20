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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
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
   private final InterfaceSubMessageApi subMessageApi;
   private final List<RelationTypeSide> relations;
   private final List<RelationTypeSide> fullRelations;
   private final List<RelationTypeSide> affectedRelations;

   InterfaceMessageApiImpl(OrcsApi orcsApi, InterfaceNodeViewApi nodeApi, InterfaceSubMessageApi subMessageApi) {
      this.nodeApi = nodeApi;
      this.subMessageApi = subMessageApi;
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
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage);
      return relations;
   }

   private List<AttributeTypeId> createMessageAttributes() {
      List<AttributeTypeId> messageAttributes = new LinkedList<AttributeTypeId>();
      messageAttributes.add(CoreAttributeTypes.Name);
      messageAttributes.add(CoreAttributeTypes.Description);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessageNumber);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessagePeriodicity);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessageRate);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessageWriteAccess);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessageType);
      return messageAttributes;
   }

   private List<AttributeTypeId> createSubMessageAttributes() {
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
      attributes.add(CoreAttributeTypes.Description);
      attributes.add(CoreAttributeTypes.InterfaceSubMessageNumber);
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
      relations.add(CoreRelationTypes.InterfaceConnectionContent_Message);
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
   public InterfaceMessageToken getRelatedToConnection(BranchId branch, ArtifactId connectionId, ArtifactId messageId, ArtifactId viewId) {
      try {
         return this.setUpMessage(branch,
            this.getAccessor().getByRelation(branch, messageId, CoreRelationTypes.InterfaceConnectionContent_Connection,
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
         List<RelationTypeSide> parentRelations = Arrays.asList(CoreRelationTypes.InterfaceConnectionContent_Connection,
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
   public Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId, long pageNum, long pageSize) {
      return this.getAllForConnection(branch, connectionId, ArtifactId.SENTINEL, pageNum, pageSize,
         AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, MimAttributeQuery query, long pageNum, long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceMessageToken> queryExact(BranchId branch, MimAttributeQuery query, long pageNum, long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, MimAttributeQuery query, boolean isExact, long pageNum, long pageSize) {
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
   public Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId, AttributeTypeId orderByAttribute) {
      return this.getAllForConnection(branch, connectionId, ArtifactId.SENTINEL, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<InterfaceMessageToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
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
   public Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId, ArtifactId viewId, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      try {
         List<InterfaceMessageToken> messages =
            this.getAccessor().getAllByRelation(branch, CoreRelationTypes.InterfaceConnectionContent_Connection,
               connectionId, this.getFollowRelationDetails(), pageNum, pageSize, orderByAttribute, viewId).stream().map(
                  m -> this.setUpMessage(branch, m)).collect(Collectors.toList());
         messages.stream().forEach(m -> {
            if (m.getInterfaceMessageType().equals("Operational")) {
               ((List<InterfaceSubMessageToken>) m.getSubMessages()).add(0, getMessageHeader(m));
            }
         });
         return messages;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceMessageToken>();
   }

   @Override
   public Collection<InterfaceMessageToken> getAllForConnectionAndFilter(BranchId branch, ArtifactId connectionId, String filter) {
      return this.getAllForConnectionAndFilter(branch, connectionId, filter, ArtifactId.SENTINEL, 0L, 0L,
         AttributeTypeId.SENTINEL);
   }

   @Override
   public int getAllForConnectionAndFilterCount(BranchId branch, ArtifactId connectionId, String filter) {
      int count = 0;
      List<AttributeTypeId> messageAttributes = createMessageAttributes();
      List<AttributeTypeId> subMessageAttributes = createSubMessageAttributes();
      try {
         count = this.getAccessor().getAllByRelationAndFilterAndCount(branch,
            CoreRelationTypes.InterfaceConnectionContent_Connection, connectionId, filter, messageAttributes,
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
            CoreRelationTypes.InterfaceConnectionContent_Connection, connectionId);
      } catch (Exception ex) {
         System.out.println(ex);
         return -1;
      }
      return count;
   }

   @Override
   public Collection<InterfaceMessageToken> getAllForConnectionAndFilter(BranchId branch, ArtifactId connectionId, String filter, ArtifactId viewId, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      List<InterfaceMessageToken> messages = new LinkedList<InterfaceMessageToken>();
      List<AttributeTypeId> messageAttributes = createMessageAttributes();
      List<AttributeTypeId> subMessageAttributes = createSubMessageAttributes();
      try {
         messages = this.getAccessor().getAllByRelationAndFilter(branch,
            CoreRelationTypes.InterfaceConnectionContent_Connection, connectionId, filter, messageAttributes,
            this.fullRelations, pageNum, pageSize, orderByAttribute, subMessageAttributes, viewId).stream().map(
               m -> this.setUpMessage(branch, m)).collect(Collectors.toList());

         messages.stream().forEach(m -> {
            if (m.getInterfaceMessageType().equals("Operational")) {
               ((List<InterfaceSubMessageToken>) m.getSubMessages()).add(0, getMessageHeader(m));
            }
         });
         return messages;
      } catch (Exception ex) {
         System.out.println(ex);
         return messages;
      }
   }
}

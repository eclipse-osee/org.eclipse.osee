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
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 * @todo
 */
public class InterfaceMessageApiImpl implements InterfaceMessageApi {
   private ArtifactAccessor<InterfaceMessageToken> accessor;
   private final InterfaceSubMessageApi submessageApi;
   private final InterfaceNodeViewApi nodeApi;
   private final List<AttributeTypeId> messageAttributes;
   private final List<RelationTypeSide> relations;

   InterfaceMessageApiImpl(OrcsApi orcsApi, InterfaceNodeViewApi nodeApi, InterfaceSubMessageApi submessageApi) {
      this.submessageApi = submessageApi;
      this.nodeApi = nodeApi;
      this.setAccessor(new InterfaceMessageAccessor(orcsApi));
      this.messageAttributes = createMessageAttributes();
      this.relations = createRelationTypeSideList();
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

   private List<AttributeTypeId> createMessageAttributes() {
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
      attributes.add(CoreAttributeTypes.Description);
      attributes.add(CoreAttributeTypes.InterfaceMessageNumber);
      attributes.add(CoreAttributeTypes.InterfaceMessagePeriodicity);
      attributes.add(CoreAttributeTypes.InterfaceMessageRate);
      attributes.add(CoreAttributeTypes.InterfaceMessageWriteAccess);
      attributes.add(CoreAttributeTypes.InterfaceMessageType);
      return attributes;
   }

   @Override
   public Collection<InterfaceMessageToken> query(BranchId branch, MimAttributeQuery query) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, this.getFollowRelationDetails(),
            InterfaceMessageToken.class).stream().map(m -> this.setUpMessage(branch, m)).collect(Collectors.toList());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceMessageToken>();
   }

   private InterfaceMessageToken setUpMessage(BranchId branch, InterfaceMessageToken message) {
      message.setInitiatingNode(nodeApi.getNodeForMessage(branch, ArtifactId.valueOf(message.getId())));
      return message;
   }

   @Override
   public Collection<InterfaceMessageToken> getAll(BranchId branch) {
      try {
         return this.getAccessor().getAll(branch, this.getFollowRelationDetails(),
            InterfaceMessageToken.class).stream().map(m -> this.setUpMessage(branch, m)).collect(Collectors.toList());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceMessageToken>();
   }

   @Override
   public Collection<InterfaceMessageToken> getAllForConnection(BranchId branch, ArtifactId connectionId) {
      try {
         return this.getAccessor().getAllByRelation(branch, CoreRelationTypes.InterfaceConnectionContent_Connection,
            connectionId, this.getFollowRelationDetails(), InterfaceMessageToken.class).stream().map(
               m -> this.setUpMessage(branch, m)).collect(Collectors.toList());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex.getCause());
      }
      return new LinkedList<InterfaceMessageToken>();
   }

   @Override
   public InterfaceMessageToken getRelatedToConnection(BranchId branch, ArtifactId connectionId, ArtifactId messageId) {
      try {
         return this.setUpMessage(branch,
            this.getAccessor().getByRelation(branch, messageId, CoreRelationTypes.InterfaceConnectionContent_Connection,
               connectionId, this.getFollowRelationDetails(), InterfaceMessageToken.class));
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
         return this.setUpMessage(branch,
            this.getAccessor().get(branch, messageId, this.getFollowRelationDetails(), InterfaceMessageToken.class));
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
         List<RelationTypeSide> parentRelations =
            Arrays.asList(CoreRelationTypes.InterfaceConnectionContent_Connection);
         return this.getAccessor().get(branch, messageId, parentRelations, InterfaceMessageToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceMessageToken.SENTINEL;
   }

}

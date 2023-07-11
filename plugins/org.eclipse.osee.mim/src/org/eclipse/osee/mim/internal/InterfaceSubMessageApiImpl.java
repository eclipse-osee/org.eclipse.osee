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
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti Api for accessing interface sub messages
 */
public class InterfaceSubMessageApiImpl implements InterfaceSubMessageApi {

   private ArtifactAccessor<InterfaceSubMessageToken> accessor;
   private final List<AttributeTypeId> subMessageAttributes;
   private final List<RelationTypeSide> affectedRelations;

   InterfaceSubMessageApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceSubMessageAccessor(orcsApi));
      this.subMessageAttributes = createSubmessageAttributes();
      this.affectedRelations = createAffectedRelations();
   }

   private List<RelationTypeSide> createAffectedRelations() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage);
      return relations;
   }

   private void setAccessor(InterfaceSubMessageAccessor interfaceSubMessageAccessor) {
      this.accessor = interfaceSubMessageAccessor;
   }

   private List<AttributeTypeId> createSubmessageAttributes() {
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
      attributes.add(CoreAttributeTypes.Description);
      attributes.add(CoreAttributeTypes.InterfaceSubMessageNumber);
      return attributes;
   }

   @Override
   public ArtifactAccessor<InterfaceSubMessageToken> getAccessor() {
      return this.accessor;
   }

   @Override
   public Collection<InterfaceSubMessageToken> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, false);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId) {
      return this.getAllByRelation(branch, messageId, 0L, 0L);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter) {
      return this.getAllByFilter(branch, filter, 0L, 0L);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByRelationAndFilter(BranchId branch, ArtifactId messageId,
      String filter) {
      try {
         return this.getAccessor().getAllByRelationAndFilter(branch,
            CoreRelationTypes.InterfaceMessageSubMessageContent_Message, messageId, filter, subMessageAttributes);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceSubMessageToken>();
   }

   @Override
   public InterfaceSubMessageToken get(BranchId branch, ArtifactId subMessageId) {
      try {
         return this.getAccessor().get(branch, subMessageId);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceSubMessageToken.SENTINEL;
   }

   @Override
   public List<InterfaceSubMessageToken> getAllRelatedFromStructure(InterfaceStructureToken structure) {
      return structure.getArtifactReadable().getRelated(
         CoreRelationTypes.InterfaceSubMessageContent_SubMessage).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceSubMessageToken(a)).collect(
               Collectors.toList());
   }

   @Override
   public InterfaceSubMessageToken getWithAllParentRelations(BranchId branch, ArtifactId subMessageId) {
      try {
         List<RelationTypeSide> parentRelations =
            Arrays.asList(CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
               CoreRelationTypes.InterfaceConnectionMessage_Connection,
               CoreRelationTypes.InterfaceConnectionTransportType_TransportType);
         return this.getAccessor().get(branch, subMessageId, parentRelations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceSubMessageToken.SENTINEL;
   }

   @Override
   public Collection<InterfaceSubMessageToken> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceSubMessageToken> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
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
   public Collection<InterfaceSubMessageToken> query(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceSubMessageToken> queryExact(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceSubMessageToken> query(BranchId branch, MimAttributeQuery query, boolean isExact,
      long pageNum, long pageSize) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, isExact, pageNum, pageSize);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceSubMessageToken>();
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId, long pageNum,
      long pageSize) {
      return this.getAllByRelation(branch, messageId, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter, long pageNum,
      long pageSize) {
      return this.getAllByFilter(branch, filter, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId,
      AttributeTypeId orderByAttribute) {
      return this.getAllByRelation(branch, messageId, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, filter, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute) {
      try {
         return this.getAccessor().getAllByRelation(branch, CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
            messageId, pageNum, pageSize);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceSubMessageToken>();
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute) {
      try {
         return this.getAccessor().getAllByFilter(branch, filter, subMessageAttributes, pageNum, pageSize,
            orderByAttribute);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceSubMessageToken>();
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAll(BranchId branch) {
      return this.getAll(branch, 0L, 0L);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      try {
         return this.getAccessor().getAll(branch, pageNum, pageSize, orderByAttribute);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceSubMessageToken>();
   }

   @Override
   public List<InterfaceSubMessageToken> getAllByName(BranchId branch, String name, long pageNum, long pageSize) {
      try {
         return (List<InterfaceSubMessageToken>) this.getAccessor().getAll(branch, new LinkedList<>(), name,
            Arrays.asList(CoreAttributeTypes.Name), pageNum, pageSize, CoreAttributeTypes.Name);
      } catch (Exception ex) {
         System.out.println(ex);
         return new LinkedList<>();
      }
   }

   @Override
   public int getAllByNameCount(BranchId branch, String name) {
      return this.getAccessor().getAllByFilterAndCount(branch, name, Arrays.asList(CoreAttributeTypes.Name));
   }

}

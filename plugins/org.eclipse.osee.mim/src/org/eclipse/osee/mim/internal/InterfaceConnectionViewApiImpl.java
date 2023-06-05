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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceConnectionViewApiImpl implements InterfaceConnectionViewApi {

   private ArtifactAccessor<InterfaceConnection> accessor;
   private final List<RelationTypeSide> affectedRelations;
   private final List<RelationTypeSide> relations;

   InterfaceConnectionViewApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceConnectionAccessor(orcsApi));
      this.affectedRelations = this.createAffectedRelationTypeSideList();
      this.relations = createRelationTypeSideList();
   }

   private List<RelationTypeSide> createRelationTypeSideList() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceConnectionTransportType_TransportType);
      return relations;
   }

   @Override
   public ArtifactAccessor<InterfaceConnection> getAccessor() {
      return this.accessor;
   }

   private List<RelationTypeSide> createAffectedRelationTypeSideList() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceConnectionNode_Node);
      return relations;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceConnection> accessor) {
      this.accessor = accessor;
   }

   @Override
   public Collection<InterfaceConnection> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, false);
   }

   @Override
   public InterfaceConnection getRelatedFromMessage(InterfaceMessageToken message) {
      return message.getArtifactReadable().getRelated(
         CoreRelationTypes.InterfaceConnectionMessage_Connection).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceConnection(a)).findFirst().orElse(
               InterfaceConnection.SENTINEL);
   }

   @Override
   public InterfaceConnection get(BranchId branch, ArtifactId connectionId) {
      try {
         return this.getAccessor().get(branch, connectionId, relations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return InterfaceConnection.SENTINEL;
   }

   @Override
   public Collection<InterfaceConnection> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceConnection> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
      return this.query(branch, query, isExact, 0L, 0L);
   }

   /**
    * note: currently non-functional as there is no way to get both primary and secondary nodes in a single query
    */
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
   public Collection<InterfaceConnection> getAll(BranchId branch) {
      return this.getAll(branch, 0, 0);
   }

   @Override
   public Collection<InterfaceConnection> getAll(BranchId branch, ArtifactId viewId) {
      return this.getAll(branch, 0L, 0L, AttributeTypeId.SENTINEL, viewId);
   }

   @Override
   public Collection<InterfaceConnection> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, 0L, 0L, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<InterfaceConnection> query(BranchId branch, MimAttributeQuery query, long pageNum, long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceConnection> queryExact(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceConnection> query(BranchId branch, MimAttributeQuery query, boolean isExact, long pageNum,
      long pageSize) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, relations, isExact, pageNum, pageSize);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceConnection>();
   }

   @Override
   public Collection<InterfaceConnection> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<InterfaceConnection> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, pageNum, pageSize, orderByAttribute, ArtifactId.SENTINEL);
   }

   @Override
   public Collection<InterfaceConnection> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute, ArtifactId viewId) {
      try {
         return this.getAccessor().getAll(branch, relations, pageNum, pageSize, orderByAttribute, viewId);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<InterfaceConnection>();
   }

}

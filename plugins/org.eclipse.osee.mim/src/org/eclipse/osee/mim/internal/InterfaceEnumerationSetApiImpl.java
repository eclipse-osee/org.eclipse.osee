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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceEnumerationSetApiImpl implements InterfaceEnumerationSetApi {

   private ArtifactAccessor<InterfaceEnumerationSet> accessor;
   private final InterfaceEnumerationApi interfaceEnumerationApi;
   private final List<RelationTypeSide> affectedRelations;

   public InterfaceEnumerationSetApiImpl(OrcsApi orcsApi, InterfaceEnumerationApi interfaceEnumerationApi) {
      this.setAccessor(new InterfaceEnumerationSetAccessor(orcsApi));
      this.interfaceEnumerationApi = interfaceEnumerationApi;
      this.affectedRelations = this.createAffectedRelationTypeSideList();
   }

   private List<RelationTypeSide> createAffectedRelationTypeSideList() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet);
      return relations;
   }

   @Override
   public ArtifactAccessor<InterfaceEnumerationSet> getAccessor() {
      return this.accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceEnumerationSet> accessor) {
      this.accessor = accessor;
   }

   @Override
   public Collection<InterfaceEnumerationSet> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, false);
   }

   @Override
   public InterfaceEnumerationSet get(BranchId branch, ArtifactId enumSetId) {
      try {
         InterfaceEnumerationSet enumSet = this.getAccessor().get(branch, enumSetId);
         enumSet.setEnumerations(
            (List<InterfaceEnumeration>) this.interfaceEnumerationApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceEnumeration_EnumerationSet, ArtifactId.valueOf(enumSet.getId())));
         return enumSet;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceEnumerationSet.SENTINEL;
   }

   @Override
   public List<InterfaceEnumerationSet> getAll(BranchId branch) {
      return this.getAll(branch, 0L, 0L);
   }

   @Override
   public Collection<InterfaceEnumerationSet> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceEnumerationSet> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
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
   public Collection<InterfaceEnumerationSet> query(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceEnumerationSet> queryExact(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceEnumerationSet> query(BranchId branch, MimAttributeQuery query, boolean isExact,
      long pageNum, long pageSize) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, isExact, pageNum, pageSize);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceEnumerationSet>();
   }

   @Override
   public List<InterfaceEnumerationSet> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public List<InterfaceEnumerationSet> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, 0L, 0L, orderByAttribute);
   }

   @Override
   public List<InterfaceEnumerationSet> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      try {
         List<InterfaceEnumerationSet> enumSet = (List<InterfaceEnumerationSet>) this.getAccessor().getAll(branch,
            FollowRelation.followList(CoreRelationTypes.InterfaceEnumeration_EnumerationState), pageNum, pageSize,
            CoreAttributeTypes.Name);
         return enumSet;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<>();
   }

}

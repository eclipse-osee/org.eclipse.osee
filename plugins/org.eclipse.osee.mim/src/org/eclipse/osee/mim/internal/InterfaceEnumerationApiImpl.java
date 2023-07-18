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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceEnumerationApiImpl implements InterfaceEnumerationApi {

   private ArtifactAccessor<InterfaceEnumeration> accessor;
   private final List<RelationTypeSide> affectedRelations;
   public InterfaceEnumerationApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceEnumerationAccessor(orcsApi));
      this.affectedRelations = this.createAffectedRelationTypeSideList();
   }

   @Override
   public ArtifactAccessor<InterfaceEnumeration> getAccessor() {
      return this.accessor;
   }

   private List<RelationTypeSide> createAffectedRelationTypeSideList() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceEnumeration_EnumerationState);
      return relations;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceEnumeration> accessor) {
      this.accessor = accessor;
   }

   @Override
   public Collection<InterfaceEnumeration> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, false);
   }

   @Override
   public InterfaceEnumeration get(BranchId branch, ArtifactId enumId, List<FollowRelation> relations) {
      try {
         return this.getAccessor().get(branch, enumId, relations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceEnumeration.SENTINEL;
   }

   @Override
   public Collection<InterfaceEnumeration> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceEnumeration> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
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
   public Collection<InterfaceEnumeration> query(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceEnumeration> queryExact(BranchId branch, MimAttributeQuery query, long pageNum,
      long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceEnumeration> query(BranchId branch, MimAttributeQuery query, boolean isExact,
      long pageNum, long pageSize) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, isExact, pageNum, pageSize);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceEnumeration>();
   }

}

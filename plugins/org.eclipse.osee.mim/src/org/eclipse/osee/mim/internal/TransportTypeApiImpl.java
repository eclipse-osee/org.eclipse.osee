/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.TransportTypeApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.TransportType;
import org.eclipse.osee.orcs.OrcsApi;

public class TransportTypeApiImpl implements TransportTypeApi {

   private ArtifactAccessor<TransportType> accessor;

   public TransportTypeApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new TransportTypeAccessor(orcsApi));
   }

   /**
    * @param accessor the accessor to set
    */
   private void setAccessor(ArtifactAccessor<TransportType> accessor) {
      this.accessor = accessor;
   }

   @Override
   public Collection<TransportType> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, 0L, 0L);
   }

   @Override
   public Collection<TransportType> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.queryExact(branch, query, 0L, 0L);
   }

   @Override
   public Collection<TransportType> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
      return this.query(branch, query, isExact, 0L, 0L);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId) {
      return null;
   }

   @Override
   public Collection<TransportType> getAll(BranchId branch) {
      return this.getAll(branch, 0L, 0L);
   }

   @Override
   public TransportType get(BranchId branch, ArtifactId artId) {
      try {
         return this.accessor.get(branch, artId);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new TransportType();
   }

   @Override
   public Collection<TransportType> query(BranchId branch, MimAttributeQuery query, long pageNum, long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<TransportType> queryExact(BranchId branch, MimAttributeQuery query, long pageNum, long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<TransportType> query(BranchId branch, MimAttributeQuery query, boolean isExact, long pageNum,
      long pageSize) {
      try {
         return this.accessor.getAllByQuery(branch, query, isExact, pageNum, pageSize);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<TransportType>();
   }

   @Override
   public Collection<TransportType> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<TransportType> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<TransportType> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAll(branch, pageNum, pageSize);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<TransportType>();
   }

   @Override
   public TransportType getFromConnection(BranchId branch, ArtifactId connectionId) {
      try {
         return this.accessor.getByRelationWithoutId(branch,
            CoreRelationTypes.InterfaceConnectionTransportType_InterfaceConnection, connectionId);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return TransportType.SENTINEL;
   }

}

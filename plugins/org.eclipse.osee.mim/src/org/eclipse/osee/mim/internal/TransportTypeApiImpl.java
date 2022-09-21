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
import org.eclipse.osee.framework.core.data.BranchId;
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
      return null;
   }

   @Override
   public Collection<TransportType> queryExact(BranchId branch, MimAttributeQuery query) {
      return null;
   }

   @Override
   public Collection<TransportType> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
      return null;
   }

   @Override
   public Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId) {
      return null;
   }

   @Override
   public Collection<TransportType> getAll(BranchId branch) {
      try {
         return this.accessor.getAll(branch, TransportType.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
      }
      return new LinkedList<TransportType>();
   }

   @Override
   public TransportType get(BranchId branch, ArtifactId artId) {
      try {
         return this.accessor.get(branch, artId, TransportType.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
      }
      return new TransportType();
   }

}

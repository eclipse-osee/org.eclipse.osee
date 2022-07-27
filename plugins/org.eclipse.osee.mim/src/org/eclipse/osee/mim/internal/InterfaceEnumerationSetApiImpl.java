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
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceEnumerationSetApiImpl implements InterfaceEnumerationSetApi {

   private ArtifactAccessor<InterfaceEnumerationSet> accessor;
   private final InterfaceEnumerationApi interfaceEnumerationApi;

   public InterfaceEnumerationSetApiImpl(OrcsApi orcsApi, InterfaceEnumerationApi interfaceEnumerationApi) {
      this.setAccessor(new InterfaceEnumerationSetAccessor(orcsApi));
      this.interfaceEnumerationApi = interfaceEnumerationApi;
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
         InterfaceEnumerationSet enumSet = this.getAccessor().get(branch, enumSetId, InterfaceEnumerationSet.class);
         enumSet.setEnumerations(
            (List<InterfaceEnumeration>) this.interfaceEnumerationApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceEnumeration_EnumerationSet, ArtifactId.valueOf(enumSet.getId()),
               InterfaceEnumeration.class));
         return enumSet;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceEnumerationSet.SENTINEL;
   }

   @Override
   public List<InterfaceEnumerationSet> getAll(BranchId branch) {
      try {
         List<InterfaceEnumerationSet> enumSet =
            (List<InterfaceEnumerationSet>) this.getAccessor().getAll(branch, InterfaceEnumerationSet.class);
         for (InterfaceEnumerationSet set : enumSet) {
            set.setEnumerations(
               (List<InterfaceEnumeration>) this.interfaceEnumerationApi.getAccessor().getAllByRelation(branch,
                  CoreRelationTypes.InterfaceEnumeration_EnumerationSet, ArtifactId.valueOf(set.getId()),
                  InterfaceEnumeration.class));
         }
         return enumSet;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<>();
   }

   @Override
   public Collection<InterfaceEnumerationSet> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceEnumerationSet> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, isExact, InterfaceEnumerationSet.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceEnumerationSet>();
   }

}

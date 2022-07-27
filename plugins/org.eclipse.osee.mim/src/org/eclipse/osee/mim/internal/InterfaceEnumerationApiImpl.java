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
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceEnumerationApiImpl implements InterfaceEnumerationApi {

   private ArtifactAccessor<InterfaceEnumeration> accessor;
   public InterfaceEnumerationApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceEnumerationAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<InterfaceEnumeration> getAccessor() {
      return this.accessor;
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
   public InterfaceEnumeration get(BranchId branch, ArtifactId enumId, List<RelationTypeSide> relations) {
      try {
         return this.getAccessor().get(branch, enumId, relations, InterfaceEnumeration.class);
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
      try {
         return this.getAccessor().getAllByQuery(branch, query, isExact, InterfaceEnumeration.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceEnumeration>();
   }

}

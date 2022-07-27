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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceConnectionViewApiImpl implements InterfaceConnectionViewApi {

   private ArtifactAccessor<InterfaceConnection> accessor;

   InterfaceConnectionViewApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceConnectionAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<InterfaceConnection> getAccessor() {
      return this.accessor;
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
         CoreRelationTypes.InterfaceConnectionContent_Connection).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceConnection(a)).findFirst().orElse(
               InterfaceConnection.SENTINEL);
   }

   @Override
   public InterfaceConnection get(BranchId branch, ArtifactId connectionId) {
      try {
         return this.getAccessor().get(branch, connectionId, InterfaceConnection.class);
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
      try {
         return this.getAccessor().getAllByQuery(branch, query, isExact, InterfaceConnection.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceConnection>();
   }

}

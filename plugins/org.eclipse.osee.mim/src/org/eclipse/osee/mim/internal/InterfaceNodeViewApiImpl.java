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
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.PLGenericDBObject;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceNodeViewApiImpl implements InterfaceNodeViewApi {

   private ArtifactAccessor<InterfaceNode> accessor;

   InterfaceNodeViewApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceNodeAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<InterfaceNode> getAccessor() {
      return this.accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceNode> accessor) {
      this.accessor = accessor;
   }

   @Override
   public Collection<InterfaceNode> query(BranchId branch, MimAttributeQuery query) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, InterfaceNode.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
      }
      return new LinkedList<InterfaceNode>();
   }

   @Override
   public InterfaceNode getNodeForMessage(BranchId branch, ArtifactId message) {
      try {
         return this.getAccessor().getByRelationWithoutId(branch, CoreRelationTypes.InterfaceMessageSendingNode_Message,
            ArtifactId.valueOf(message.getId()), InterfaceNode.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
      }
      return (InterfaceNode) PLGenericDBObject.SENTINEL;
   }

}

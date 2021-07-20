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

import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.ArtifactInserter;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceNodeViewApiImpl implements InterfaceNodeViewApi {

   private ArtifactAccessor<InterfaceNode> accessor;
   private ArtifactInserter<InterfaceNode> inserter;

   InterfaceNodeViewApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceNodeAccessor(orcsApi));
      this.setInserter(new InterfaceNodeInserter(orcsApi, this.getAccessor()));
   }

   @Override
   public ArtifactAccessor<InterfaceNode> getAccessor() {
      return this.accessor;
   }

   @Override
   public ArtifactInserter<InterfaceNode> getInserter() {
      return this.inserter;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceNode> accessor) {
      this.accessor = accessor;
   }

   /**
    * @param inserter the inserter to set
    */
   public void setInserter(ArtifactInserter<InterfaceNode> inserter) {
      this.inserter = inserter;
   }

}

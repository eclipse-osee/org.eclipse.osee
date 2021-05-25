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
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureApiImpl implements InterfaceStructureApi {

   private ArtifactAccessor<InterfaceStructureToken> accessor;
   private ArtifactInserter<InterfaceStructureToken> inserter;

   InterfaceStructureApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceStructureAccessor(orcsApi));
      this.setInserter(new InterfaceStructureInserter(orcsApi, this.getAccessor()));
   }

   @Override
   public ArtifactAccessor<InterfaceStructureToken> getAccessor() {
      return this.accessor;
   }

   @Override
   public ArtifactInserter<InterfaceStructureToken> getInserter() {
      return this.inserter;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceStructureToken> accessor) {
      this.accessor = accessor;
   }

   /**
    * @param inserter the inserter to set
    */
   public void setInserter(ArtifactInserter<InterfaceStructureToken> inserter) {
      this.inserter = inserter;
   }

}

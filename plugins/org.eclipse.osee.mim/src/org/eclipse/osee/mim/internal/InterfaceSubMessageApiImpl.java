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
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti Api for accessing interface sub messages
 */
public class InterfaceSubMessageApiImpl implements InterfaceSubMessageApi {

   private ArtifactAccessor<InterfaceSubMessageToken> accessor;
   private ArtifactInserter<InterfaceSubMessageToken> inserter;

   InterfaceSubMessageApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceSubMessageAccessor(orcsApi));
      this.setInserter(new InterfaceSubMessageInserter(orcsApi, this.getAccessor()));
   }

   private void setInserter(InterfaceSubMessageInserter interfaceSubMessageInserter) {
      this.inserter = interfaceSubMessageInserter;
   }

   private void setAccessor(InterfaceSubMessageAccessor interfaceSubMessageAccessor) {
      this.accessor = interfaceSubMessageAccessor;
   }

   @Override
   public ArtifactAccessor<InterfaceSubMessageToken> getAccessor() {
      return this.accessor;
   }

   @Override
   public ArtifactInserter<InterfaceSubMessageToken> getInserter() {
      return this.inserter;
   }

}

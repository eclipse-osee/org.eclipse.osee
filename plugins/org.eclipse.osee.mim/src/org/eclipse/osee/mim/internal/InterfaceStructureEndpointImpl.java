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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceElementArrayApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceStructureEndpoint;
import org.eclipse.osee.mim.types.InterfaceStructureToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureEndpointImpl implements InterfaceStructureEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final ArtifactId messageId;
   private final ArtifactId subMessageId;
   private final InterfaceStructureApi interfaceStructureApi;

   public InterfaceStructureEndpointImpl(BranchId branch, UserId accountId, ArtifactId messageId, ArtifactId subMessageId, InterfaceStructureApi interfaceStructureApi) {
      this.account = accountId;
      this.branch = branch;
      this.messageId = messageId;
      this.subMessageId = subMessageId;
      this.interfaceStructureApi = interfaceStructureApi;

   }

   @Override
   public Collection<InterfaceStructureToken> getAllStructures() {
      return this.interfaceStructureApi.getAllRelated(branch, subMessageId);
   }

   @Override
   public InterfaceStructureToken getStructure(ArtifactId structureId) {
      return this.interfaceStructureApi.getRelated(branch, subMessageId, structureId);
   }
}

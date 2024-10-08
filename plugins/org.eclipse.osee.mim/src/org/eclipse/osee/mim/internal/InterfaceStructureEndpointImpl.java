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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceStructureEndpoint;
import org.eclipse.osee.mim.types.InterfaceStructureToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureEndpointImpl implements InterfaceStructureEndpoint {

   private final BranchId branch;
   private final ArtifactId connectionId;
   private final ArtifactId messageId;
   private final ArtifactId subMessageId;
   private final InterfaceStructureApi interfaceStructureApi;

   public InterfaceStructureEndpointImpl(BranchId branch, ArtifactId connectionId, ArtifactId messageId, ArtifactId subMessageId, InterfaceStructureApi interfaceStructureApi) {
      this.branch = branch;
      this.connectionId = connectionId;
      this.messageId = messageId;
      this.subMessageId = subMessageId;
      this.interfaceStructureApi = interfaceStructureApi;

   }

   @Override
   public Collection<InterfaceStructureToken> getAllStructures(String filter, ArtifactId viewId, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeTypeId) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (subMessageId.getId() == 0) {
         return Arrays.asList(interfaceStructureApi.getMessageHeaderStructure(branch, connectionId, messageId, viewId));
      }
      if (Strings.isValid(filter)) {
         return this.interfaceStructureApi.getAllRelatedAndFilter(branch, connectionId, subMessageId, viewId, filter,
            pageNum, pageSize, orderByAttributeTypeId);
      }
      return this.interfaceStructureApi.getAllRelated(branch, connectionId, subMessageId, viewId, pageNum, pageSize,
         orderByAttributeTypeId);
   }

   @Override
   public InterfaceStructureToken getStructure(ArtifactId structureId, String filter, ArtifactId viewId) {
      if (Strings.isValid(filter)) {
         return this.interfaceStructureApi.getRelatedAndFilter(branch, connectionId, subMessageId, structureId, filter,
            viewId);
      }
      return this.interfaceStructureApi.getRelated(branch, connectionId, subMessageId, structureId, viewId);
   }

}

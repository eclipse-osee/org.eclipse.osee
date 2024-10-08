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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceElementEndpoint;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceElementEndpointImpl implements InterfaceElementEndpoint {
   private final BranchId branch;
   private final ArtifactId messageId;
   private final ArtifactId subMessageId;
   private final ArtifactId structureId;
   private final InterfaceElementApi elementApi;

   public InterfaceElementEndpointImpl(BranchId branch, ArtifactId messageId, ArtifactId subMessageId, ArtifactId structureId, InterfaceElementApi interfaceElementApi) {
      this.branch = branch;
      this.messageId = messageId;
      this.subMessageId = subMessageId;
      this.structureId = structureId;
      this.elementApi = interfaceElementApi;
   }

   @Override
   public Collection<InterfaceStructureElementToken> getAllElements(long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      return this.elementApi.getAllRelated(branch, structureId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public InterfaceStructureElementToken getElement(ArtifactId elementId) {
      return this.elementApi.getRelated(branch, structureId, elementId);
   }

}
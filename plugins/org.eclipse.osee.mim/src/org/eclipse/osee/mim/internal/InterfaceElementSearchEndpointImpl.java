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
import org.eclipse.osee.mim.InterfaceElementSearchEndpoint;
import org.eclipse.osee.mim.types.ElementPosition;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureElementTokenWithPath;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceElementSearchEndpointImpl implements InterfaceElementSearchEndpoint {

   private final BranchId branch;
   private final InterfaceElementApi elementApi;

   public InterfaceElementSearchEndpointImpl(BranchId branch, InterfaceElementApi interfaceElementApi) {
      this.branch = branch;
      this.elementApi = interfaceElementApi;
   }

   @Override
   public Collection<InterfaceStructureElementToken> getElements(long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      return this.elementApi.getAll(branch, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public Collection<InterfaceStructureElementToken> getElements(String filter, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      return this.elementApi.getFiltered(branch, filter, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public Collection<InterfaceStructureElementToken> getElementsOfType(ArtifactId platformTypeId) {
      return this.elementApi.getElementsByType(branch, platformTypeId);
   }

   @Override
   public ElementPosition findElement(ArtifactId elementId) {
      //Todo at a later date, this endpoint is responsible for returning the relationship tree of an element so that users may return to the main page from an element search.
      //This ties directly to one of the actions out of a meeting on 6/22, corresponding with new UI, however it could be slightly optional. Likely will be worked during TW19361.
      return new ElementPosition();
   }

   @Override
   public Collection<InterfaceStructureElementTokenWithPath> getElementsByType() {
      return this.elementApi.getElementsByType(branch);
   }

   @Override
   public Collection<InterfaceStructureElementTokenWithPath> getElementsByType(String filter) {
      return this.elementApi.getElementsByTypeFilter(branch, filter);
   }

   @Override
   public Collection<InterfaceStructureElementToken> getElementsByName(String name, long pageNum, long pageSize) {
      return this.elementApi.getElementsByName(branch, name, pageNum, pageSize);
   }

   @Override
   public int getElementsByNameCount(String name) {
      return this.elementApi.getElementsByNameCount(branch, name);
   }

}

/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeBranchId extends AttributeTypeGeneric<BranchId> {
   public AttributeTypeBranchId(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, namespace, name, mediaType, description, taggerType);
   }

   @Override
   public boolean isBranchId() {
      return true;
   }

   @Override
   public String storageStringFromValue(BranchId branch) {
      return branch.getIdString();
   }

   @Override
   public BranchId valueFromStorageString(String storedValue) {
      return BranchId.valueOf(storedValue);
   }
}
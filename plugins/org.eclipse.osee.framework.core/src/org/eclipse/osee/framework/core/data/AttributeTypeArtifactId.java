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
public final class AttributeTypeArtifactId extends AttributeTypeGeneric<ArtifactId> {
   public AttributeTypeArtifactId(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, namespace, name, mediaType, description, taggerType);
   }

   @Override
   public boolean isArtifactId() {
      return true;
   }

   @Override
   public String storageStringFromValue(ArtifactId value) {
      return value.getIdString();
   }

   @Override
   public ArtifactId valueFromStorageString(String storedValue) {
      return ArtifactId.valueOf(storedValue);
   }
}
/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeArtifactId extends AbstractAttributeType<ArtifactId> {
   public AttributeTypeArtifactId(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, namespace, name, mediaType, description, taggerType);
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
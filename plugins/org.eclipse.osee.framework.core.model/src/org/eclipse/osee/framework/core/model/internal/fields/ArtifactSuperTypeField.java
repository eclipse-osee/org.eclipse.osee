/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.internal.fields;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactSuperTypeField extends CollectionField<ArtifactType> {

   private final IArtifactType baseType;

   public ArtifactSuperTypeField(IArtifactType baseType, Collection<ArtifactType> superTypes) {
      super(superTypes);
      this.baseType = baseType;
   }

   @Override
   protected Collection<ArtifactType> checkInput(Collection<ArtifactType> input) {
      Collection<ArtifactType> toReturn = Collections.emptyList();
      if (input == null || input.isEmpty()) {
         if (baseType.notEqual(CoreArtifactTypes.Artifact)) {
            throw new OseeInvalidInheritanceException(
               "All artifacts must inherit from [Artifact] - attempted make [%s] have null inheritance", baseType);
         }
      } else {
         if (input.contains(baseType)) {
            throw new OseeInvalidInheritanceException("Circular inheritance detected for artifact type [%s]", baseType);
         }
         toReturn = input;
      }
      return toReturn;
   }
}
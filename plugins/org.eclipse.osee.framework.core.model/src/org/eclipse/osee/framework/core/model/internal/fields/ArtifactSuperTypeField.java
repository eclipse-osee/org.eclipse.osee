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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactSuperTypeField extends CollectionField<ArtifactTypeToken> {

   private final ArtifactTypeToken baseType;

   public ArtifactSuperTypeField(ArtifactTypeToken baseType, Collection<ArtifactTypeToken> superTypes) {
      super(superTypes);
      this.baseType = baseType;
   }

   @Override
   protected Collection<ArtifactTypeToken> checkInput(Collection<ArtifactTypeToken> input) {
      Collection<ArtifactTypeToken> toReturn = Collections.emptyList();
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
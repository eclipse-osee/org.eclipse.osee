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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public interface ArtifactTypeToken extends NamedId, ArtifactTypeId {
   ArtifactTypeToken SENTINEL = valueOf(Id.SENTINEL, Named.SENTINEL);

   public static ArtifactTypeToken valueOf(String id) {
      return valueOf(Long.valueOf(id), Named.SENTINEL);
   }

   public static ArtifactTypeToken valueOf(long id, String name, ArtifactTypeToken... superTypes) {
      return create(id, NamespaceToken.OSEE, name, false, null, superTypes);
   }

   boolean isAbstract();

   List<ArtifactTypeToken> getSuperTypes();

   public static ArtifactTypeToken create(Long id, NamespaceToken namespace, String name, boolean isAbstract, AttributeMultiplicity attributeTypes, ArtifactTypeToken... superTypes) {
      final class ArtifactTypeTokenImpl extends NamedIdBase implements ArtifactTypeToken {
         private final boolean isAbstract;
         private final List<ArtifactTypeToken> superTypes;
         private final AttributeMultiplicity attributeTypes;

         public ArtifactTypeTokenImpl(Long id, String name, boolean isAbstract, AttributeMultiplicity attributeTypes, ArtifactTypeToken... superTypes) {
            super(id, name);
            this.isAbstract = isAbstract;
            this.superTypes = Arrays.asList(superTypes);
            this.attributeTypes = attributeTypes;
            if (superTypes.length > 1 && this.superTypes.contains(Artifact)) {
               throw new OseeArgumentException("Multiple super types for artifact type [%s] and and supertype Artifact",
                  name);
            }
            // since each superType has already run the following loop, they already have all their inherited multiplicity
            for (ArtifactTypeToken superType : superTypes) {
               attributeTypes.putAll(((ArtifactTypeTokenImpl) superType).attributeTypes);
            }
         }

         @Override
         public boolean isAbstract() {
            return isAbstract;
         }

         @Override
         public List<ArtifactTypeToken> getSuperTypes() {
            return superTypes;
         }
      }
      return new ArtifactTypeTokenImpl(id, name, isAbstract, attributeTypes, superTypes);
   }

}
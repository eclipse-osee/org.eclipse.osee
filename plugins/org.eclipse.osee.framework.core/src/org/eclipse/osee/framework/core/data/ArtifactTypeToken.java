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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
      return new AttributeMultiplicity(id, NamespaceToken.OSEE, name, false, Arrays.asList(superTypes)).get();
   }

   public static ArtifactTypeToken create(Long id, NamespaceToken namespace, String name, boolean isAbstract, List<ArtifactTypeToken> superTypes) {
      return new AttributeMultiplicity(id, namespace, name, isAbstract, superTypes).get();
   }

   default boolean inheritsFrom(ArtifactTypeToken otherType) {
      if (equals(otherType)) {
         return true;
      } else {
         for (ArtifactTypeToken superType : getSuperTypes()) {
            if (superType.inheritsFrom(otherType)) {
               return true;
            }
         }
      }
      return false;
   }

   default boolean inheritsFromAny(Collection<ArtifactTypeToken> artTypes) {
      for (ArtifactTypeToken inheritType : artTypes) {
         if (this.inheritsFrom(inheritType)) {
            return true;
         }
      }
      return false;
   }

   default boolean inheritsFrom(ArtifactTypeId otherType) {
      if (equals(otherType)) {
         return true;
      } else {
         for (ArtifactTypeToken superType : getSuperTypes()) {
            if (superType.inheritsFrom(otherType)) {
               return true;
            }
         }
      }
      return false;
   }

   List<ArtifactTypeToken> getDirectDescendantTypes();

   default List<ArtifactTypeToken> getAllDescendantTypes() {
      List<ArtifactTypeToken> allDescendantTypes = new ArrayList<>();
      getAllDescendantTypes(allDescendantTypes);
      return allDescendantTypes;
   }

   default void getAllDescendantTypes(List<ArtifactTypeToken> allDescendantTypes) {
      for (ArtifactTypeToken descendant : getDirectDescendantTypes()) {
         descendant.getAllDescendantTypes(allDescendantTypes);
      }
   }

   boolean isAbstract();

   List<ArtifactTypeToken> getSuperTypes();

   public static ArtifactTypeToken create(Long id, NamespaceToken namespace, String name, boolean isAbstract, AttributeMultiplicity attributeTypes, List<ArtifactTypeToken> superTypes) {
      final class ArtifactTypeTokenImpl extends NamedIdBase implements ArtifactTypeToken {
         private final boolean isAbstract;
         private final List<ArtifactTypeToken> superTypes;
         private final List<ArtifactTypeToken> directDescendants = new ArrayList<>(4);
         private final AttributeMultiplicity attributeTypes;

         public ArtifactTypeTokenImpl(Long id, String name, boolean isAbstract, AttributeMultiplicity attributeTypes, List<ArtifactTypeToken> superTypes) {
            super(id, name);
            this.isAbstract = isAbstract;
            this.superTypes = superTypes;
            this.attributeTypes = attributeTypes;
            if (superTypes.size() > 1 && this.superTypes.contains(Artifact)) {
               throw new OseeArgumentException("Multiple super types for artifact type [%s] and and supertype Artifact",
                  name);
            }
            // since each superType has already run the following loop, they already have all their inherited multiplicity
            for (ArtifactTypeToken superType : superTypes) {
               attributeTypes.putAll(((ArtifactTypeTokenImpl) superType).attributeTypes);
            }

            for (ArtifactTypeToken superType : superTypes) {
               ((ArtifactTypeTokenImpl) superType).addDirectDescendantType(this);
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

         @Override
         public List<ArtifactTypeToken> getDirectDescendantTypes() {
            return directDescendants;
         }

         public void addDirectDescendantType(ArtifactTypeToken descendantType) {
            directDescendants.add(descendantType);
         }
      }
      return new ArtifactTypeTokenImpl(id, name, isAbstract, attributeTypes, superTypes);
   }
}
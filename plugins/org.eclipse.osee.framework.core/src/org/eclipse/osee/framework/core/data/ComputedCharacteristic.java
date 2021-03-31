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

package org.eclipse.osee.framework.core.data;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdDescription;

/**
 * @author Stephen J. Molaro
 */
public abstract class ComputedCharacteristic<T> extends NamedIdDescription implements ComputedCharacteristicToken<T> {

   private final TaggerTypeToken taggerType;
   private final NamespaceToken namespace;
   protected final List<AttributeTypeGeneric<T>> typesToCompute;
   protected final Set<DisplayHint> displayHints;

   public ComputedCharacteristic(Long id, String name, TaggerTypeToken taggerType, NamespaceToken namespace, String description, List<AttributeTypeGeneric<T>> typesToCompute, Set<DisplayHint> displayHints) {
      super(id, name, description);
      this.namespace = namespace;
      this.taggerType = taggerType;
      this.typesToCompute = typesToCompute;
      this.displayHints = displayHints;
   }

   public ComputedCharacteristic(Long id, String name, TaggerTypeToken taggerType, NamespaceToken namespace, String description, List<AttributeTypeGeneric<T>> typesToCompute) {
      this(id, name, taggerType, namespace, description, typesToCompute, Collections.emptySet());
   }

   public ComputedCharacteristic(Long id, String name, TaggerTypeToken taggerType, NamespaceToken namespace, String description, List<AttributeTypeGeneric<T>> typesToCompute, DisplayHint... displayHints) {
      this(id, name, taggerType, namespace, description, typesToCompute,
         org.eclipse.osee.framework.jdk.core.util.Collections.asHashSet(displayHints));
   }

   @Override
   public List<AttributeTypeGeneric<T>> getAttributeTypesToCompute() {
      return typesToCompute;
   }

   @Override
   public String getMediaType() {
      return MediaType.TEXT_PLAIN;
   }

   @Override
   public TaggerTypeToken getTaggerType() {
      return taggerType;
   }

   @Override
   public NamespaceToken getNamespace() {
      return namespace;
   }

   @Override
   public Set<DisplayHint> getDisplayHints() {
      return displayHints;
   }

   /**
    * return true in the case that the computed type only has two valid values. Should be used in isMultiplicityValid()
    * if only two values are desired.
    */
   protected boolean exactlyTwoValues(ArtifactTypeToken artifactType) {
      if (typesToCompute.size() != 2) {
         return false;
      }
      for (AttributeTypeGeneric<T> attributeType : typesToCompute) {
         if (artifactType.getMax(attributeType) > 1) {
            return false;
         }
      }
      return true;
   }

   /**
    * return true in the case that the computed type can have any number of values, as long as there are at least 2.
    * Should be used in isMultiplicityValid().
    */
   protected boolean atLeastTwoValues(ArtifactTypeToken artifactType) {
      if (typesToCompute.isEmpty()) {
         return false;
      }
      for (AttributeTypeGeneric<T> attributeType : typesToCompute) {
         if (typesToCompute.size() == 1 && artifactType.getMax(attributeType) == 1) {
            return false;
         }
      }
      return true;
   }
}
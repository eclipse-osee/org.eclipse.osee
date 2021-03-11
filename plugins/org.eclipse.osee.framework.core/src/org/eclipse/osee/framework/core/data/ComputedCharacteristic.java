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
   protected final List<AttributeTypeGeneric<T>> attributeTypes;
   protected final Set<DisplayHint> displayHints;

   public ComputedCharacteristic(Long id, String name, TaggerTypeToken taggerType, NamespaceToken namespace, String description, List<AttributeTypeGeneric<T>> attributeTypes, Set<DisplayHint> displayHints) {
      super(id, name, description);
      this.namespace = namespace;
      this.taggerType = taggerType;
      this.attributeTypes = attributeTypes;
      this.displayHints = displayHints;
   }

   public ComputedCharacteristic(Long id, String name, TaggerTypeToken taggerType, NamespaceToken namespace, String description, List<AttributeTypeGeneric<T>> attributeTypes) {
      this(id, name, taggerType, namespace, description, attributeTypes, Collections.emptySet());
   }

   public ComputedCharacteristic(Long id, String name, TaggerTypeToken taggerType, NamespaceToken namespace, String description, List<AttributeTypeGeneric<T>> attributeTypes, DisplayHint... displayHints) {
      this(id, name, taggerType, namespace, description, attributeTypes,
         org.eclipse.osee.framework.jdk.core.util.Collections.asHashSet(displayHints));
   }

   @Override
   public List<AttributeTypeGeneric<T>> getAttributeTypes() {
      return attributeTypes;
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
   private boolean exactlyTwoValues(ArtifactTypeToken artifactType) {
      if (attributeTypes.size() > 2) {
         return false;
      }
      for (AttributeTypeGeneric<T> attributeType : attributeTypes) {
         if (artifactType.getMax(attributeType) > 1) {
            return false;
         }
      }
      return true;
   }
}
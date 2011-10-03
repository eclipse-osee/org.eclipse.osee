/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactData implements AttributeContainer {

   private final HashCollection<IAttributeType, Attribute<?>> attributes =
      new HashCollection<IAttributeType, Attribute<?>>(false, LinkedList.class, 12);

   private boolean isLoaded;

   @Override
   public boolean isLoaded() {
      return isLoaded;
   }

   @Override
   public void setLoaded(boolean value) {
      this.isLoaded = value;
      if (value == true) {
         onLoaded();
      }
   }

   private void onLoaded() {
      //      computeLastDateModified();
      //    artifact.meetMinimumAttributeCounts(false);
   }

   @Override
   public int getCount(IAttributeType type) throws OseeCoreException {
      return getAttributesByModificationType(type, ModificationType.getCurrentModTypes()).size();
   }

   @Override
   public void add(IAttributeType type, Attribute<?> attribute) {
      attributes.put(type, attribute);
   }

   @Override
   public Collection<IAttributeType> getAttributeTypes() throws OseeCoreException {
      return attributes.keySet();
   }

   @Override
   public <T> List<Attribute<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      return Collections.castAll(getAttributesByModificationType(attributeType, ModificationType.getCurrentModTypes()));
   }

   private List<Attribute<?>> getAttributesByModificationType(Set<ModificationType> allowedModTypes) throws OseeCoreException {
      ensureAttributesLoaded();
      return filterByModificationType(attributes.getValues(), allowedModTypes);
   }

   private List<Attribute<?>> getAttributesByModificationType(IAttributeType attributeType, Set<ModificationType> allowedModTypes) throws OseeCoreException {
      ensureAttributesLoaded();
      return filterByModificationType(attributes.getValues(attributeType), allowedModTypes);
   }

   private static List<Attribute<?>> filterByModificationType(Collection<Attribute<?>> attributes, Set<ModificationType> allowedModTypes) {
      List<Attribute<?>> filteredList = new ArrayList<Attribute<?>>();
      if (allowedModTypes != null && !allowedModTypes.isEmpty() && attributes != null && !attributes.isEmpty()) {
         for (Attribute<?> attribute : attributes) {
            if (allowedModTypes.contains(attribute.getModificationType())) {
               filteredList.add(attribute);
            }
         }
      }
      return filteredList;
   }

   private void ensureAttributesLoaded() throws OseeCoreException {
      //      if (!isLoaded() && isInDb()) {
      //         ArtifactLoader.loadArtifactData(this, LoadLevel.ATTRIBUTE);
      //      }
   }
}

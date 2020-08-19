/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.core.model.internal.fields;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.AbstractOseeField;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactTypeAttributesField extends AbstractOseeField<Map<BranchId, Collection<AttributeTypeToken>>> {

   private final Map<BranchId, Collection<AttributeTypeToken>> validityMap;

   public ArtifactTypeAttributesField(Map<BranchId, Collection<AttributeTypeToken>> validityMap) {
      super();
      this.validityMap = validityMap;
   }

   @Override
   public Map<BranchId, Collection<AttributeTypeToken>> get() {
      return new HashMap<>(validityMap);
   }

   public void put(BranchId branch, Collection<AttributeTypeToken> attributes) {
      Collection<AttributeTypeToken> current = validityMap.get(branch);
      validityMap.put(branch, attributes);
      if (Compare.isDifferent(current, attributes)) {
         isDirty = true;
      }
   }

   @Override
   public void set(Map<BranchId, Collection<AttributeTypeToken>> attributeTypeMap) {
      Conditions.checkNotNull(attributeTypeMap, "attribute type map input");
      boolean isDifferent = Compare.isDifferent(get(), attributeTypeMap);
      if (isDifferent) {
         validityMap.clear();
         for (Entry<BranchId, Collection<AttributeTypeToken>> entry : attributeTypeMap.entrySet()) {
            // Ensure we are using a hash set - don't use putAll
            set(entry.getKey(), entry.getValue());
         }
      }
      isDirty |= isDifferent;
   }

   private void set(BranchId branch, Collection<AttributeTypeToken> attributeTypes) {
      Conditions.checkNotNull(branch, "branch");
      Conditions.checkNotNull(attributeTypes, "attribute types list");

      if (attributeTypes.isEmpty()) {
         validityMap.remove(branch);
      } else {
         Collection<AttributeTypeToken> cachedItems = validityMap.get(branch);
         if (cachedItems == null) {
            cachedItems = new HashSet<>(attributeTypes);
            validityMap.put(branch, cachedItems);
         } else {
            cachedItems.clear();
            cachedItems.addAll(attributeTypes);
         }
      }
   }
}
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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeremy A. Midvidy
 */
public class AttribtueMultiplicityResolver {

   private final HashMap<AttributeMultiplicitySelectionOption, Boolean> optionMap;
   private boolean isSingeltonAttribute;
   private boolean isRemovalAllowedAttr;
   private final AttributeTypeId attributeType;
   private final Collection<? extends Artifact> artifacts;

   public AttribtueMultiplicityResolver(AttributeTypeId attributeType) {
      this(attributeType, new ArrayList<Artifact>());
   }

   public AttribtueMultiplicityResolver(AttributeTypeId attributeType, Collection<? extends Artifact> artifacts) {
      this.attributeType = attributeType;
      this.artifacts = artifacts;
      int minVal = AttributeTypeManager.getMinOccurrences(attributeType);
      int maxVal = AttributeTypeManager.getMaxOccurrences(attributeType);
      optionMap = AttributeMultiplicitySelectionOption.getOptionMap();
      if (minVal == 0 && maxVal == 0) {
         isSingeltonAttribute = false;
         isRemovalAllowedAttr = false;
      } else if (minVal == 0 && maxVal == 1) {
         isSingeltonAttribute = true;
         isRemovalAllowedAttr = true;
         optionMap.put(AttributeMultiplicitySelectionOption.AddSelection, true);
      } else if (minVal == 0 && maxVal == Integer.MAX_VALUE) {
         isSingeltonAttribute = false;
         isRemovalAllowedAttr = true;
         for (AttributeMultiplicitySelectionOption key : optionMap.keySet()) {
            optionMap.put(key, true);
         }
      } else if (minVal == 1 && maxVal == 1) {
         isSingeltonAttribute = true;
         isRemovalAllowedAttr = false;
         optionMap.put(AttributeMultiplicitySelectionOption.ReplaceAll, true);
      } else if (minVal == 1 && maxVal == Integer.MAX_VALUE) {
         isSingeltonAttribute = false;
         isRemovalAllowedAttr = false;
         optionMap.put(AttributeMultiplicitySelectionOption.AddSelection, true);
         optionMap.put(AttributeMultiplicitySelectionOption.ReplaceAll, true);
      }
   }

   public boolean isSingeltonAttribute() {
      return isSingeltonAttribute;
   }

   public boolean isRemovalAllowed() {
      if (artifacts.isEmpty()) {
         return isRemovalAllowedAttr;
      }
      boolean notAllowed = false;
      for (Artifact art : artifacts) {
         notAllowed = art.isAttributeTypeValid(attributeType);
         if (notAllowed) {
            break;
         }
      }
      return isRemovalAllowedAttr || !notAllowed;
   }

   public Set<AttributeMultiplicitySelectionOption> getSelectionOptions() {
      Set<AttributeMultiplicitySelectionOption> ret = new HashSet<>();
      if (!isSingeltonAttribute && optionMap.get(AttributeMultiplicitySelectionOption.AddSelection).equals(true)) {
         ret.add(AttributeMultiplicitySelectionOption.AddSelection);
      }
      if (!isSingeltonAttribute && optionMap.get(AttributeMultiplicitySelectionOption.DeleteSelected).equals(true)) {
         ret.add(AttributeMultiplicitySelectionOption.DeleteSelected);
      }
      if (optionMap.get(AttributeMultiplicitySelectionOption.ReplaceAll).equals(true)) {
         ret.add(AttributeMultiplicitySelectionOption.ReplaceAll);
      }
      if (isRemovalAllowed() && optionMap.get(AttributeMultiplicitySelectionOption.RemoveAll).equals(true)) {
         ret.add(AttributeMultiplicitySelectionOption.RemoveAll);
      }
      return ret;
   }

}

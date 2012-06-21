/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.attribute;

import java.util.Set;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public class AttributeModTypeFilter extends AttributeFilter {
   private final Set<ModificationType> toMatch;

   public AttributeModTypeFilter(DeletionFlag includeDeleted) {
      if (includeDeleted.areDeletedAllowed()) {
         toMatch = ModificationType.getAllCurrentModTypes();
      } else {
         toMatch = ModificationType.getCurrentModTypes();
      }
   }

   @Override
   public boolean accept(Attribute<?> attribute) {
      return toMatch != null && toMatch.contains(attribute.getModificationType());
   }
}

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
package org.eclipse.osee.framework.core.model.access.exp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.jdk.core.type.Pair;

public class AttributeTypeFilter implements IAcceptFilter<IAttributeType> {
   private final PermissionEnum toMatch;
   private final Map<IBasicArtifact<?>, IAttributeType> itemsToCheck;

   public AttributeTypeFilter(PermissionEnum toMatch, Collection<Pair<IBasicArtifact<?>, IAttributeType>> items) {
      this.toMatch = toMatch;
      this.itemsToCheck = new HashMap<IBasicArtifact<?>, IAttributeType>();

      for (Pair<IBasicArtifact<?>, IAttributeType> pair : items) {
         itemsToCheck.put(pair.getFirst(), pair.getSecond());
      }
   }

   @Override
   public IAttributeType getObject(Object object) {
      IAttributeType toReturn = null;
      if (object instanceof IAttributeType) {
         toReturn = (IAttributeType) object;
      }
      return toReturn;
   }

   @Override
   public boolean accept(IAttributeType item, IBasicArtifact<?> artifact, PermissionEnum permission) {
      boolean result = false;

      if (itemsToCheck.containsKey(artifact)) {
         result = permission.matches(toMatch);
      }
      return result;
   }

}

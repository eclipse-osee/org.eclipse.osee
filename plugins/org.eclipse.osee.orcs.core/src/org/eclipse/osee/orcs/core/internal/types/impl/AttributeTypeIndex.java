/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeIndex extends TokenTypeIndex<AttributeTypeToken, XAttributeType> {

   private final Set<AttributeTypeId> taggables = new HashSet<>();

   public AttributeTypeIndex() {
      super(AttributeTypeToken.SENTINEL);
   }

   @Override
   public void put(AttributeTypeToken token, XAttributeType dslType) {
      super.put(token, dslType);
      if (Strings.isValid(dslType.getTaggerId())) {
         taggables.add(token);
      }
   }

   public Collection<AttributeTypeId> getAllTaggable() {
      return taggables;
   }

}
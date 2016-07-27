/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeIndex extends TokenTypeIndex<IAttributeType, XAttributeType> {

   private final Set<IAttributeType> taggables = new HashSet<>();

   @Override
   public void put(IAttributeType token, XAttributeType dslType) {
      super.put(token, dslType);
      if (Strings.isValid(dslType.getTaggerId())) {
         taggables.add(token);
      }
   }

   public Collection<? extends IAttributeType> getAllTaggable() {
      return taggables;
   }

}
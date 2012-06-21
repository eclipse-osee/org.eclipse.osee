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

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class AttributeValueFilter<T> extends AttributeFilter {
   private final T toMatch;

   public AttributeValueFilter(T value) {
      toMatch = value;
   }

   @Override
   public boolean accept(Attribute<?> attribute) throws OseeCoreException {
      return attribute.getValue().equals(toMatch);
   }
}

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
public abstract class AttributeFilter {

   public abstract boolean accept(Attribute<?> attribute) throws OseeCoreException;

   private abstract static class BooleanOperation extends AttributeFilter {
      protected final AttributeFilter filter1;
      protected final AttributeFilter filter2;

      public BooleanOperation(AttributeFilter filter1, AttributeFilter filter2) {
         this.filter1 = filter1;
         this.filter2 = filter2;
      }
   }

   public AttributeFilter or(AttributeFilter anotherFilter) {
      return new BooleanOperation(this, anotherFilter) {
         @Override
         public boolean accept(Attribute<?> attribute) throws OseeCoreException {
            return filter1.accept(attribute) || filter2.accept(attribute);
         }
      };
   }

   public AttributeFilter and(AttributeFilter anotherFilter) {
      return new BooleanOperation(this, anotherFilter) {
         @Override
         public boolean accept(Attribute<?> attribute) throws OseeCoreException {
            return filter1.accept(attribute) && filter2.accept(attribute);
         }
      };
   }
}
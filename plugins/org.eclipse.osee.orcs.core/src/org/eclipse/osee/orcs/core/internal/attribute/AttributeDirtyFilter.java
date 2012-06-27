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

/**
 * @author Roberto E. Escobar
 */
public class AttributeDirtyFilter extends AttributeFilter {

   public static enum DirtyFlag {
      DIRTY,
      NON_DIRTY,
   }

   private final DirtyFlag dirtyFlag;

   public AttributeDirtyFilter(DirtyFlag includeDirty) {
      this.dirtyFlag = includeDirty;
   }

   @Override
   public boolean accept(Attribute<?> attribute) {
      boolean result = true;
      if (dirtyFlag == DirtyFlag.DIRTY) {
         result = attribute.isDirty();
      } else if (dirtyFlag == DirtyFlag.NON_DIRTY) {
         result = !attribute.isDirty();
      }
      return result;
   }
}

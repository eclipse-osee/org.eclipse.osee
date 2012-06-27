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

import org.eclipse.osee.framework.core.enums.DeletionFlag;

/**
 * @author Roberto E. Escobar
 */
public class AttributeDeletedFilter extends AttributeFilter {

   private final boolean checkNeeded;

   public AttributeDeletedFilter(DeletionFlag includeDeleted) {
      this.checkNeeded = !includeDeleted.areDeletedAllowed();
   }

   @Override
   public boolean accept(Attribute<?> attribute) {
      boolean result = true;
      if (checkNeeded) {
         result = !attribute.isDeleted();
      }
      return result;
   }
}

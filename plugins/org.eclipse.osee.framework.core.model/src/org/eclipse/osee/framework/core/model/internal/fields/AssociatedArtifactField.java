/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.internal.fields;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class AssociatedArtifactField extends AbstractOseeField<Integer> {

   private Integer artId;

   public AssociatedArtifactField(Integer artId) {
      super();
      this.artId = artId;
   }

   @Override
   public Integer get() throws OseeCoreException {
      return artId;
   }

   @Override
   public void set(Integer artId) throws OseeCoreException {
      boolean wasDifferent = isDifferent(get(), artId);
      if (wasDifferent) {
         this.artId = artId;
      }
      isDirty |= wasDifferent;
   }

   private boolean isDifferent(Integer artId1, Integer artId2) {
      boolean result = false;
      if (artId1 != null && artId2 == null || artId1 == null && artId2 != null) {
         result = true;
      } else {
         result = artId1 != artId2;
      }
      return result;
   }
}
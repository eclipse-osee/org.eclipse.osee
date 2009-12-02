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
package org.eclipse.osee.framework.core.internal.fields;

import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class AssociatedArtifactField extends AbstractOseeField<IBasicArtifact<?>> {

   private IBasicArtifact<?> basicArtifact;

   public AssociatedArtifactField(IBasicArtifact<?> basicArtifact) {
      super();
      this.basicArtifact = basicArtifact;
   }

   @Override
   public IBasicArtifact<?> get() throws OseeCoreException {
      return basicArtifact;
   }

   @Override
   public void set(IBasicArtifact<?> artifact) throws OseeCoreException {
      boolean wasDifferent = isDifferent(get(), artifact);
      if (wasDifferent) {
         this.basicArtifact = artifact;
      }
      isDirty |= wasDifferent;
   }

   private boolean isDifferent(IBasicArtifact<?> art1, IBasicArtifact<?> art2) {
      boolean result = false;
      if (art1 != null && art2 == null || art1 == null && art2 != null) {
         result = true;
      } else {
         result = art1.getArtId() != art2.getArtId();
      }
      return result;
   }
}
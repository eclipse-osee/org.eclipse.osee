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
package org.eclipse.osee.framework.skynet.core.types.field;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Roberto E. Escobar
 */
public class AssociatedArtifactField extends AbstractOseeField<IArtifact> {

   private final Branch branch;
   private final BranchCache cache;

   public AssociatedArtifactField(BranchCache cache, Branch branch) {
      super();
      this.branch = branch;
      this.cache = cache;
   }

   @Override
   public IArtifact get() throws OseeCoreException {
      return cache.getAssociatedArtifact(branch);
   }

   @Override
   public void set(IArtifact artifact) throws OseeCoreException {
      IArtifact oldArtifact = cache.getAssociatedArtifact(branch);
      cache.setAssociatedArtifact(branch, artifact);
      IArtifact newArtifact = cache.getAssociatedArtifact(branch);
      isDirty |= ChangeUtil.isDifferent(oldArtifact, newArtifact);
   }
}
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

import org.eclipse.osee.framework.core.data.AbstractOseeField;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.ChangeUtil;

/**
 * @author Roberto E. Escobar
 */
public class AssociatedArtifactField extends AbstractOseeField<IBasicArtifact<?>> {

   private IBasicArtifact<?> artifact;

   public AssociatedArtifactField() {
      super();
      artifact = null;
   }

   @Override
   public IBasicArtifact<?> get() throws OseeCoreException {
      return artifact;
   }

   @Override
   public void set(IBasicArtifact<?> newArtifact) throws OseeCoreException {
      boolean wasDifferent = ChangeUtil.isDifferent(get(), resolve(newArtifact));
      if (wasDifferent) {
         this.artifact = newArtifact;
      }
      isDirty |= wasDifferent;
   }

   private IBasicArtifact<?> resolve(IBasicArtifact<?> artifact) {
      IBasicArtifact<?> toReturn = artifact;
      if (artifact != null) {
         //         // Artifact has already been loaded so check
         //         // TODO: this method should allow the artifact to be on any branch, not just common
         //         if (artifact instanceof Artifact) {
         //            if (artifact.getBranch() != getCommonBranch()) {
         //               throw new OseeArgumentException(
         //                     "Setting associated artifact for branch only valid for common branch artifact.");
         //            }
         //         }
         //         IArtifact lastArtifact = branchToAssociatedArtifact.get(branch);
         //         if (lastArtifact != null) {
         //            if (!lastArtifact.equals(artifact)) {
         //               branchToAssociatedArtifact.put(branch, artifact);
         //            }
         //         } else {
         //            branchToAssociatedArtifact.put(branch, artifact);
         //         }
         //      } else {
         //         branchToAssociatedArtifact.remove(branch);
      }
      return toReturn;
   }
}
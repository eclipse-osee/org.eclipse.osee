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
package org.eclipse.osee.framework.skynet.core.artifact.checks;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCheck;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author John Misinco
 */
public class AssociatedArtifactCheck extends ArtifactCheck {

   @Override
   public IStatus isDeleteable(Collection<Artifact> artifacts) throws OseeCoreException {
      List<Integer> artIdsToCheck = new LinkedList<>();
      for (Artifact art : artifacts) {
         if (art.isOnBranch(CoreBranches.COMMON_ID)) {
            artIdsToCheck.add(art.getArtId());
         }
      }

      if (!artIdsToCheck.isEmpty()) {
         for (Branch branch : BranchManager.getAll()) {
            if (branch.getBranchState() != BranchState.DELETED && artIdsToCheck.contains(branch.getAssociatedArtifactId())) {
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
                  "Cannot delete artId [%d] because it is the associated artifact of branch [%s]",
                  branch.getAssociatedArtifactId(), branch.getName()));
            }
         }
      }

      return ArtifactCheck.OK_STATUS;
   }
}

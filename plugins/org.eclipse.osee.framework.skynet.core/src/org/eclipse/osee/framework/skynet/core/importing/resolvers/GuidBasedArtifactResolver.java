/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.resolvers;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;

/**
 * @author Ryan Schmitt
 */
public class GuidBasedArtifactResolver extends NewArtifactImportResolver {
   private final boolean createNewIfNotExist;

   public GuidBasedArtifactResolver(ArtifactType primaryArtifactType, ArtifactType secondaryArtifactType, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      super(primaryArtifactType, secondaryArtifactType);
      this.createNewIfNotExist = createNewIfNotExist;
   }

   private boolean guidsMatch(RoughArtifact roughArt, Artifact realArt) {
      String roughGuid = roughArt.getGuid();
      String realGuid = realArt.getGuid();
         return realGuid.equals(roughGuid);
   }

   @Override
   public Artifact resolve(RoughArtifact roughArtifact, Branch branch, Artifact realParent, Artifact root) throws OseeCoreException {
      List<Artifact> descendants = root.getDescendants();
      Artifact realArtifact = null;

      for (Artifact artifact : descendants) {
         if (guidsMatch(roughArtifact, artifact)) {
            translateAttributes(roughArtifact, artifact);
            return artifact;
         }
      }

      if (createNewIfNotExist) {
         realArtifact = super.resolve(roughArtifact, branch, null, root);
      }

      return realArtifact;
   }
}
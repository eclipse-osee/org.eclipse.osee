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
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;

/**
 * @author Ryan Schmitt
 */
public class GuidBasedArtifactResolver extends NewArtifactImportResolver {

   private final boolean createNewIfNotExist;

   public GuidBasedArtifactResolver(IRoughArtifactTranslator translator, IArtifactType primaryArtifactType, IArtifactType secondaryArtifactType, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      super(translator, primaryArtifactType, secondaryArtifactType);
      this.createNewIfNotExist = createNewIfNotExist;
   }

   private boolean guidsMatch(RoughArtifact roughArt, Artifact realArt) {
      String roughGuid = roughArt.getGuid();
      String realGuid = realArt.getGuid();
      return realGuid.equals(roughGuid);
   }

   @Override
   public Artifact resolve(RoughArtifact roughArtifact, BranchId branch, Artifact realParent, Artifact root) {
      List<Artifact> descendants = root.getDescendants();
      Artifact realArtifact = null;

      if (roughArtifact.getGuid() == null) {
         OseeLog.logf(GuidBasedArtifactResolver.class, Level.INFO,
            "Guid based resolver is comparing a null GUID. roughArtifactifact: [%s]. Attributes: [%s]", roughArtifact,
            roughArtifact.getAttributes());
      }

      for (Artifact artifact : descendants) {
         if (guidsMatch(roughArtifact, artifact)) {
            getTranslator().translate(roughArtifact, artifact);
            return artifact;
         }
      }

      if (createNewIfNotExist) {
         realArtifact = super.resolve(roughArtifact, branch, null, root);
      }

      return realArtifact;
   }
}

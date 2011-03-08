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
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.ats.util.VersionManager;
import org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;

public class VersionCommitConfigArtifact implements ICommitConfigArtifact {

   private final Artifact verArt;

   public VersionCommitConfigArtifact(Artifact verArt) throws OseeCoreException {
      this.verArt = verArt;
      VersionManager.ensureVersionArtifact(verArt);
   }

   @Override
   public Result isCreateBranchAllowed() throws OseeCoreException {
      return VersionManager.isCreateBranchAllowed(verArt);
   }

   @Override
   public Result isCommitBranchAllowed() throws OseeCoreException {
      return VersionManager.isCommitBranchAllowed(verArt);
   }

   @Override
   public Branch getParentBranch() throws OseeCoreException {
      return VersionManager.getParentBranch(verArt);
   }

   @Override
   public String toString() {
      return verArt.getName();
   }

   @Override
   public String getFullDisplayName() throws OseeCoreException {
      return VersionManager.getFullDisplayName(verArt);
   }

   public Artifact getVersionArt() {
      return verArt;
   }
}

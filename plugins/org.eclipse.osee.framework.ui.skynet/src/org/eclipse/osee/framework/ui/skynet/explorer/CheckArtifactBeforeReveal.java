/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.explorer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
class CheckArtifactBeforeReveal extends AbstractOperation {

   private final ArtifactData artifactData;

   public CheckArtifactBeforeReveal(ArtifactData artifactData) {
      super("Check Artifact Before Reveal", Activator.PLUGIN_ID);
      this.artifactData = artifactData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(artifactData, "artifact data");

      Artifact artifact = artifactData.getArtifact();
      Conditions.checkNotNull(artifact, "artifact");
      if (artifact.isDeleted()) {
         throw new OseeStateException("The artifact [%s] has been deleted.", artifact.getName());
      } else {
         if (artifact.isHistorical()) {
            artifactData.setArtifact(ArtifactQuery.getArtifactFromToken(artifact));
         }

         if (artifact.isNotRootedInDefaultRoot()) {
            throw new OseeStateException("Artifact [%s] is not rooted in the default hierarchical root",
               artifact.getName());
         }
      }
   }
}

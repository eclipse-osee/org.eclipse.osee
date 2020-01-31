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
package org.eclipse.osee.framework.ui.skynet.cm;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public interface IOseeCmService {

   public static enum ImageType {
      Pcr,
      Task
   };

   default void openArtifact(ArtifactId artifactId, OseeCmEditor oseeCmEditor) {
      Artifact artifact = ArtifactQuery.getArtifactFromId(artifactId, CoreBranches.COMMON);
      RendererManager.openInJob(artifact, PresentationType.GENERALIZED_EDIT);
   }

   default void openArtifacts(String name, Collection<Artifact> artifacts, OseeCmEditor oseeCmEditor) {
      RendererManager.openInJob(artifacts, PresentationType.GENERALIZED_EDIT);
   }

   default boolean isPcrArtifact(Artifact artifact) {
      return false;
   }

   default boolean isBranchesAllCommittedExcept(Artifact art, BranchId branch) {
      return true;
   }

   default KeyedImage getImage(ImageType imageType) {
      return FrameworkImage.HEADING;
   }

   default boolean isWorkFlowBranch(BranchId branch) {
      return false;
   }

   default void commitBranch(Artifact art, IOseeBranch branch, boolean isArchiveSource) {
      //
   }
}
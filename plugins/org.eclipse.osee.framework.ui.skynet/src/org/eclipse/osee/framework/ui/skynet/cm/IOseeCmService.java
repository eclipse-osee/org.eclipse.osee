/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.cm;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

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

   default boolean isWorkFlowBranch(BranchId branch) {
      return false;
   }

   default XResultData commitBranch(Artifact art, BranchToken branch, boolean isArchiveSource, XResultData rd) {
      return rd;
   }
}
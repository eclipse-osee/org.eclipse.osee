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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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

   KeyedImage getImage(ImageType imageType);

   void openArtifact(Artifact artifact, OseeCmEditor oseeCmEditor);

   void openArtifact(String id, OseeCmEditor oseeCmEditor);

   void openArtifacts(String name, Collection<Artifact> artifacts, OseeCmEditor oseeCmEditor);

   boolean isPcrArtifact(Artifact artifact);

   boolean isBranchesAllCommittedExcept(Artifact art, BranchId branch);

   boolean isWorkFlowBranch(BranchId branch);

   void commitBranch(Artifact art, IOseeBranch branch, boolean isArchiveSource);
}

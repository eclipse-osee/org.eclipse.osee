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

package org.eclipse.osee.define.rest.importing.resolvers;

import java.util.List;
import org.eclipse.osee.define.api.importing.RoughArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan Schmitt
 */
public class GuidBasedArtifactResolver extends NewArtifactImportResolver {

   private final boolean createNewIfNotExist;

   public GuidBasedArtifactResolver(TransactionBuilder transaction, IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      super(transaction, translator, primaryArtifactType, secondaryArtifactType);
      this.createNewIfNotExist = createNewIfNotExist;
   }

   private boolean guidsMatch(RoughArtifact roughArt, ArtifactReadable realArt) {
      String roughGuid = roughArt.getGuid();
      String realGuid = realArt.getGuid();
      return realGuid.equals(roughGuid);
   }

   @Override
   public ArtifactId resolve(RoughArtifact roughArtifact, BranchId branch, ArtifactId realParentId, ArtifactId rootId) {
      ArtifactReadable root =
         roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andId(rootId).getArtifact();
      List<ArtifactReadable> descendants = root.getDescendants();
      ArtifactId realArtifact = null;

      if (roughArtifact.getGuid() == null) {
         roughArtifact.getResults().warningf(
            "Guid based resolver is comparing a null GUID. roughArtifactifact: [%s]. Attributes: [%s]", roughArtifact,
            roughArtifact.getAttributes());
      }

      for (ArtifactReadable artifact : descendants) {
         if (guidsMatch(roughArtifact, artifact)) {
            getTranslator().translate(transaction, roughArtifact, artifact);
            return artifact;
         }
      }

      if (createNewIfNotExist) {
         realArtifact = super.resolve(roughArtifact, branch, null, rootId);
      }

      return realArtifact;
   }
}

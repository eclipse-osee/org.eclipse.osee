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

package org.eclipse.osee.framework.skynet.core.importing.resolvers;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;

/**
 * @author Ryan D. Brooks
 */
public class NewArtifactImportResolver implements IArtifactImportResolver {

   private final IRoughArtifactTranslator translator;
   private final ArtifactTypeToken primaryArtifactType;

   public NewArtifactImportResolver(IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType) {
      this.translator = translator;
      this.primaryArtifactType = primaryArtifactType;
   }

   public NewArtifactImportResolver(IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType, ArtifactTypeToken tertiaryArtifactType, ArtifactTypeToken quaternaryArtifactType) {
      this.translator = translator;
      this.primaryArtifactType = primaryArtifactType;
   }

   protected IRoughArtifactTranslator getTranslator() {
      return translator;
   }

   @Override
   public Artifact resolve(final RoughArtifact roughArtifact, final BranchId branch, Artifact realParent, Artifact root) {
      ArtifactTypeToken artifactType = getArtifactType(roughArtifact);

      OseeLog.logf(NewArtifactImportResolver.class, Level.INFO, "New artifact: [%s]. Attributes: [%s]", roughArtifact,
         roughArtifact.getAttributes());

      Artifact realArtifact =
         ArtifactTypeManager.addArtifact(artifactType, branch, null, roughArtifact.getGuid(), null);
      translator.translate(roughArtifact, realArtifact);
      return realArtifact;
   }

   private ArtifactTypeToken getArtifactType(RoughArtifact art) {
      if (!primaryArtifactType.equals(ArtifactTypeId.SENTINEL)) {
         return primaryArtifactType;
      } else {
         return art.getArtifactType();
      }
   }
}
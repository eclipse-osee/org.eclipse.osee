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

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;

/**
 * @author Ryan D. Brooks
 */
public class NewArtifactImportResolver implements IArtifactImportResolver {

   private final IRoughArtifactTranslator translator;
   private final ArtifactTypeToken primaryArtifactType;
   private final ArtifactTypeToken secondaryArtifactType;
   private final ArtifactTypeToken tertiaryArtifactType;
   private final ArtifactTypeToken quaternaryArtifactType;

   public NewArtifactImportResolver(IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType) {
      this.translator = translator;
      this.primaryArtifactType = primaryArtifactType;
      this.secondaryArtifactType = secondaryArtifactType;
      this.tertiaryArtifactType = secondaryArtifactType;
      this.quaternaryArtifactType = secondaryArtifactType;
   }

   public NewArtifactImportResolver(IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType, ArtifactTypeToken tertiaryArtifactType, ArtifactTypeToken quaternaryArtifactType) {
      this.translator = translator;
      this.primaryArtifactType = primaryArtifactType;
      this.secondaryArtifactType = secondaryArtifactType;
      this.tertiaryArtifactType = tertiaryArtifactType;
      this.quaternaryArtifactType = quaternaryArtifactType;
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
      ArtifactTypeToken type = art.getType();
      if (!type.equals(ArtifactTypeId.SENTINEL)) {
         return type;
      } else {
         RoughArtifactKind kind = art.getRoughArtifactKind();
         switch (kind) {
            case PRIMARY:
               return primaryArtifactType;
            case SECONDARY:
               return secondaryArtifactType;
            case TERTIARY:
               return tertiaryArtifactType;
            case QUATERNARY:
               return quaternaryArtifactType;
            case CONTAINER:
               return CoreArtifactTypes.Folder;
            default:
               throw new OseeCoreException("Unknown Artifact Kind " + kind);
         }
      }
   }
}
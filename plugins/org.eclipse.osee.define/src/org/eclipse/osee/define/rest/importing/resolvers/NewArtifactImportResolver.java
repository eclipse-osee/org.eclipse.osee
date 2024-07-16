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

import org.eclipse.osee.define.rest.api.importing.RoughArtifact;
import org.eclipse.osee.define.rest.api.importing.RoughArtifactKind;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public class NewArtifactImportResolver implements IArtifactImportResolver {

   protected final TransactionBuilder transaction;
   protected final IRoughArtifactTranslator translator;
   private final ArtifactTypeToken primaryArtifactType;
   private final ArtifactTypeToken secondaryArtifactType;
   private final ArtifactTypeToken tertiaryArtifactType;
   private final ArtifactTypeToken quaternaryArtifactType;

   public NewArtifactImportResolver(TransactionBuilder transaction, IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType) {
      this.translator = translator;
      this.primaryArtifactType = primaryArtifactType;
      this.secondaryArtifactType = secondaryArtifactType;
      this.tertiaryArtifactType = secondaryArtifactType;
      this.quaternaryArtifactType = secondaryArtifactType;
      this.transaction = transaction;
   }

   public NewArtifactImportResolver(TransactionBuilder transaction, IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType, ArtifactTypeToken tertiaryArtifactType, ArtifactTypeToken quaternaryArtifactType) {
      this.translator = translator;
      this.primaryArtifactType = primaryArtifactType;
      this.secondaryArtifactType = secondaryArtifactType;
      this.tertiaryArtifactType = tertiaryArtifactType;
      this.quaternaryArtifactType = quaternaryArtifactType;
      this.transaction = transaction;
   }

   protected IRoughArtifactTranslator getTranslator() {
      return translator;
   }

   @Override
   public ArtifactId resolve(final RoughArtifact roughArtifact, final BranchId branch, ArtifactId realParent, ArtifactId root) {
      ArtifactTypeToken artifactType = getArtifactType(roughArtifact);

      roughArtifact.getResults().logf("New artifact resolved: [%s].", roughArtifact.getName());

      ArtifactId realArtifact =
         transaction.createArtifact(artifactType, roughArtifact.getName(), roughArtifact.getGuid());
      translator.translate(transaction, roughArtifact, realArtifact);
      return realArtifact;
   }

   protected ArtifactTypeToken getArtifactType(RoughArtifact art) {
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
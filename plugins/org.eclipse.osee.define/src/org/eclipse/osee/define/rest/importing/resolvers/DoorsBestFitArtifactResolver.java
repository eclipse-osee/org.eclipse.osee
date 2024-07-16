/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.define.rest.api.importing.RoughArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author David Miller
 */
public class DoorsBestFitArtifactResolver extends NewArtifactImportResolver {

   private final boolean createNewIfNotExist;

   public DoorsBestFitArtifactResolver(TransactionBuilder transaction, IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      super(transaction, translator, primaryArtifactType, secondaryArtifactType);
      this.createNewIfNotExist = createNewIfNotExist;
   }

   @Override
   public ArtifactId resolve(RoughArtifact roughArtifact, BranchId branch, ArtifactId realParentId, ArtifactId rootId) {
      ArtifactReadable realArtifact = null;
      ArtifactReadable root =
         roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andId(rootId).getArtifact();
      if (roughArtifact.getGuid() != null) {
         realArtifact = findMatchByGUID(roughArtifact, root);
         if (realArtifact == null) {
            realArtifact = findMatchBySysSpec(roughArtifact, root);
         }
         if (realArtifact != null) {
            getTranslator().translate(transaction, roughArtifact, realArtifact);
         }
      } else {
         roughArtifact.getResults().warningf(
            "Doors Best Fit based resolver is comparing a null GUID. roughArtifactifact: [%s]. Attributes: [%s]",
            roughArtifact, roughArtifact.getAttributes());
      }

      if (realArtifact == null && createNewIfNotExist) {
         return super.resolve(roughArtifact, branch, null, rootId);
      }

      return realArtifact;
   }

   private boolean guidsMatch(RoughArtifact roughArt, ArtifactReadable realArt) {
      String roughGuid = roughArt.getGuid();
      String realGuid = realArt.getGuid();
      return realGuid.equals(roughGuid);
   }

   private boolean legacyIdsMatch(RoughArtifact roughArt, ArtifactReadable realArt) {
      // if the roughArtifact contains all of the sys specs that the real artifact has,
      // even if it has more, then it matches
      boolean allContained = true;
      String legacyIds = realArt.getAttributeValuesAsString(CoreAttributeTypes.LegacyId);
      if (Strings.isValid(legacyIds)) {
         List<String> splitIds = Arrays.asList(legacyIds.split(","));
         String roughLegacyIds = roughArt.getRoughAttribute(CoreAttributeTypes.LegacyId.getName());
         List<String> splitRoughIds = Arrays.asList(roughLegacyIds.split(","));
         for (String splitId : splitIds) {
            if (!splitRoughIds.contains(splitId)) {
               allContained = false;
               break;
            }
         }
      } else {
         allContained = false;
      }
      return allContained;
   }

   private ArtifactReadable findMatchByGUID(RoughArtifact roughArtifact, ArtifactReadable root) {

      List<ArtifactReadable> descendants = root.getDescendants();
      ArtifactReadable toReturn = null;
      for (ArtifactReadable artifact : descendants) {
         if (guidsMatch(roughArtifact, artifact)) {
            toReturn = artifact;
            break;
         }
      }
      return toReturn;
   }

   private ArtifactReadable findMatchBySysSpec(RoughArtifact roughArtifact, ArtifactReadable root) {
      List<ArtifactReadable> descendants = root.getDescendants();
      ArtifactReadable toReturn = null;
      for (ArtifactReadable artifact : descendants) {
         if (legacyIdsMatch(roughArtifact, artifact)) {
            toReturn = artifact;
            break;
         }
      }
      return toReturn;
   }
}

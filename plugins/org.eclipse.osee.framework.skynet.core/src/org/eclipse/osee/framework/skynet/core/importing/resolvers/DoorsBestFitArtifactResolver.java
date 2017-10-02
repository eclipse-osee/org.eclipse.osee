/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.resolvers;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;

/**
 * @author David Miller
 */
public class DoorsBestFitArtifactResolver extends NewArtifactImportResolver {

   private final boolean createNewIfNotExist;

   public DoorsBestFitArtifactResolver(IRoughArtifactTranslator translator, IArtifactType primaryArtifactType, IArtifactType secondaryArtifactType, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      super(translator, primaryArtifactType, secondaryArtifactType);
      this.createNewIfNotExist = createNewIfNotExist;
   }

   @Override
   public Artifact resolve(RoughArtifact roughArtifact, BranchId branch, Artifact realParent, Artifact root)  {
      Artifact realArtifact = null;

      if (roughArtifact.getGuid() != null) {
         realArtifact = findMatchByGUID(roughArtifact, root);
         if (realArtifact == null) {
            realArtifact = findMatchBySysSpec(roughArtifact, root);
         }
         if (realArtifact != null) {
            getTranslator().translate(roughArtifact, realArtifact);
         }
      } else {
         OseeLog.logf(DoorsBestFitArtifactResolver.class, Level.INFO,
            "Doors Best Fit based resolver is comparing a null GUID. roughArtifactifact: [%s]. Attributes: [%s]",
            roughArtifact, roughArtifact.getAttributes());
      }

      if (realArtifact == null && createNewIfNotExist) {
         realArtifact = super.resolve(roughArtifact, branch, null, root);
      }

      return realArtifact;
   }

   private boolean guidsMatch(RoughArtifact roughArt, Artifact realArt) {
      String roughGuid = roughArt.getGuid();
      String realGuid = realArt.getGuid();
      return realGuid.equals(roughGuid);
   }

   private boolean legacyIdsMatch(RoughArtifact roughArt, Artifact realArt) {
      // if the roughArtifact contains all of the sys specs that the real artifact has,
      // even if it has more, then it matches
      boolean allContained = true;
      String legacyIds = realArt.getSoleAttributeValueAsString(CoreAttributeTypes.LegacyId, "");
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

   private Artifact findMatchByGUID(RoughArtifact roughArtifact, Artifact root) {
      List<Artifact> descendants = root.getDescendants();
      Artifact toReturn = null;
      for (Artifact artifact : descendants) {
         if (guidsMatch(roughArtifact, artifact)) {
            toReturn = artifact;
            break;
         }
      }
      return toReturn;
   }

   private Artifact findMatchBySysSpec(RoughArtifact roughArtifact, Artifact root) {
      List<Artifact> descendants = root.getDescendants();
      Artifact toReturn = null;
      for (Artifact artifact : descendants) {
         if (legacyIdsMatch(roughArtifact, artifact)) {
            toReturn = artifact;
            break;
         }
      }
      return toReturn;
   }
}

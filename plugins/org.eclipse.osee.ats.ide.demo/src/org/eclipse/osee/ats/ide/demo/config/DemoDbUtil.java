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

package org.eclipse.osee.ats.ide.demo.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.ide.demo.internal.Activator;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class DemoDbUtil {

   public static String INTERFACE_INITIALIZATION = "Interface Initialization";

   public static Collection<Artifact> getSoftwareRequirements(boolean DEBUG, SoftwareRequirementStrs str,
      BranchId branch) {
      return getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirementMsWord, str.name(), branch);
   }

   public static Collection<Artifact> getArtTypeRequirements(boolean DEBUG, ArtifactTypeToken artifactType,
      String artifactNameStr, BranchId branch) {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO,
            "Getting \"" + artifactNameStr + "\" requirement(s) from Branch " + branch.getIdString());
      }
      Collection<Artifact> arts = ArtifactQuery.getArtifactListFromTypeAndName(artifactType, artifactNameStr, branch,
         QueryOption.CONTAINS_MATCH_OPTIONS);

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Found " + arts.size() + " Artifacts");
      }
      return arts;
   }
   public static enum SoftwareRequirementStrs {
      Robot,
      CISST,
      daVinci,
      Functional,
      Event,
      Haptic
   };
   public static String HAPTIC_CONSTRAINTS_REQ = "Haptic Constraints";

   public static Artifact getInterfaceInitializationSoftwareRequirement(boolean DEBUG, BranchId branch) {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Getting \"" + INTERFACE_INITIALIZATION + "\" requirement.");
      }
      return ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirementMsWord,
         INTERFACE_INITIALIZATION, branch);
   }

   public static Collection<IAtsActionableItem> getActionableItems(ArtifactToken... aiTokens) {
      Set<IAtsActionableItem> aias = new HashSet<>();
      for (ArtifactToken aiToken : aiTokens) {
         aias.add(AtsApiService.get().getActionableItemService().getActionableItemById(aiToken));
      }
      return aias;
   }
}
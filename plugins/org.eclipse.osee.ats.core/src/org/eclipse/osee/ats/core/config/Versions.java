/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public class Versions {

   public static Collection<String> getNames(Collection<? extends IAtsVersion> versions) {
      ArrayList<String> names = new ArrayList<>();
      for (IAtsVersion version : versions) {
         names.add(version.getName());
      }
      return names;
   }

   public static String getTargetedVersionStr(IAtsWorkItem workItem, IAtsVersionService versionService)  {
      IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
      if (teamWf != null) {
         IAtsVersion version = versionService.getTargetedVersion(workItem);
         if (version != null) {
            if (!teamWf.getStateMgr().getStateType().isCompletedOrCancelled() && versionService.isReleased(teamWf)) {
               String errStr =
                  "Workflow " + teamWf.getAtsId() + " targeted for released version, but not completed: " + version;
               return "!Error " + errStr;
            }
            return version.getName();
         }
      }
      return "";
   }

   public static List<IAtsVersion> getParallelVersions(IAtsVersion version, IAtsServices services) {
      List<IAtsVersion> parallelVersions = new ArrayList<>();
      for (ArtifactId parallelVersion : services.getRelationResolver().getRelated(services.getArtifact(version),
         AtsRelationTypes.ParallelVersion_Child)) {
         IAtsVersion parallelVer = services.getConfigItemFactory().getVersion(parallelVersion);
         parallelVersions.add(parallelVer);
      }
      return parallelVersions;
   }

   public static void getParallelVersions(IAtsVersion version, Set<ICommitConfigItem> configArts, IAtsServices services) {
      configArts.add(version);
      for (IAtsVersion childArt : getParallelVersions(version, services)) {
         if (!configArts.contains(childArt)) {
            getParallelVersions(childArt, configArts, services);
         }
      }
   }

}

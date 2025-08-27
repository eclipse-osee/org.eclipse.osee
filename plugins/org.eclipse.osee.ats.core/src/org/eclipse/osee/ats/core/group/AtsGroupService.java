/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.core.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsGroupService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsGroupService implements IAtsGroupService {

   private final AtsApi atsApi;

   public AtsGroupService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public Collection<ArtifactToken> getGroups(BranchToken branch) {
      Collection<ArtifactToken> artifacts = null;
      try {
         artifacts = atsApi.getQueryService().getArtifacts(CoreArtifactTypes.UniversalGroup, branch);
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
         artifacts = new LinkedList<>();
      }
      return artifacts;
   }

   @Override
   public Collection<ArtifactToken> getGroupsNotRoot(BranchToken branch) {
      Collection<ArtifactToken> groups = new HashSet<>();
      for (ArtifactToken group : getGroups(branch)) {
         if (!group.getName().equals("Root Artifact")) {
            groups.add(group);
         }
      }
      return groups;
   }

   @Override
   public Collection<ArtifactToken> getGroups(String groupName, BranchToken branch) {
      try {
         return atsApi.getQueryService().getArtifactsFromTypeAndName(CoreArtifactTypes.UniversalGroup, groupName,
            branch, QueryOption.EXACT_MATCH_OPTIONS);
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
      return new ArrayList<>();
   }

   @Override
   public ArtifactToken getGroupOrNull(ArtifactToken groupToken, BranchToken branch) {
      return atsApi.getQueryService().getArtifact(groupToken, branch);
   }

   public ArtifactToken addGroup(String name, BranchToken branch, IAtsChangeSet changes) {
      if (!getGroups(name, branch).isEmpty()) {
         throw new OseeArgumentException("Group Already Exists");
      }
      ArtifactToken groupArt = changes.createArtifact(CoreArtifactTypes.UniversalGroup, branch, name);
      ArtifactToken groupRoot = getTopUniversalGroupArtifact(branch);
      changes.relate(groupRoot, CoreRelationTypes.UniversalGrouping_Members, groupArt);
      return groupArt;
   }

   @Override
   public ArtifactToken addGroup(ArtifactToken groupToken, BranchToken branch, IAtsChangeSet changes) {
      if (getGroupOrNull(groupToken, branch) != null) {
         throw new OseeArgumentException("Group Already Exists");
      }
      return addGroup(groupToken.getName(), branch, changes);
   }

   @Override
   public ArtifactToken getTopUniversalGroupArtifact(BranchId branch) {
      return atsApi.getQueryService().getArtifact(CoreArtifactTokens.UniversalGroupRoot, branch);
   }

}

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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class UniversalGroup {

   public static Collection<Artifact> getGroups(BranchId branch) {
      Collection<Artifact> artifacts = null;
      try {
         artifacts = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.UniversalGroup, branch);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         artifacts = new LinkedList<>();
      }
      return artifacts;
   }

   public static Collection<Artifact> getGroupsNotRoot(BranchId branch) {
      Collection<Artifact> groups = new HashSet<>();
      for (Artifact group : UniversalGroup.getGroups(branch)) {
         if (!group.getName().equals("Root Artifact")) {
            groups.add(group);
         }
      }
      return groups;
   }

   public static Collection<Artifact> getGroups(String groupName, BranchId branch) {
      try {
         return ArtifactQuery.getArtifactListFromTypeAndName(CoreArtifactTypes.UniversalGroup, groupName, branch);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return new ArrayList<>();
   }

   public static Artifact getGroupOrNull(ArtifactToken groupToken, BranchToken branch) {
      return ArtifactQuery.checkArtifactFromId(groupToken, branch);
   }

   public static Artifact addGroup(String name, BranchToken branch, SkynetTransaction transaction) {
      if (!getGroups(name, branch).isEmpty()) {
         throw new OseeArgumentException("Group Already Exists");
      }

      Artifact groupArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.UniversalGroup, branch, name);
      groupArt.persist(transaction);

      Artifact groupRoot = getTopUniversalGroupArtifact(branch);
      groupRoot.addRelation(CoreRelationTypes.UniversalGrouping_Members, groupArt);
      groupRoot.persist(transaction);

      return groupArt;
   }

   public static Artifact addGroup(ArtifactToken groupToken, BranchToken branch, SkynetTransaction transaction) {
      if (getGroupOrNull(groupToken, branch) != null) {
         throw new OseeArgumentException("Group Already Exists");
      }

      Artifact groupArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.UniversalGroup, branch,
         groupToken.getName(), groupToken.getId());
      groupArt.persist(transaction);

      Artifact groupRoot = getTopUniversalGroupArtifact(branch);
      groupRoot.addRelation(CoreRelationTypes.UniversalGrouping_Members, groupArt);
      groupRoot.persist(transaction);

      return groupArt;
   }

   public static Artifact getTopUniversalGroupArtifact(BranchId branch) {
      return ArtifactQuery.getArtifactFromId(CoreArtifactTokens.UniversalGroupRoot, branch);
   }
}
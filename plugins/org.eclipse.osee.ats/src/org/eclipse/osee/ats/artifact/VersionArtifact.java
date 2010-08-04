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
package org.eclipse.osee.ats.artifact;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;

public class VersionArtifact extends Artifact implements ICommitConfigArtifact {
   public static enum VersionReleaseType {
      Released,
      UnReleased,
      Both,
      VersionLocked
   };

   public VersionArtifact(ArtifactFactory parentFactory, String guid, String humandReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humandReadableId, branch, artifactType);
   }

   @Override
   public Result isCreateBranchAllowed() throws OseeCoreException {
      if (getSoleAttributeValue(AtsAttributeTypes.ATS_ALLOW_CREATE_BRANCH, false) == false) {
         return new Result(false, "Branch creation disabled for Version [" + this + "]");
      }
      if (getParentBranch() == null) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Result isCommitBranchAllowed() throws OseeCoreException {
      if (getSoleAttributeValue(AtsAttributeTypes.ATS_ALLOW_COMMIT_BRANCH, false) == false) {
         return new Result(false, "Version [" + this + "] not configured to allow branch commit.");
      }
      if (getParentBranch() == null) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Branch getParentBranch() throws OseeCoreException {
      try {
         String guid = getSoleAttributeValue(AtsAttributeTypes.ATS_BASELINE_BRANCH_GUID, "");
         if (GUID.isValid(guid)) {
            return BranchManager.getBranchByGuid(guid);
         }
      } catch (BranchDoesNotExist ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

   public TeamDefinitionArtifact getParentTeamDefinition() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition, TeamDefinitionArtifact.class).iterator().next();
   }

   public Boolean isReleased() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.ATS_RELEASED, false);
   }

   public Boolean isNextVersion() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.ATS_NEXT_VERSION, false);
   }

   public void getParallelVersions(Set<ICommitConfigArtifact> configArts) throws OseeCoreException {
      configArts.add(this);
      for (VersionArtifact verArt : getRelatedArtifacts(AtsRelationTypes.ParallelVersion_Child, VersionArtifact.class)) {
         verArt.getParallelVersions(configArts);
      }
   }

   public Boolean isVersionLocked() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.ATS_VERSION_LOCKED, false);
   }

   @Override
   public String toString() {
      return getName();
   }

   public void setReleased(boolean released) throws OseeCoreException {
      setSoleAttributeValue(AtsAttributeTypes.ATS_RELEASED, released);
   }

   public void setNextVersion(boolean nextVersion) throws OseeCoreException {
      setSoleAttributeValue(AtsAttributeTypes.ATS_NEXT_VERSION, nextVersion);
   }

   public void setVersionLocked(boolean locked) throws OseeCoreException {
      setSoleAttributeValue(AtsAttributeTypes.ATS_VERSION_LOCKED, locked);
   }

   public String getFullName() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.ATS_FULL_NAME, "");
   }

   public void setFullName(String name) throws OseeCoreException {
      setSoleAttributeValue(AtsAttributeTypes.ATS_FULL_NAME, name);
   }

   public String getDescription() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.ATS_DESCRIPTION, "");
   }

   public void setDescription(String desc) throws OseeCoreException {
      setSoleAttributeValue(AtsAttributeTypes.ATS_DESCRIPTION, desc);
   }

   public Collection<TeamWorkFlowArtifact> getTargetedForTeamArtifacts() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow, TeamWorkFlowArtifact.class);
   }

   @Override
   public String getFullDisplayName() throws OseeCoreException {
      String str = "";
      if (!getName().equals(Artifact.UNNAMED)) {
         str += getName();
      }
      if (!getFullName().equals("")) {
         if (str.equals("")) {
            str = getFullName();
         } else {
            str += " - " + getFullName();
         }
      }
      if (!getDescription().equals("")) {
         if (str.equals("")) {
            str = getDescription();
         } else {
            str += " - " + getDescription();
         }
      }
      return str;
   }

   public TeamDefinitionArtifact getTeamDefinitionArtifact() throws OseeCoreException {
      try {
         return (TeamDefinitionArtifact) getRelatedArtifact(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition);
      } catch (ArtifactDoesNotExist ex) {
         return null;
      }
   }

   public Date getEstimatedReleaseDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.ATS_ESTIMATED_RELEASE_DATE, null);
   }

   public Date getReleaseDate() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.ATS_ESTIMATED_RELEASE_DATE, null);
   }

   public static Set<VersionArtifact> getVersions(Collection<String> teamDefNames) throws OseeCoreException {
      Set<VersionArtifact> teamDefs = new HashSet<VersionArtifact>();
      for (String teamDefName : teamDefNames) {
         teamDefs.add(getSoleVersion(teamDefName));
      }
      return teamDefs;
   }

   /**
    * Refrain from using this method as Version Artifact names can be changed by the user.
    * 
    * @param name
    * @return Version
    */
   public static VersionArtifact getSoleVersion(String name) throws OseeCoreException {
      return (VersionArtifact) AtsCacheManager.getArtifactsByName(AtsArtifactTypes.Version, name).iterator().next();
   }
}

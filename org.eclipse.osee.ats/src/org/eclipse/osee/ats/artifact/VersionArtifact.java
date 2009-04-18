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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;

public class VersionArtifact extends Artifact implements ICommitConfigArtifact {

   public static String ARTIFACT_NAME = "Version";

   public static enum VersionReleaseType {
      Released, UnReleased, Both
   };

   public VersionArtifact(ArtifactFactory parentFactory, String guid, String humandReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humandReadableId, branch, artifactType);
   }

   public Result isCreateBranchAllowed() throws OseeCoreException {
      if (getSoleAttributeValue(ATSAttributes.ALLOW_CREATE_BRANCH.getStoreName(), false) == false) {
         return new Result(false, "Branch creation disabled for Version [" + this + "]");
      }
      if (getParentBranch() == null) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   public Result isCommitBranchAllowed() throws OseeCoreException {
      if (getSoleAttributeValue(ATSAttributes.ALLOW_COMMIT_BRANCH.getStoreName(), false) == false) {
         return new Result(false, "Version [" + this + "] not configured to allow branch commit.");
      }
      if (getParentBranch() == null) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   public Branch getParentBranch() throws OseeCoreException {
      try {
         Integer branchId = getSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(), 0);
         if (branchId != null && branchId > 0) {
            return BranchManager.getBranch(branchId);
         }
      } catch (BranchDoesNotExist ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

   public TeamDefinitionArtifact getParentTeamDefinition() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelation.TeamDefinitionToVersion_TeamDefinition, TeamDefinitionArtifact.class).iterator().next();
   }

   public Boolean isReleased() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName(), false);
   }

   public Boolean isNextVersion() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), false);
   }

   public void getParallelVersions(Set<ICommitConfigArtifact> configArts) throws OseeCoreException {
      configArts.add(this);
      for (VersionArtifact verArt : getRelatedArtifacts(AtsRelation.ParallelVersion_Child, VersionArtifact.class)) {
         verArt.getParallelVersions(configArts);
      }
   }

   @Override
   public String toString() {
      return getDescriptiveName();
   }

   public void setReleased(boolean released) throws OseeCoreException {
      setSoleAttributeValue(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName(), released);
   }

   public void setNextVersion(boolean nextVersion) throws OseeCoreException {
      setSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), nextVersion);
   }

   public String getFullName() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.FULL_NAME_ATTRIBUTE.getStoreName(), "");
   }

   public void setFullName(String name) throws OseeCoreException {
      setSoleAttributeValue(ATSAttributes.FULL_NAME_ATTRIBUTE.getStoreName(), name);
   }

   public String getDescription() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
   }

   public void setDescription(String desc) throws OseeCoreException {
      setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
   }

   public Collection<TeamWorkFlowArtifact> getTargetedForTeamArtifacts() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Workflow, TeamWorkFlowArtifact.class);
   }

   public String getFullDisplayName() throws OseeCoreException {
      String str = "";
      if (!getDescriptiveName().equals(Artifact.UNNAMED)) str += getDescriptiveName();
      if (!getFullName().equals("")) {
         if (str.equals(""))
            str = getFullName();
         else
            str += " - " + getFullName();
      }
      if (!getDescription().equals("")) {
         if (str.equals(""))
            str = getDescription();
         else
            str += " - " + getDescription();
      }
      return str;
   }

   public TeamDefinitionArtifact getTeamDefinitionArtifact() throws OseeCoreException {
      try {
         return (TeamDefinitionArtifact) getRelatedArtifact(AtsRelation.TeamDefinitionToVersion_TeamDefinition);
      } catch (ArtifactDoesNotExist ex) {
         return null;
      }
   }

   public Date getEstimatedReleaseDate() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(), null);
   }

   public Date getReleaseDate() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.RELEASE_DATE_ATTRIBUTE.getStoreName(), null);
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
      return AtsCache.getArtifactsByName(name, VersionArtifact.class).iterator().next();
   }
}

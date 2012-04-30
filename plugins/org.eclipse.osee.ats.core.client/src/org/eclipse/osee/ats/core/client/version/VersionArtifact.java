/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.commit.ICommitConfigArtifact;
import org.eclipse.osee.ats.core.client.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

public class VersionArtifact extends org.eclipse.osee.framework.skynet.core.artifact.Artifact implements ICommitConfigArtifact {

   public VersionArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public TeamDefinitionArtifact getParentTeamDefinition() throws OseeCoreException {
      return this.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition,
         TeamDefinitionArtifact.class).iterator().next();
   }

   public void getParallelVersions(Set<ICommitConfigArtifact> configArts) throws OseeCoreException {
      configArts.add(this);
      for (VersionArtifact childArt : this.getRelatedArtifacts(AtsRelationTypes.ParallelVersion_Child,
         VersionArtifact.class)) {
         childArt.getParallelVersions(configArts);
      }
   }

   public Collection<TeamWorkFlowArtifact> getTargetedForTeamArtifacts() throws OseeCoreException {
      return this.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow,
         TeamWorkFlowArtifact.class);
   }

   public void ensureVersionArtifact() throws OseeCoreException {
      if (!this.isOfType(AtsArtifactTypes.Version)) {
         throw new OseeArgumentException("Artifact should be Version not [%s]", this.getArtifactTypeName());
      }
   }

   public Boolean isVersionLocked() throws OseeCoreException {
      return this.getSoleAttributeValue(AtsAttributeTypes.VersionLocked, false);
   }

   public void setVersionLocked(boolean locked) throws OseeCoreException {
      this.setSoleAttributeValue(AtsAttributeTypes.VersionLocked, locked);
   }

   public Boolean isNextVersion() throws OseeCoreException {
      return this.getSoleAttributeValue(AtsAttributeTypes.NextVersion, false);
   }

   public void setNextVersion(boolean nextVersion) throws OseeCoreException {
      this.setSoleAttributeValue(AtsAttributeTypes.NextVersion, nextVersion);
   }

   public Boolean isReleased() throws OseeCoreException {
      return this.getSoleAttributeValue(AtsAttributeTypes.Released, false);
   }

   public void setReleased(boolean released) throws OseeCoreException {
      this.setSoleAttributeValue(AtsAttributeTypes.Released, released);
   }

   public TeamDefinitionArtifact getTeamDefinitionArtifact() throws OseeCoreException {
      try {
         return (TeamDefinitionArtifact) this.getRelatedArtifact(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition);
      } catch (ArtifactDoesNotExist ex) {
         return null;
      }
   }

   public Branch getBaselineBranch() throws OseeCoreException {
      String branchGuid = this.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, "");
      if (Strings.isValid(branchGuid)) {
         return BranchManager.getBranchByGuid(branchGuid);
      } else {
         return getTeamDefinitionArtifact().getTeamBranch();
      }
   }

   @Override
   public Result isCreateBranchAllowed() throws OseeCoreException {
      if (!getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false)) {
         return new Result(false, "Branch creation disabled for Version [" + this + "]");
      }
      if (getParentBranch() == null) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Result isCommitBranchAllowed() throws OseeCoreException {
      if (!getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false)) {
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
         String guid = getSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, "");
         if (GUID.isValid(guid)) {
            return BranchManager.getBranchByGuid(guid);
         }
      } catch (BranchDoesNotExist ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public String getFullDisplayName() throws OseeCoreException {
      List<String> strs = new ArrayList<String>();
      if (!getName().equals(Artifact.UNNAMED)) {
         strs.add(getName());
      }
      String fullName = getSoleAttributeValue(AtsAttributeTypes.FullName, "");
      if (Strings.isValid(fullName)) {
         strs.add(fullName);
      }
      String description = getSoleAttributeValue(AtsAttributeTypes.Description, "");
      if (Strings.isValid(description)) {
         strs.add(description);
      }
      return Collections.toString(" - ", strs);
   }

   @Override
   public String toString() {
      return getName();
   }

}

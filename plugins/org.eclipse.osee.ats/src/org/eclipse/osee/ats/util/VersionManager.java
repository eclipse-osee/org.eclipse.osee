/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionCommitConfigArtifact;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;

public class VersionManager {

   public static TeamDefinitionArtifact getParentTeamDefinition(Artifact verArt) throws OseeCoreException {
      return verArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition,
         TeamDefinitionArtifact.class).iterator().next();
   }

   public static void getParallelVersions(Artifact verArt, Set<ICommitConfigArtifact> configArts) throws OseeCoreException {
      configArts.add(new VersionCommitConfigArtifact(verArt));
      for (Artifact childArt : verArt.getRelatedArtifacts(AtsRelationTypes.ParallelVersion_Child)) {
         VersionManager.getParallelVersions(childArt, configArts);
      }
   }

   public static Collection<TeamWorkFlowArtifact> getTargetedForTeamArtifacts(Artifact verArt) throws OseeCoreException {
      ensureVersionArtifact(verArt);
      return verArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow,
         TeamWorkFlowArtifact.class);
   }

   public static void ensureVersionArtifact(Artifact verArt) throws OseeCoreException {
      if (!verArt.isOfType(AtsArtifactTypes.Version)) {
         throw new OseeArgumentException("Artifact should be Version not [%s]", verArt.getArtifactTypeName());
      }
   }

   public static Boolean isVersionLocked(Artifact verArt) throws OseeCoreException {
      ensureVersionArtifact(verArt);
      return verArt.getSoleAttributeValue(AtsAttributeTypes.VersionLocked, false);
   }

   public static void setVersionLocked(Artifact verArt, boolean locked) throws OseeCoreException {
      ensureVersionArtifact(verArt);
      verArt.setSoleAttributeValue(AtsAttributeTypes.VersionLocked, locked);
   }

   public static Boolean isNextVersion(Artifact verArt) throws OseeCoreException {
      ensureVersionArtifact(verArt);
      return verArt.getSoleAttributeValue(AtsAttributeTypes.NextVersion, false);
   }

   public static void setNextVersion(Artifact verArt, boolean nextVersion) throws OseeCoreException {
      ensureVersionArtifact(verArt);
      verArt.setSoleAttributeValue(AtsAttributeTypes.NextVersion, nextVersion);
   }

   public static Boolean isReleased(Artifact verArt) throws OseeCoreException {
      ensureVersionArtifact(verArt);
      return verArt.getSoleAttributeValue(AtsAttributeTypes.Released, false);
   }

   public static void setReleased(Artifact verArt, boolean released) throws OseeCoreException {
      ensureVersionArtifact(verArt);
      verArt.setSoleAttributeValue(AtsAttributeTypes.Released, released);
   }

   public static TeamDefinitionArtifact getTeamDefinitionArtifact(Artifact verArt) throws OseeCoreException {
      ensureVersionArtifact(verArt);
      try {
         return (TeamDefinitionArtifact) verArt.getRelatedArtifact(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition);
      } catch (ArtifactDoesNotExist ex) {
         return null;
      }
   }

   public static Branch getBaselineBranch(Artifact verArt) throws OseeCoreException {
      ensureVersionArtifact(verArt);
      String branchGuid = verArt.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, "");
      if (Strings.isValid(branchGuid)) {
         return BranchManager.getBranchByGuid(branchGuid);
      } else {
         return getTeamDefinitionArtifact(verArt).getTeamBranch();
      }
   }

   public static Set<Artifact> getVersions(Collection<String> teamDefNames) {
      Set<Artifact> teamDefs = new HashSet<Artifact>();
      for (String teamDefName : teamDefNames) {
         teamDefs.add(getSoleVersion(teamDefName));
      }
      return teamDefs;
   }

   /**
    * Refrain from using this method as Version Artifact names can be changed by the user.
    */
   public static Artifact getSoleVersion(String name) {
      return AtsCacheManager.getArtifactsByName(AtsArtifactTypes.Version, name).iterator().next();
   }

   public static Result isCreateBranchAllowed(Artifact verArt) throws OseeCoreException {
      if (!verArt.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false)) {
         return new Result(false, "Branch creation disabled for Version [" + verArt + "]");
      }
      if (getParentBranch(verArt) == null) {
         return new Result(false, "Parent Branch not configured for Version [" + verArt + "]");
      }
      return Result.TrueResult;
   }

   public static Result isCommitBranchAllowed(Artifact verArt) throws OseeCoreException {
      if (!verArt.getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false)) {
         return new Result(false, "Version [" + verArt + "] not configured to allow branch commit.");
      }
      if (getParentBranch(verArt) == null) {
         return new Result(false, "Parent Branch not configured for Version [" + verArt + "]");
      }
      return Result.TrueResult;
   }

   public static Branch getParentBranch(Artifact verArt) throws OseeCoreException {
      try {
         String guid = verArt.getSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, "");
         if (GUID.isValid(guid)) {
            return BranchManager.getBranchByGuid(guid);
         }
      } catch (BranchDoesNotExist ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

   public static String getFullDisplayName(Artifact verArt) throws OseeCoreException {
      List<String> strs = new ArrayList<String>();
      if (!verArt.getName().equals(Artifact.UNNAMED)) {
         strs.add(verArt.getName());
      }
      String fullName = verArt.getSoleAttributeValue(AtsAttributeTypes.FullName, "");
      if (Strings.isValid(fullName)) {
         strs.add(fullName);
      }
      String description = verArt.getSoleAttributeValue(AtsAttributeTypes.Description, "");
      if (Strings.isValid(description)) {
         strs.add(description);
      }
      return Collections.toString(" - ", strs);
   }
}

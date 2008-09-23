/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.artifact.DemoCodeTeamWorkflowArtifact;
import org.eclipse.osee.ats.config.demo.config.DemoDatabaseConfig.SawBuilds;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class DemoDbUtil {

   public static String INTERFACE_INITIALIZATION = "Interface Initialization";
   private static List<DemoCodeTeamWorkflowArtifact> codeArts;

   public static List<DemoCodeTeamWorkflowArtifact> getSampleCodeWorkflows() throws Exception {
      if (codeArts == null) {
         codeArts = new ArrayList<DemoCodeTeamWorkflowArtifact>();
         for (String actionName : new String[] {"SAW (committed) Reqt Changes for Diagram View",
               "SAW (uncommitted) More Reqt Changes for Diagram View"}) {
            DemoCodeTeamWorkflowArtifact codeArt = null;
            for (Artifact art : ArtifactQuery.getArtifactsFromName(actionName, AtsPlugin.getAtsBranch())) {
               if (art instanceof DemoCodeTeamWorkflowArtifact) {
                  codeArt = (DemoCodeTeamWorkflowArtifact) art;
                  codeArts.add(codeArt);
               }
            }
         }
      }
      return codeArts;
   }

   public static void sleep(long milliseconds) throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Sleeping " + milliseconds, false);
      Thread.sleep(milliseconds);
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Awake", false);
   }

   public static void setDefaultBranch(Branch branch) throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Setting default branch to \"" + branch + "\".", false);
      BranchPersistenceManager.setDefaultBranch(branch);
      sleep(2000L);
      Branch defaultBranch = BranchPersistenceManager.getDefaultBranch();
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Current Default == \"" + defaultBranch + "\".", false);
      if (!branch.equals(defaultBranch)) {
         throw new IllegalStateException("Default Branch did not change on setDefaultBranch.");
      }
   }

   public static Result isDbPopulatedWithDemoData() throws Exception {
      setDefaultBranch(BranchPersistenceManager.getKeyedBranch(SawBuilds.SAW_Bld_1.name()));

      if (DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Robot).size() != 6) return new Result(
            "Expected at least 6 Software Requirements with word \"Robot\".  Database is not be populated with demo data.");
      return Result.TrueResult;
   }

   public static Collection<Artifact> getSoftwareRequirements(SoftwareRequirementStrs str) throws Exception {
      return getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, str.name());
   }

   public static Collection<Artifact> getArtTypeRequirements(String artifactType, String artifactNameStr) throws Exception {
      OSEELog.logInfo(
            OseeAtsConfigDemoPlugin.class,
            "Getting \"" + artifactNameStr + "\" requirement(s) from Branch " + BranchPersistenceManager.getDefaultBranch().getBranchName(),
            false);
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactsFromTypeAndName(artifactType, "%" + artifactNameStr + "%",
                  BranchPersistenceManager.getDefaultBranch());

      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Found " + arts.size() + " Artifacts", false);
      return arts;
   }
   public static enum SoftwareRequirementStrs {
      Robot, CISST, daVinci, Functional, Event, Haptic
   };
   public static String HAPTIC_CONSTRAINTS_REQ = "Haptic Constraints";

   public static Artifact getInterfaceInitializationSoftwareRequirement() throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Getting \"" + INTERFACE_INITIALIZATION + "\" requirement.", false);
      return ArtifactQuery.getArtifactFromTypeAndName(Requirements.SOFTWARE_REQUIREMENT, INTERFACE_INITIALIZATION,
            BranchPersistenceManager.getDefaultBranch());
   }

}

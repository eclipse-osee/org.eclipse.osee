/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.artifact.DemoCodeTeamWorkflowArtifact;
import org.eclipse.osee.ats.config.demo.config.DemoDatabaseConfig.SawBuilds;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;

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
            for (Artifact art : ArtifactQuery.getArtifactsFromName(actionName, AtsPlugin.getAtsBranch(), false)) {
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
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Sleeping " + milliseconds);
      Thread.sleep(milliseconds);
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Awake");
   }

   public static void setDefaultBranch(Branch branch) throws Exception {
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Setting default branch to \"" + branch + "\".");
      BranchManager.setDefaultBranch(branch);
      sleep(2000L);
      Branch defaultBranch = BranchManager.getDefaultBranch();
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Current Default == \"" + defaultBranch + "\".");
      if (!branch.equals(defaultBranch)) {
         throw new IllegalStateException("Default Branch did not change on setDefaultBranch.");
      }
   }

   public static Result isDbPopulatedWithDemoData() throws Exception {
      setDefaultBranch(BranchManager.getKeyedBranch(SawBuilds.SAW_Bld_1.name()));

      if (DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Robot).size() != 6) return new Result(
            "Expected at least 6 Software Requirements with word \"Robot\".  Database is not be populated with demo data.");
      return Result.TrueResult;
   }

   public static Collection<Artifact> getSoftwareRequirements(SoftwareRequirementStrs str) throws Exception {
      return getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, str.name());
   }

   public static Collection<Artifact> getArtTypeRequirements(String artifactType, String artifactNameStr) throws Exception {
      OseeLog.log(
            OseeAtsConfigDemoPlugin.class,
            Level.INFO,
            "Getting \"" + artifactNameStr + "\" requirement(s) from Branch " + BranchManager.getDefaultBranch().getBranchName());
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactsFromTypeAndName(artifactType, "%" + artifactNameStr + "%",
                  BranchManager.getDefaultBranch());

      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO, "Found " + arts.size() + " Artifacts");
      return arts;
   }
   public static enum SoftwareRequirementStrs {
      Robot, CISST, daVinci, Functional, Event, Haptic
   };
   public static String HAPTIC_CONSTRAINTS_REQ = "Haptic Constraints";

   public static Artifact getInterfaceInitializationSoftwareRequirement() throws Exception {
      OseeLog.log(OseeAtsConfigDemoPlugin.class, Level.INFO,
            "Getting \"" + INTERFACE_INITIALIZATION + "\" requirement.");
      return ArtifactQuery.getArtifactFromTypeAndName(Requirements.SOFTWARE_REQUIREMENT, INTERFACE_INITIALIZATION,
            BranchManager.getDefaultBranch());
   }

}

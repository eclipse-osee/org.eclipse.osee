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
package org.eclipse.osee.ats.config.demo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.config.demo.artifact.DemoCodeTeamWorkflowArtifact;
import org.eclipse.osee.ats.config.demo.internal.OseeAtsConfigDemoActivator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.support.test.util.DemoUsers;

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
            for (Artifact art : ArtifactQuery.getArtifactListFromName(actionName, AtsUtil.getAtsBranch(), false)) {
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
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Sleeping " + milliseconds);
      Thread.sleep(milliseconds);
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Awake");
   }

   public static Result isDbPopulatedWithDemoData(Branch branch) throws Exception {
      if (DemoDbUtil.getSoftwareRequirements(SoftwareRequirementStrs.Robot, branch).size() != 6) return new Result(
            "Expected at least 6 Software Requirements with word \"Robot\".  Database is not be populated with demo data.");
      return Result.TrueResult;
   }

   public static Collection<Artifact> getSoftwareRequirements(SoftwareRequirementStrs str, Branch branch) throws Exception {
      return getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, str.name(), branch);
   }

   public static Collection<Artifact> getArtTypeRequirements(String artifactType, String artifactNameStr, Branch branch) throws Exception {
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            "Getting \"" + artifactNameStr + "\" requirement(s) from Branch " + branch.getName());
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactListFromTypeAndName(artifactType, "%" + artifactNameStr + "%", branch);

      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO, "Found " + arts.size() + " Artifacts");
      return arts;
   }
   public static enum SoftwareRequirementStrs {
      Robot, CISST, daVinci, Functional, Event, Haptic
   };
   public static String HAPTIC_CONSTRAINTS_REQ = "Haptic Constraints";

   public static Artifact getInterfaceInitializationSoftwareRequirement(Branch branch) throws Exception {
      OseeLog.log(OseeAtsConfigDemoActivator.class, Level.INFO,
            "Getting \"" + INTERFACE_INITIALIZATION + "\" requirement.");
      return ArtifactQuery.getArtifactFromTypeAndName(Requirements.SOFTWARE_REQUIREMENT, INTERFACE_INITIALIZATION,
            branch);
   }

   public static User getDemoUser(DemoUsers demoUser) throws OseeCoreException {
      return UserManager.getUserByName(demoUser.getName());
   }

}

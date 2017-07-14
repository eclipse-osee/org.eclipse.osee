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
package org.eclipse.osee.ats.client.demo.config;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class DemoDbUtil {

   public static String INTERFACE_INITIALIZATION = "Interface Initialization";
   private static List<TeamWorkFlowArtifact> codeArts;

   public static List<TeamWorkFlowArtifact> getSampleCodeWorkflows() throws OseeCoreException {
      if (codeArts == null) {
         codeArts = new ArrayList<>();
         for (String actionName : new String[] {
            "SAW (committed) Reqt Changes for Diagram View",
            "SAW (uncommitted) More Reqt Changes for Diagram View"}) {
            for (Artifact art : ArtifactQuery.getArtifactListFromName(actionName, AtsClientService.get().getAtsBranch(),
               EXCLUDE_DELETED)) {
               if (art.isOfType(DemoArtifactTypes.DemoCodeTeamWorkflow)) {
                  codeArts.add((TeamWorkFlowArtifact) art);
               }
            }
         }
      }
      return codeArts;
   }

   public static Result isDbPopulatedWithDemoData(boolean DEBUG, Branch branch) throws OseeCoreException {
      if (DemoDbUtil.getSoftwareRequirements(DEBUG, SoftwareRequirementStrs.Robot, branch).size() != 6) {
         return new Result(
            "Expected at least 6 Software Requirements with word \"Robot\".  Database is not be populated with demo data.");
      }
      return Result.TrueResult;
   }

   public static Collection<Artifact> getSoftwareRequirements(boolean DEBUG, SoftwareRequirementStrs str, IOseeBranch branch) throws OseeCoreException {
      return getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirement, str.name(), branch);
   }

   public static Collection<Artifact> getArtTypeRequirements(boolean DEBUG, IArtifactType artifactType, String artifactNameStr, BranchId branch) throws OseeCoreException {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO,
            "Getting \"" + artifactNameStr + "\" requirement(s) from Branch " + branch.getId());
      }
      Collection<Artifact> arts = ArtifactQuery.getArtifactListFromTypeAndName(artifactType, artifactNameStr, branch,
         QueryOption.CONTAINS_MATCH_OPTIONS);

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Found " + arts.size() + " Artifacts");
      }
      return arts;
   }
   public static enum SoftwareRequirementStrs {
      Robot,
      CISST,
      daVinci,
      Functional,
      Event,
      Haptic
   };
   public static String HAPTIC_CONSTRAINTS_REQ = "Haptic Constraints";

   public static Artifact getInterfaceInitializationSoftwareRequirement(boolean DEBUG, BranchId branch) throws OseeCoreException {
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Getting \"" + INTERFACE_INITIALIZATION + "\" requirement.");
      }
      return ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirement, INTERFACE_INITIALIZATION,
         branch);
   }

   public static Collection<IAtsActionableItem> getActionableItems(String[] aiasNames) throws OseeCoreException {
      Set<IAtsActionableItem> aias = new HashSet<>();
      for (String str : aiasNames) {
         for (IAtsActionableItem aia : ActionableItems.getActionableItemsAll(
            AtsClientService.get().getQueryService())) {
            if (str.equals(aia.getName())) {
               aias.add(aia);
            }
         }
      }
      return aias;
   }
}
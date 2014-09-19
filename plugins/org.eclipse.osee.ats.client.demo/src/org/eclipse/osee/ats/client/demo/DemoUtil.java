/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

public class DemoUtil {

   public static final String SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW =
      "SAW (committed) Reqt Changes for Diagram View";

   private DemoUtil() {
      // Utility class
   }

   public static void checkDbInitSuccess() throws OseeCoreException {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
   }

   public static void checkDbInitAndPopulateSuccess() throws OseeCoreException {
      if (!isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
      if (!isPopulateDbSuccessful()) {
         throw new OseeStateException("PopulateDb must be successful to continue");
      }
   }

   public static boolean isDbInitSuccessful() throws OseeCoreException {
      return OseeInfo.isBoolean("DbInitSuccess");
   }

   public static void setDbInitSuccessful(boolean success) throws OseeCoreException {
      OseeInfo.setBoolean("DbInitSuccess", success);
   }

   public static boolean isPopulateDbSuccessful() throws OseeCoreException {
      return OseeInfo.isBoolean("PopulateSuccessful");
   }

   public static void setPopulateDbSuccessful(boolean success) throws OseeCoreException {
      OseeInfo.setBoolean("PopulateSuccessful", success);
   }

   public static TeamWorkFlowArtifact getSawCodeCommittedWf() throws OseeCoreException {
      return getCodeTeamWorkflowNamed(SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW);
   }

   public static TeamWorkFlowArtifact getSawCodeUnCommittedWf() throws OseeCoreException {
      return getCodeTeamWorkflowNamed("SAW (uncommitted) More Reqt Changes for Diagram View");
   }

   public static TeamWorkFlowArtifact getSawCodeNoBranchWf() throws OseeCoreException {
      return getCodeTeamWorkflowNamed("SAW (no-branch) Even More Requirement Changes for Diagram View");
   }

   public static Collection<TeamWorkFlowArtifact> getSawWfs(String name) {
      List<TeamWorkFlowArtifact> teamWfs = new ArrayList<TeamWorkFlowArtifact>();
      for (Artifact art : ArtifactQuery.getArtifactListFromName(name, AtsUtilCore.getAtsBranch(),
         DeletionFlag.EXCLUDE_DELETED)) {
         if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            teamWfs.add((TeamWorkFlowArtifact) art);
         }
      }
      return teamWfs;
   }

   public static TeamWorkFlowArtifact getCodeTeamWorkflowNamed(String name) throws OseeCoreException {
      TeamWorkFlowArtifact result = null;
      for (Artifact art : getSawWfs(name)) {
         if (art.isOfType(DemoArtifactTypes.DemoCodeTeamWorkflow)) {
            result = (TeamWorkFlowArtifact) art;
            break;
         }
      }
      return result;
   }

   public static Collection<TeamWorkFlowArtifact> getSawCommittedWfs() {
      return getSawWfs(SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW);
   }

}

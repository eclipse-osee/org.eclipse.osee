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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.client.demo.DemoGroups;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class DemoDbGroups {

   public static List<TeamWorkFlowArtifact> createGroups(boolean DEBUG) throws Exception {

      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Populate Demo DB - Create Groups");

      // Create group of all resulting objects
      List<TeamWorkFlowArtifact> codeWorkflows = new ArrayList<TeamWorkFlowArtifact>();
      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Create Groups and add objects");
      }
      Artifact groupArt = UniversalGroup.addGroup(DemoGroups.TEST_GROUP_NAME, AtsUtilCore.getAtsBranch(), transaction);
      for (TeamWorkFlowArtifact codeArt : DemoDbUtil.getSampleCodeWorkflows()) {

         // Add Action to Universal Group
         groupArt.addRelation(CoreRelationTypes.Universal_Grouping__Members, codeArt.getParentActionArtifact());

         // Add All Team Workflows to Universal Group
         for (Artifact teamWorkflow : ActionManager.getTeams(codeArt.getParentActionArtifact())) {
            groupArt.addRelation(CoreRelationTypes.Universal_Grouping__Members, teamWorkflow);
         }

         codeArt.persist(transaction);
      }

      // Add all Tasks to Group
      for (Artifact task : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.Task, AtsUtilCore.getAtsBranch())) {
         groupArt.addRelation(CoreRelationTypes.Universal_Grouping__Members, task);
      }
      groupArt.persist(transaction);
      transaction.execute();
      return codeWorkflows;
   }
}

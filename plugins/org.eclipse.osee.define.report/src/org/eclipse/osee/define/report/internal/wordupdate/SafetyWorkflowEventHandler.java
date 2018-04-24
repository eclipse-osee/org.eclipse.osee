/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.internal.wordupdate;

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author David W. Miller
 */
public class SafetyWorkflowEventHandler implements EventHandler {
   public final static String SAFETY_EVENT_TOPIC = "org/eclipse/osee/define/safetyworkflow/modified";
   public final static String SAFETY_EVENT_BRANCH_ID = "org/eclipse/osee/define/report/internal/BRANCH_ID";
   public final static String SAFETY_EVENT_USER_ART = "org/eclipse/osee/define/report/internal/USER_ART";

   private QueryFactory queryFactory;
   private IAtsServer atsServer;
   private Log logger;

   public SafetyWorkflowEventHandler() {
      /**
       * this is placed here to remind developers that the osgi component this class implements needs a no argument
       * constructor
       */
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.queryFactory = orcsApi.getQueryFactory();
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public void handleEvent(Event event) {
      try {
         Object branchId = event.getProperty(SAFETY_EVENT_BRANCH_ID);
         Object userArt = event.getProperty(SAFETY_EVENT_USER_ART);

         checkEventObjects(branchId, userArt); // throws exception if incorrect

         Branch branch = queryFactory.branchQuery().andId((BranchId) branchId).getResults().getExactlyOne();
         ArtifactId workflowId = branch.getAssociatedArtifact();
         if (workflowId.notEqual(SystemUser.OseeSystem)) {
            ArtifactReadable assocArt =
               atsServer.getOrcsApi().getQueryFactory().fromBranch(atsServer.getAtsBranch()).andId(
                  workflowId).getResults().getExactlyOne();
            IAtsTeamWorkflow safetyWf = getSafetyWorkflow(assocArt);
            if (safetyWf == null) {
               IAtsTeamWorkflow teamWf = atsServer.getWorkItemService().getTeamWf(assocArt);
               safetyWf = createSafetyAction(teamWf, (ArtifactId) userArt);
            }
         }
      } catch (Exception ex) {
         logger.error(ex, "Could not create safety workflow");
      }
   }

   private void checkEventObjects(Object branchId, Object userArt) {
      if (!(branchId instanceof BranchId)) {
         throw new OseeArgumentException("BranchID provided to safety workflow creation event incorrect type: %s",
            branchId.getClass());
      }
      if (!(userArt instanceof ArtifactId)) {
         throw new OseeArgumentException("User Artifact provided to safety workflow creation event incorrect type: %s",
            userArt.getClass());
      }
   }

   private IAtsTeamWorkflow getSafetyWorkflow(ArtifactReadable workflowArt) {
      Conditions.checkNotNull(workflowArt, "work flow artifact");
      IAtsTeamWorkflow safetyWorkflow = null;
      ArtifactReadable safetyActionableItemArt =
         (ArtifactReadable) atsServer.getQueryService().getArtifact(AtsArtifactToken.SafetyActionableItem);
      IAtsTeamWorkflow teamWf = atsServer.getWorkItemService().getTeamWf(workflowArt);
      IAtsActionableItem actionableItem = atsServer.getConfigItemFactory().getActionableItem(safetyActionableItemArt);
      for (IAtsTeamWorkflow sibling : atsServer.getActionFactory().getSiblingTeamWorkflows(teamWf)) {
         if (sibling.getActionableItems().contains(actionableItem)) {
            safetyWorkflow = sibling;
            break;
         }
      }
      return safetyWorkflow;
   }

   private IAtsTeamWorkflow createSafetyAction(IAtsTeamWorkflow teamWf, ArtifactId userArt) {
      IAtsTeamWorkflow teamWorkflow = null;
      try {
         IAtsActionableItem ai = atsServer.getCache().getAtsObject(AtsArtifactToken.SafetyActionableItem);
         if (ai == null) {
            throw new OseeCoreException("Safety Actionable Item not configured");
         }
         IAtsTeamDefinition teamDef = atsServer.getCache().getAtsObject(AtsArtifactToken.SafetyTeamDefinition);
         if (teamDef == null) {
            throw new OseeCoreException("Safety Team Definition not configured");
         }
         IAtsUser createdBy = AtsCoreUsers.SYSTEM_USER;
         IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet("Create System Safety Workflow",
            atsServer.getUserService().getUserByArtifactId(userArt));
         IAtsAction action = atsServer.getActionFactory().getAction(teamWf);
         teamWorkflow =
            atsServer.getActionFactory().createTeamWorkflow(action, teamDef, java.util.Collections.singleton(ai),
               Arrays.asList(AtsCoreUsers.UNASSIGNED_USER), changes, new Date(), createdBy, new INewActionListener() {

                  @Override
                  public void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
                     changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.Description,
                        "Review System Safety Changes for the associated RPCR to Complete the Workflow");
                  }

               });
         changes.setSoleAttributeValue(teamWorkflow, CoreAttributeTypes.Name,
            "Safety Workflow for " + teamWf.getAtsId());
         changes.execute();
      } catch (Exception ex) {
         logger.error(ex, "WordUpdateData Safety Action creation");
      }
      return teamWorkflow;
   }

}

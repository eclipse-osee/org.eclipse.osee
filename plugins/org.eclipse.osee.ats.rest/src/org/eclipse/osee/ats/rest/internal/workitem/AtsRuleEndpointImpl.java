/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.rule.JaxRuleDefinitions;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AddRuleData;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.RunRuleData;
import org.eclipse.osee.ats.api.workdef.RunRuleResults;
import org.eclipse.osee.ats.api.workflow.AtsRuleEndpointApi;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.WorkflowRuleRunner;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Mark Joy
 */
public class AtsRuleEndpointImpl implements AtsRuleEndpointApi {
   private final IAtsServer atsServer;

   public AtsRuleEndpointImpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @GET
   @Path("rule")
   @Produces({MediaType.APPLICATION_JSON})
   @Override
   public JaxRuleDefinitions get() throws Exception {
      JaxRuleDefinitions rules = new JaxRuleDefinitions();
      for (IAtsRuleDefinition rule : atsServer.getWorkDefinitionService().getAllRuleDefinitions()) {
         rules.getRules().add(rule);
      }
      return rules;
   }

   @POST
   @Path("rulerun")
   @Override
   public RunRuleResults runWorkflowRules(RunRuleData runRuleData) {
      RunRuleResults ruleResults = new RunRuleResults();

      List<IAtsWorkItem> workItemsCreated = new LinkedList<>();
      for (long workflowId : runRuleData.getWorkItemIds()) {
         ArtifactReadable artifact = (ArtifactReadable) atsServer.getQueryService().getArtifact(workflowId);
         IAtsWorkItem workItem = atsServer.getWorkItemFactory().getWorkItem(artifact);
         if (workItem == null) {
            throw new OseeArgumentException("Workflow of id [%d] does not exist", workflowId);
         }
         if (!workItem.isTeamWorkflow()) {
            throw new OseeArgumentException("Workflow of id [%d] is not a Team Workflow", workflowId);
         }
         workItemsCreated.add(workItem);
      }
      if (!workItemsCreated.isEmpty()) {
         WorkflowRuleRunner runner =
            new WorkflowRuleRunner(runRuleData.getRuleEventType(), workItemsCreated, atsServer, ruleResults);
         runner.run();
      }
      return ruleResults;
   }

   @POST
   @Path("rule")
   @Override
   public Response addRuleToConfig(AddRuleData setRuleData) {
      ArtifactReadable artifact = (ArtifactReadable) atsServer.getQueryService().getArtifact(setRuleData.getConfigItemId());
      List<String> ruleList = artifact.getAttributeValues(AtsAttributeTypes.RuleDefinition);
      if (!ruleList.contains(setRuleData.getRuleName())) {
         IAtsChangeSet changes =
            atsServer.getStoreService().createAtsChangeSet("Update artifact with Rule", AtsCoreUsers.SYSTEM_USER);
         changes.addAttribute(atsServer.getConfigItemFactory().getConfigObject(artifact),
            AtsAttributeTypes.RuleDefinition, setRuleData.getRuleName());
         changes.execute();
         IAtsConfigObject atsObject = atsServer.getCache().getAtsObject(setRuleData.getConfigItemId());
         atsServer.getCache().deCacheAtsObject(atsObject);
      }

      return Response.ok().build();
   }

}

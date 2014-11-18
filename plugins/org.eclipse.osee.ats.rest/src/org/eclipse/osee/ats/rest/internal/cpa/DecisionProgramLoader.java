/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.cpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.cpa.IAtsCpaDecision;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.cpa.ICpaPcr;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.cpa.CpaDecision;
import org.eclipse.osee.ats.core.cpa.CpaFactory;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class DecisionProgramLoader {

   private final String programUuid;
   private final Boolean open;
   private final IAtsServer atsServer;
   private final CpaServiceRegistry cpaRegistry;

   public DecisionProgramLoader(String programUuid, Boolean open, CpaServiceRegistry cpaRegistry, IAtsServer atsServer) {
      this.programUuid = programUuid;
      this.open = open;
      this.cpaRegistry = cpaRegistry;
      this.atsServer = atsServer;
   }

   public List<IAtsCpaDecision> load() {
      List<IAtsCpaDecision> decisions = new ArrayList<IAtsCpaDecision>();
      QueryBuilder queryBuilder =
         atsServer.getQuery().andTypeEquals(AtsArtifactTypes.TeamWorkflow).and(AtsAttributeTypes.ApplicabilityWorkflow,
            "true").and(AtsAttributeTypes.ProgramUuid, programUuid);
      if (open != null) {
         queryBuilder.and(AtsAttributeTypes.CurrentStateType,
            (open ? StateType.Working.name() : StateType.Completed.name()));
      }
      HashCollection<String, IAtsCpaDecision> origPcrIdToDecision = new HashCollection<String, IAtsCpaDecision>();
      String pcrToolId = null;
      ElapsedTime time = new ElapsedTime("load cpa workflows");
      ResultSet<ArtifactReadable> results = queryBuilder.getResults();
      time.end(Units.SEC);
      time = new ElapsedTime("process cpa workflows");
      for (ArtifactReadable art : results) {
         IAtsTeamWorkflow teamWf = atsServer.getWorkItemFactory().getTeamWf(art);
         CpaDecision decision = CpaFactory.getDecision(teamWf, null);
         decision.setApplicability(art.getSoleAttributeValue(AtsAttributeTypes.ApplicableToProgram, ""));
         decision.setRationale(art.getSoleAttributeValue(AtsAttributeTypes.Rationale, ""));
         String pcrToolIdValue = art.getSoleAttributeValue(AtsAttributeTypes.PcrToolId, "");
         if (pcrToolId == null) {
            pcrToolId = pcrToolIdValue;
         }
         decision.setPcrSystem(pcrToolIdValue);
         boolean completed =
            art.getSoleAttributeValue(AtsAttributeTypes.CurrentStateType, "").equals(StateType.Completed.name());
         decision.setComplete(completed);
         decision.setAssignees(teamWf.getStateMgr().getAssigneesStr());
         if (completed) {
            decision.setCompletedBy(teamWf.getCompletedBy().getName());
            decision.setCompletedDate(DateUtil.getMMDDYY(teamWf.getCompletedDate()));
         }

         // set location of decision workflow
         decision.setDecisionLocation(CpaUtil.getCpaPath(atsServer).path(teamWf.getAtsId()).build().toString());

         // set location of originating pcr
         String origPcrId = art.getSoleAttributeValue(AtsAttributeTypes.OriginatingPcrId);
         origPcrIdToDecision.put(origPcrId, decision);
         decision.setOrigPcrLocation(CpaUtil.getCpaPath(atsServer).path(origPcrId).queryParam("pcrSystem",
            decision.getPcrSystem()).build().toString());

         // set location of duplicated pcr (if any)
         String duplicatedPcrId = art.getSoleAttributeValue(AtsAttributeTypes.DuplicatedPcrId, null);
         if (Strings.isValid(duplicatedPcrId)) {
            String duplicatedLocation =
               CpaUtil.getCpaPath(atsServer).path(duplicatedPcrId).queryParam("pcrSystem", decision.getPcrSystem()).build().toString();
            decision.setDuplicatedPcrLocation(duplicatedLocation);
            decision.setDuplicatedPcrId(duplicatedPcrId);
         }

         decisions.add(decision);
      }
      time.end();

      time = new ElapsedTime("load issues");
      IAtsCpaService service = cpaRegistry.getServiceById(pcrToolId);
      for (Entry<String, ICpaPcr> entry : service.getPcrsByIds(origPcrIdToDecision.keySet()).entrySet()) {
         for (IAtsCpaDecision decision : origPcrIdToDecision.getValues(entry.getKey())) {
            ((CpaDecision) decision).setOriginatingPcr(entry.getValue());
         }
      }
      time.end();

      return decisions;
   }

}

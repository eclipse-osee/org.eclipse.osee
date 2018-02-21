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
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.cpa.CpaDecision;
import org.eclipse.osee.ats.api.cpa.CpaPcr;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.cpa.CpaFactory;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class DecisionLoader {

   private String programId;
   private Boolean open;
   private final AtsApi atsApi;
   private final CpaServiceRegistry cpaRegistry;
   private final OrcsApi orcsApi;
   private Collection<String> ids;

   public DecisionLoader(CpaServiceRegistry cpaRegistry, AtsApi atsApi, OrcsApi orcsApi) {
      this.cpaRegistry = cpaRegistry;
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public DecisionLoader andProgramId(String programId) {
      this.programId = programId;
      return this;
   }

   public DecisionLoader andCpaIds(Collection<String> ids) {
      this.ids = ids;
      return this;
   }

   public DecisionLoader andOpen(Boolean open) {
      this.open = open;
      return this;
   }

   public List<CpaDecision> load() {
      List<CpaDecision> decisions = new ArrayList<>();
      QueryBuilder queryBuilder =
         orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch()).andTypeEquals(AtsArtifactTypes.TeamWorkflow).and(
            AtsAttributeTypes.ApplicabilityWorkflow, "true");
      if (Strings.isValid(programId)) {
         queryBuilder.and(AtsAttributeTypes.ProgramId, programId);
      }
      if (Conditions.hasValues(ids)) {
         queryBuilder.and(AtsAttributeTypes.AtsId, ids);
      }
      if (open != null) {
         queryBuilder.and(AtsAttributeTypes.CurrentStateType,
            open ? StateType.Working.name() : StateType.Completed.name());
      }
      HashCollection<String, CpaDecision> origPcrIdToDecision = new HashCollection<>();
      String pcrToolId = null;
      ElapsedTime time = new ElapsedTime("load cpa workflows");
      ResultSet<ArtifactReadable> results = queryBuilder.getResults();
      time.end(Units.SEC);
      time = new ElapsedTime("process cpa workflows");
      for (ArtifactReadable art : results) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemFactory().getTeamWf(art);
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
         decision.setDecisionLocation(CpaUtil.getCpaPath(atsApi).path(teamWf.getAtsId()).build().toString());

         // set location of originating pcr
         String origPcrId = art.getSoleAttributeValue(AtsAttributeTypes.OriginatingPcrId);
         origPcrIdToDecision.put(origPcrId, decision);
         decision.setOrigPcrLocation(CpaUtil.getCpaPath(atsApi).path(origPcrId).queryParam("pcrSystem",
            decision.getPcrSystem()).build().toString());

         // set location of duplicated pcr (if any)
         String duplicatedPcrId = art.getSoleAttributeValue(AtsAttributeTypes.DuplicatedPcrId, null);
         if (Strings.isValid(duplicatedPcrId)) {
            String duplicatedLocation = CpaUtil.getCpaPath(atsApi).path(duplicatedPcrId).queryParam("pcrSystem",
               decision.getPcrSystem()).build().toString();
            decision.setDuplicatedPcrLocation(duplicatedLocation);
            decision.setDuplicatedPcrId(duplicatedPcrId);
         }

         decisions.add(decision);
      }
      time.end();

      time = new ElapsedTime("load issues");
      IAtsCpaService service = cpaRegistry.getServiceById(pcrToolId);
      for (Entry<String, CpaPcr> entry : service.getPcrsByIds(origPcrIdToDecision.keySet()).entrySet()) {
         for (CpaDecision decision : origPcrIdToDecision.getValues(entry.getKey())) {
            decision.setOriginatingPcr(entry.getValue());
         }
      }
      time.end();

      return decisions;
   }

}

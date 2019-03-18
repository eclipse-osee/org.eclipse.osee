/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.commit.operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.commit.CommitOverride;
import org.eclipse.osee.ats.api.commit.CommitOverrideOperations;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class CommitOverrideOperationsImpl implements CommitOverrideOperations {

   private final AtsApi atsApi;
   private ObjectMapper mapper;

   public CommitOverrideOperationsImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * @return commit override for given branch or null
    */
   @Override
   public CommitOverride getCommitOverride(IAtsTeamWorkflow teamWf, BranchId branch) {
      for (CommitOverride override : getCommitOverrides(teamWf)) {
         if (override.getBranchId().equals(branch.getIdString())) {
            return override;
         }
      }
      return null;
   }

   @Override
   public Collection<CommitOverride> getCommitOverrides(IAtsTeamWorkflow teamWf) {
      ObjectMapper mapper = getObjectMapper();
      List<CommitOverride> overrides = new LinkedList<>();
      for (String overrideAttr : atsApi.getAttributeResolver().getAttributesToStringList(teamWf,
         AtsAttributeTypes.CommitOverride)) {
         try {
            CommitOverride override = mapper.readValue(overrideAttr, CommitOverride.class);
            overrides.add(override);
         } catch (Exception ex) {
            atsApi.getLogger().error("Error reading json Commit Override for %s.  Value: [%s] Exception %s",
               teamWf.toStringWithId(), overrideAttr, ex.getMessage());
         }
      }
      return overrides;
   }

   protected ObjectMapper getObjectMapper() {
      if (mapper == null) {
         mapper = new ObjectMapper();
      }
      return mapper;
   }

   @Override
   public Result setCommitOverride(IAtsTeamWorkflow teamWf, BranchId branch, String reason) {
      atsApi.getStoreService().reload(Collections.singleton(teamWf));
      if (getCommitOverride(teamWf, branch) != null) {
         return new Result(false, String.format("Commit Override already set for branch [%s]", branch.getIdString()));
      }

      CommitOverride override = CommitOverride.valueOf(
         UserId.valueOf(atsApi.getUserService().getCurrentUser().getStoreObject().getId()), branch, reason);
      ObjectMapper mapper = getObjectMapper();

      try {
         IAtsChangeSet changes = atsApi.createChangeSet("Set Commit Override");
         String jsonStr = mapper.writeValueAsString(override);
         changes.addAttribute(teamWf, AtsAttributeTypes.CommitOverride, jsonStr);
         changes.execute();
      } catch (Exception ex) {
         return new Result(false,
            String.format("Commit Override failed for [%s].  Exception %s", branch.getIdString(), ex.getMessage()));
      }
      return Result.TrueResult;
   }

   @Override
   public Result removeCommitOverride(IAtsTeamWorkflow teamWf, BranchId branch) {
      atsApi.getStoreService().reload(Collections.singleton(teamWf));
      CommitOverride override = getCommitOverride(teamWf, branch);
      if (override == null) {
         return new Result(false, String.format("Commit Override not set for branch [%s]", branch.getIdString()));
      }

      try {
         IAtsChangeSet changes = atsApi.createChangeSet("Remove Commit Override");
         for (IAttribute<Object> attr : atsApi.getAttributeResolver().getAttributes(teamWf,
            AtsAttributeTypes.CommitOverride)) {
            String jsonValue = (String) attr.getValue();
            CommitOverride or = mapper.readValue(jsonValue, CommitOverride.class);

            if (or.getBranchId().equals(branch.getIdString())) {
               changes.deleteAttribute(teamWf, attr);
            }
         }
         changes.execute();
      } catch (Exception ex) {
         return new Result(false, String.format("Commit Override failed for branch [%s].  Exception %s",
            branch.getIdString(), ex.getMessage()));
      }
      return Result.TrueResult;
   }

}

/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class NewActionResult {

   ArtifactId action;
   List<ArtifactId> teamWfs = new LinkedList<>();
   XResultData results = new XResultData();
   BranchId workingBranch;

   AtsApi atsApi;
   IAtsAction atsAction;
   List<IAtsTeamWorkflow> atsTeamWfs = new ArrayList<>();
   TransactionId transaction = TransactionId.SENTINEL;

   public NewActionResult() {
      // for jax-rs
   }

   public ArtifactId getAction() {
      return action;
   }

   public void setAction(ArtifactId action) {
      this.action = action;
   }

   public List<ArtifactId> getTeamWfs() {
      return teamWfs;
   }

   public void setTeamWfs(List<ArtifactId> teamWfs) {
      this.teamWfs = teamWfs;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public void addTeamWf(ArtifactId teamWf) {
      teamWfs.add(ArtifactId.valueOf(teamWf));
   }

   public BranchId getWorkingBranchId() {
      return workingBranch;
   }

   public void setWorkingBranchId(BranchId workingBranchId) {
      this.workingBranch = workingBranchId;
   }

   public BranchId getWorkingBranch() {
      return workingBranch;
   }

   public void setWorkingBranch(BranchId workingBranch) {
      this.workingBranch = workingBranch;
   }

   @JsonIgnore
   public IAtsAction getAtsAction() {
      return atsAction;
   }

   @JsonIgnore
   public void setAtsAction(IAtsAction atsAction) {
      this.atsAction = atsAction;
   }

   @JsonIgnore
   public List<IAtsTeamWorkflow> getAtsTeamWfs() {
      return atsTeamWfs;
   }

   @JsonIgnore
   public void setAtsTeamWfs(List<IAtsTeamWorkflow> atsTeamWfs) {
      this.atsTeamWfs = atsTeamWfs;
   }

   public TransactionId getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionId transaction) {
      this.transaction = transaction;
   }

   @JsonIgnore
   public AtsApi getAtsApi() {
      return atsApi;
   }

   @JsonIgnore
   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String toString() {
      return "NewActionResult [action=" + action + ", teamWfs=" + teamWfs + ", brch=" + workingBranch + ", tx=" + transaction.getId() + "]";
   }

}

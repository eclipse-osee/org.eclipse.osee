/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.task.create;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTaskData {

   /**
    * Results for all processing of this operation including validation checks, creating workflow, creating tasks and
    * error handling
    */
   public XResultData results = new XResultData();
   public String operationName = "";
   public String commitComment = "";
   public List<ChangeReportTaskTeamWfData> changeReportDatas = new ArrayList<ChangeReportTaskTeamWfData>();
   // True if just want results of what would be done
   boolean reportOnly = false;
   IAtsVersion targetedVersion;
   AtsUser asUser;
   // Workflow that initiated request
   ArtifactToken hostTeamWf;
   // Workflow that owns change report or empty (will be determined from create task definition team def)
   ArtifactToken chgRptTeamWf = ArtifactToken.SENTINEL;
   // Token of the StaticTaskDefinition to run against change report
   AtsTaskDefToken taskDefToken;
   private String workOrParentBranch; // DON'T CHANGE TO BranchToken till deserialization is fixed
   // ChangeItems from executed change report
   private List<ChangeItem> changeItems;
   // Definition loaded from the taskDefToken that defines how tasks are created
   private CreateTasksDefinition setDef;
   @JsonIgnore
   private final Map<Long, IAtsTeamWorkflow> idToTeamWf = new HashMap<Long, IAtsTeamWorkflow>();
   // Show detailed debug logging
   private boolean debug = false;
   private TransactionId transaction = TransactionId.SENTINEL;
   private Collection<ArtifactId> destTeamWfs = new HashSet<>();
   private ArtifactId actionId;
   private IAtsChangeSet changes;
   private Set<ArtifactId> ids;
   boolean finalTaskGen = false;

   public ChangeReportTaskData() {
      // for jax-rs
   }

   public List<ChangeReportTaskTeamWfData> getChangeReportDatas() {
      return changeReportDatas;
   }

   public void addChangeReportData(ChangeReportTaskTeamWfData changeReportData) {
      changeReportDatas.add(changeReportData);
   }

   public void setChangeReportDatas(List<ChangeReportTaskTeamWfData> changeReportDatas) {
      this.changeReportDatas = changeReportDatas;
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public boolean isReportOnly() {
      return reportOnly;
   }

   public void setReportOnly(boolean reportOnly) {
      this.reportOnly = reportOnly;
   }

   public IAtsVersion getTargetedVersion() {
      return targetedVersion;
   }

   public void setTargetedVersion(IAtsVersion targetedVersion) {
      this.targetedVersion = targetedVersion;
   }

   public AtsUser getAsUser() {
      return asUser;
   }

   public void setAsUser(AtsUser asUser) {
      this.asUser = asUser;
   }

   public AtsTaskDefToken getTaskDefToken() {
      return taskDefToken;
   }

   public void setTaskDefToken(AtsTaskDefToken taskDefToken) {
      this.taskDefToken = taskDefToken;
   }

   public ArtifactToken getHostTeamWf() {
      return hostTeamWf;
   }

   public void setHostTeamWf(ArtifactToken hostTeamWf) {
      this.hostTeamWf = hostTeamWf;
   }

   public ArtifactToken getChgRptTeamWf() {
      return chgRptTeamWf;
   }

   public void setChgRptTeamWf(ArtifactToken chgRptTeamWf) {
      this.chgRptTeamWf = chgRptTeamWf;
   }

   public void setWorkOrParentBranch(String workOrParentBranch) {
      this.workOrParentBranch = workOrParentBranch;
   }

   public void setChangeItems(List<ChangeItem> changeItems) {
      this.changeItems = changeItems;
   }

   public boolean hasChangeItems() {
      return this.changeItems == null || this.changeItems.isEmpty();
   }

   public String getWorkOrParentBranch() {
      return workOrParentBranch;
   }

   @JsonIgnore
   public BranchId getWorkOrParentBranchId() {
      return BranchId.valueOf(workOrParentBranch);
   }

   @JsonIgnore
   public List<ChangeItem> getChangeItems() {
      return changeItems;
   }

   public void setSetDef(CreateTasksDefinition setDef) {
      this.setDef = setDef;
   }

   public CreateTasksDefinition getSetDef() {
      return setDef;
   }

   /**
    * Storage for team workflows loaded or create so they can be used later in process without having to search db
    * (which they may not have been stored in yet).
    */
   public Map<Long, IAtsTeamWorkflow> getIdToTeamWf() {
      return idToTeamWf;
   }

   public boolean isDebug() {
      return debug;
   }

   public void setDebug(boolean debug) {
      this.debug = debug;
   }

   public TransactionId getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionId transaction) {
      this.transaction = transaction;
   }

   public Collection<ArtifactId> getDestTeamWfs() {
      return destTeamWfs;
   }

   public void setDestTeamWfs(Collection<ArtifactId> destTeamWfs) {
      this.destTeamWfs = destTeamWfs;
   }

   public ArtifactId getActionId() {
      return actionId;
   }

   public void setActionId(ArtifactId actionId) {
      this.actionId = actionId;
   }

   public IAtsChangeSet getChanges() {
      return changes;
   }

   public void setChanges(IAtsChangeSet changes) {
      this.changes = changes;
   }

   public Set<ArtifactId> getIds() {
      return ids;
   }

   public void setIds(Set<ArtifactId> ids) {
      this.ids = ids;
   }

   public String getOperationName() {
      return operationName;
   }

   public void setOperationName(String operationName) {
      this.operationName = operationName;
   }

   public boolean isFinalTaskGen() {
      return finalTaskGen;
   }

   public void setFinalTaskGen(boolean finalTaskGen) {
      this.finalTaskGen = finalTaskGen;
   }

   @JsonIgnore
   public boolean isNoChangeItems() {
      return changeItems == null || changeItems.isEmpty();
   }

   public String getCommitComment() {
      return commitComment;
   }

   public void setCommitComment(String commitComment) {
      this.commitComment = commitComment;
   }

}

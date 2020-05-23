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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class NewActionResult {

   ArtifactId action;
   List<ArtifactId> teamWfs = new LinkedList<>();
   XResultData results = new XResultData();
   BranchId workingBranch;

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

}

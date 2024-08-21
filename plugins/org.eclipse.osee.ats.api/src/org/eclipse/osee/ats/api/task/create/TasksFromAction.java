/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

public class TasksFromAction {

   ArtifactToken destTeamWf = ArtifactToken.SENTINEL;
   List<ArtifactToken> sourceTeamWfs = new LinkedList<>();
   List<ArtifactToken> sourceTasks = new LinkedList<>();
   XResultData rd = new XResultData();
   String reason = "";
   ArtifactId createdBy = ArtifactId.SENTINEL;

   public ArtifactToken getDestTeamWf() {
      return destTeamWf;
   }

   public void setDestTeamWf(ArtifactToken destTeamWf) {
      this.destTeamWf = destTeamWf;
   }

   public List<ArtifactToken> getSourceTeamWfs() {
      return sourceTeamWfs;
   }

   public void setSourceTeamWfs(List<ArtifactToken> sourceTeamWfs) {
      this.sourceTeamWfs = sourceTeamWfs;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

   public String getReason() {
      return reason;
   }

   public void setReason(String reason) {
      this.reason = reason;
   }

   public ArtifactId getCreatedBy() {
      return createdBy;
   }

   public void setCreatedBy(ArtifactId createdBy) {
      this.createdBy = createdBy;
   }

   public List<ArtifactToken> getSourceTasks() {
      return sourceTasks;
   }

   public void setSourceTasks(List<ArtifactToken> sourceTasks) {
      this.sourceTasks = sourceTasks;
   }

}

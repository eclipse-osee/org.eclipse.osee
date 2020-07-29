/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.api.task.related;

import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Class to resolve related artifact from Derived-From relation and branch/commit.
 *
 * @author Donald G. Dunne
 */
public class DerivedFromTaskData {
   private boolean deleted;
   private ArtifactToken headArtifact;
   private ArtifactToken latestArt;
   private XResultData results = new XResultData();
   private IAtsTeamWorkflow derivedFromTeamWf;
   private IAtsTask task;
   private boolean derived;

   public DerivedFromTaskData() {
      this(false, null, null);
   }

   public DerivedFromTaskData(IAtsTask task) {
      this(false, null, null);
      this.task = task;
   }

   public DerivedFromTaskData(boolean deleted, ArtifactToken headArtifact, ArtifactToken latestArt) {
      this.deleted = deleted;
      this.headArtifact = headArtifact;
      this.latestArt = latestArt;
      this.results = new XResultData();
   }

   public boolean isDeleted() {
      return deleted;
   }

   public ArtifactToken getHeadArtifact() {
      return headArtifact;
   }

   public ArtifactToken getLatestArt() {
      return latestArt;
   }

   public boolean isDerived() {
      return derived;
   }

   public void setDerived(boolean derived) {
      this.derived = derived;
   }

   public XResultData getResults() {
      return results;
   }

   public IAtsTask getTask() {
      return task;
   }

   public void setTask(IAtsTask task) {
      this.task = task;
   }

   public void setLatestArt(ArtifactToken latestArt) {
      this.latestArt = latestArt;
   }

   public void setHeadArtifact(ArtifactToken headArtifact) {
      this.headArtifact = headArtifact;
   }

   public IAtsTeamWorkflow getDerivedFromTeamWf() {
      return derivedFromTeamWf;
   }

   public void setDerivedFromTeamWf(IAtsTeamWorkflow derivedFromTeamWf) {
      this.derivedFromTeamWf = derivedFromTeamWf;
   }

   @Override
   public String toString() {
      if (results.isEmpty()) {
         return "Empty";
      }
      return results.toString();
   }

   public void setDeleted(boolean deleted) {
      this.deleted = deleted;
   }
}

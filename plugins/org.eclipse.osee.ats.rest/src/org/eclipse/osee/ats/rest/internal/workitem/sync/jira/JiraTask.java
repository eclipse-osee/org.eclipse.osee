/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem.sync.jira;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public class JiraTask {

   String summary;
   String status;
   String desc;
   Set<String> atsIds = new HashSet<String>();
   String jSprint;
   IAgileSprint aSprint;
   IAtsTeamWorkflow teamWf;
   String points;
   String amsId;

   public JiraTask() {
   }

   public String getSummary() {
      return summary;
   }

   public void setSummary(String summary) {
      this.summary = summary;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getDesc() {
      return desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }

   @Override
   public String toString() {
      return "JiraTask [summary=" + summary + ", status=" + status + ", atsIds=" + atsIds + ", jSprint=" + jSprint + "]";
   }

   public IAtsTeamWorkflow getTeamWf() {
      return teamWf;
   }

   public void setTeamWf(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
   }

   public Set<String> getAtsIds() {
      return atsIds;
   }

   public void setAtsIds(Collection<String> atsIds) {
      this.atsIds.addAll(atsIds);
   }

   public void addAtsId(String atsId) {
      this.atsIds.add(atsId);
   }

   public String getjSprint() {
      return jSprint;
   }

   public void setjSprint(String jSprint) {
      this.jSprint = jSprint;
   }

   public IAgileSprint getaSprint() {
      return aSprint;
   }

   public void setaSprint(IAgileSprint aSprint) {
      this.aSprint = aSprint;
   }

   public String getPoints() {
      return points;
   }

   public void setPoints(String points) {
      this.points = points;
   }

   public String getAmsId() {
      return amsId;
   }

   public void setAmsId(String amsId) {
      this.amsId = amsId;
   }
}

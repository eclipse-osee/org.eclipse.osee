/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem.sync.jira;

import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public class JiraTask {

   String summary;
   String status;
   String desc;
   String atsId;
   String sprint;
   IAtsTeamWorkflow teamWf;

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

   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

   @Override
   public String toString() {
      return "JiraTask [summary=" + summary + ", status=" + status + ", atsId=" + atsId + ", sprint=" + sprint + "]";
   }

   public String getSprint() {
      return sprint;
   }

   public void setSprint(String sprint) {
      this.sprint = sprint;
   }

   public IAtsTeamWorkflow getTeamWf() {
      return teamWf;
   }

   public void setTeamWf(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
   }

}

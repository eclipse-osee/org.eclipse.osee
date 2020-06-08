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

package org.eclipse.osee.ats.api.task.create;

import org.eclipse.osee.ats.api.config.WorkType;

/**
 * Data for creation of workflow/tasks for certain team
 *
 * @author Donald G. Dunne
 */
public class ChangeReportOptionsToTeam {

   private String teamId;
   private String aiId;
   private WorkType workType;
   private ChangeReportTaskNameProviderToken nameProviderId;

   public ChangeReportOptionsToTeam() {
      // for jax-rs
   }

   public String getTeamId() {
      return teamId;
   }

   public void setTeamId(String teamId) {
      this.teamId = teamId;
   }

   public String getAiId() {
      return aiId;
   }

   public void setAiId(String aiId) {
      this.aiId = aiId;
   }

   @Override
   public String toString() {
      String nameProviderStr =
         nameProviderId == null ? IAtsChangeReportTaskNameProvider.class.getSimpleName() : nameProviderId.toStringWithId();
      return "ChangeReportOptionsToTeam [teamId=" + teamId + ", aiId=" + aiId + ", workType=" + workType + ", " + "nameProvider=" + nameProviderStr + "]";
   }

   public ChangeReportTaskNameProviderToken getNameProviderId() {
      return nameProviderId;
   }

   public void setNameProviderId(ChangeReportTaskNameProviderToken nameProviderId) {
      this.nameProviderId = nameProviderId;
   }

   public WorkType getWorkType() {
      return workType;
   }

   public void setWorkType(WorkType workType) {
      this.workType = workType;
   }

}

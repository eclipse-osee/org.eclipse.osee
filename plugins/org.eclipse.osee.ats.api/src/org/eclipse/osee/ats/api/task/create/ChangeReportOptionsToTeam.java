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
package org.eclipse.osee.ats.api.task.create;

/**
 * Data for creation of workflow/tasks for certain team
 *
 * @author Donald G. Dunne
 */
public class ChangeReportOptionsToTeam {

   private String teamId;
   private String aiId;
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
         nameProviderId == null ? IAtsChangeReportTaskNameProvider.class.getSimpleName() : nameProviderId.getClass().getSimpleName();
      return "ChangeReportOptionsToTeam [teamId=" + teamId + ", aiId=" + aiId + ", nameProvider=" + nameProviderStr + "]";
   }

   public ChangeReportTaskNameProviderToken getNameProviderId() {
      return nameProviderId;
   }

   public void setNameProviderId(ChangeReportTaskNameProviderToken nameProviderId) {
      this.nameProviderId = nameProviderId;
   }

}

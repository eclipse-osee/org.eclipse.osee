/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import org.eclipse.osee.ats.api.task.JaxAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class JaxTeamWorkflow extends JaxAtsWorkItem {

   ArtifactToken newAi = ArtifactToken.SENTINEL;
   ArtifactToken team = ArtifactToken.SENTINEL;
   ArtifactToken targetVersion = ArtifactToken.SENTINEL;
   String priority = "";

   public JaxTeamWorkflow() {
   }

   public ArtifactToken getNewAi() {
      return newAi;
   }

   public void setNewAi(ArtifactToken newAi) {
      this.newAi = newAi;
   }

   public String getTeamName() {
      return team.getName();
   }

   public ArtifactToken getTeam() {
      return team;
   }

   public void setTeam(ArtifactToken team) {
      this.team = team;
   }

   public ArtifactToken getTargetVersion() {
      return targetVersion;
   }

   public void setTargetVersion(ArtifactToken targetVersion) {
      this.targetVersion = targetVersion;
   }

   public String getPriority() {
      return priority;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

}

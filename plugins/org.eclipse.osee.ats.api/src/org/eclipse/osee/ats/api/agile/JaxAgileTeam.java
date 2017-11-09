/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileTeam extends JaxAtsObject {

   private List<Long> atsTeamIds = new ArrayList<>();
   private ArtifactId backlogId = ArtifactId.SENTINEL;
   private ArtifactId sprintId = ArtifactId.SENTINEL;
   private String description = "";

   public List<Long> getAtsTeamIds() {
      return atsTeamIds;
   }

   public void setAtsTeamIds(List<Long> atsTeamIds) {
      this.atsTeamIds = atsTeamIds;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public void setDescription(String description) {
      this.description = description;
   }

   public ArtifactId getBacklogId() {
      return backlogId;
   }

   public void setBacklogId(ArtifactId backlogId) {
      this.backlogId = backlogId;
   }

   public ArtifactId getSprintId() {
      return sprintId;
   }

   public void setSprintId(ArtifactId sprintId) {
      this.sprintId = sprintId;
   }

}

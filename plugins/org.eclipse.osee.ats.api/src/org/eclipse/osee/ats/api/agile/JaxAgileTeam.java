/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.agile;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileTeam extends JaxAtsObject {

   private List<Long> atsTeamIds = new ArrayList<>();
   private ArtifactId backlogId = ArtifactId.SENTINEL;
   private ArtifactId sprintId = ArtifactId.SENTINEL;
   private AttributeTypeToken pointsAttrType = AttributeTypeToken.SENTINEL;
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

   public AttributeTypeToken getPointsAttrType() {
      return pointsAttrType;
   }

   public void setPointsAttrType(AttributeTypeToken pointsAttrType) {
      this.pointsAttrType = pointsAttrType;
   }

}

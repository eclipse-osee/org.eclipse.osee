/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.create;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Donald G. Dunne
 */
public class CreateTaskDefinition {

   private String relatedToState;
   private String title;
   public List<Long> assigneeAccountIds = new ArrayList<>();
   private String description;
   private ArtifactId sourceTeamWfAi = ArtifactId.SENTINEL;
   private ArtifactId destTeamWfAi = ArtifactId.SENTINEL;
   private NamedId workDefId = NamedId.SENTINEL;

   public String getRelatedToState() {
      return relatedToState;
   }

   public CreateTaskDefinition andRelatedToState(StateToken state) {
      setRelatedToState(state.getName());
      return this;
   }

   public void setRelatedToState(String relatedToState) {
      this.relatedToState = relatedToState;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public List<Long> getAssigneeAccountIds() {
      return assigneeAccountIds;
   }

   public void setAssigneeAccountIds(List<Long> assigneeAccountIds) {
      this.assigneeAccountIds = assigneeAccountIds;
   }

   public ArtifactId getSourceTeamWfAi() {
      return sourceTeamWfAi;
   }

   public void setSourceTeamWfAi(ArtifactId sourceTeamWfAi) {
      this.sourceTeamWfAi = sourceTeamWfAi;
   }

   public ArtifactId getDestTeamWfAi() {
      return destTeamWfAi;
   }

   public void setDestTeamWfAi(ArtifactId destTeamWfAi) {
      this.destTeamWfAi = destTeamWfAi;
   }

   public NamedId getWorkDefId() {
      return workDefId;
   }

   public void setWorkDefId(NamedId workDefId) {
      this.workDefId = workDefId;
   }

}

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
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public class StaticTaskDefinition {

   private String relatedToState;
   private String name;
   public List<Long> assigneeAccountIds = new ArrayList<>();
   private String description;
   private ArtifactId sourceTeamWfAi = ArtifactId.SENTINEL;
   private ArtifactId destTeamWfAi = ArtifactId.SENTINEL;
   private AtsWorkDefinitionToken workDefTok = AtsWorkDefinitionToken.SENTINEL;

   public StaticTaskDefinition() {
      // for jax-rs
   }

   public StaticTaskDefinition andRelatedToState(StateToken state) {
      setRelatedToState(state.getName());
      return this;
   }

   public String getRelatedToState() {
      return relatedToState;
   }

   public void setRelatedToState(String relatedToState) {
      this.relatedToState = relatedToState;
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

   public AtsWorkDefinitionToken getWorkDefTok() {
      return workDefTok;
   }

   public void setWorkDefTok(AtsWorkDefinitionToken workDefTok) {
      this.workDefTok = workDefTok;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}

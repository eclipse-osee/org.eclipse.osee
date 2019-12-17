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

import org.eclipse.osee.ats.api.config.tx.IAtsActionableItemArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * Builder to create task set that gets tasks from sibling's change report
 *
 * @author Donald G. Dunne
 */
public class CreateChangeReportTasksDefinitionBuilder extends CreateTasksDefinitionBuilder {

   public CreateChangeReportTasksDefinitionBuilder(Long id, String name) {
      super(id, name);
   }

   public CreateChangeReportTasksDefinitionBuilder(AtsTaskDefToken taskSetToken) {
      super(taskSetToken);
   }

   public CreateChangeReportTasksDefinitionBuilder andFromSiblingTeam(IAtsTeamDefinitionArtifactToken siblingTeam) {
      createTasksDef.getChgRptOptions().setFromSiblingTeamDef(siblingTeam);
      return this;
   }

   public CreateChangeReportTasksDefinitionBuilder andArtifactType(ArtifactTypeToken... artifactTypes) {
      for (ArtifactTypeToken artType : artifactTypes) {
         createTasksDef.getChgRptOptions().andArtifactType(artType);
      }
      return this;
   }

   public CreateChangeReportTasksDefinitionBuilder andNotArtifactType(ArtifactTypeToken... artifactTypes) {
      for (ArtifactTypeToken artType : artifactTypes) {
         createTasksDef.getChgRptOptions().andNotArtifactType(artType);
      }
      return this;
   }

   public CreateChangeReportTasksDefinitionBuilder andAttribute(AttributeTypeToken... attributeTypeTokens) {
      for (AttributeTypeToken attrType : attributeTypeTokens) {
         createTasksDef.getChgRptOptions().andAttributeType(attrType);
      }
      return this;
   }

   public CreateChangeReportTasksDefinitionBuilder andNotAttribute(AttributeTypeToken... attributeTypeTokens) {
      for (AttributeTypeToken attrType : attributeTypeTokens) {
         createTasksDef.getChgRptOptions().andNotAttributeType(attrType);
      }
      return this;
   }

   public CreateChangeReportTasksDefinitionBuilder andToSiblingTeamAi(IAtsTeamDefinitionArtifactToken teamDef, IAtsActionableItemArtifactToken ai) {
      createTasksDef.getChgRptOptions().andToSiblingTeam(teamDef, ai);
      return this;
   }

   public CreateChangeReportTasksDefinitionBuilder andToSiblingTeamAi(IAtsTeamDefinitionArtifactToken teamDef, IAtsActionableItemArtifactToken ai, ChangeReportTaskNameProviderToken nameProviderId) {
      createTasksDef.getChgRptOptions().andToSiblingTeam(teamDef, ai, nameProviderId);
      return this;
   }

}

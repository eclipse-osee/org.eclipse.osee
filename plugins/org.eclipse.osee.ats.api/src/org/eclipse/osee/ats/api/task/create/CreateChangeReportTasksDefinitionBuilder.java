/*********************************************************************
 * Copyright (c) 2019 Boeing
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

   /**
    * From Team Definition will be determined from Work Type attribute, must only be one in program's confiured team def
    * hierarchy. This allows a single task def set to be used for multiple programs in single Work Def.
    */
   public CreateChangeReportTasksDefinitionBuilder andFromSiblingTeamWorkType(WorkType workType) {
      createTasksDef.getChgRptOptions().setFromSiblingTeamDefWorkType(workType);
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

   public CreateChangeReportTasksDefinitionBuilder andToSiblingTeamAi(IAtsTeamDefinitionArtifactToken teamDef,
      IAtsActionableItemArtifactToken ai) {
      createTasksDef.getChgRptOptions().andToSiblingTeam(teamDef, ai);
      return this;
   }

   public CreateChangeReportTasksDefinitionBuilder andToSiblingTeamAi(IAtsTeamDefinitionArtifactToken teamDef,
      IAtsActionableItemArtifactToken ai, ChangeReportTaskNameProviderToken nameProviderId) {
      createTasksDef.getChgRptOptions().andToSiblingTeam(teamDef, ai, nameProviderId);
      return this;
   }

   public CreateChangeReportTasksDefinitionBuilder andToSiblingWorkType(WorkType workType) {
      createTasksDef.getChgRptOptions().andToSiblingWorkType(workType);
      return this;
   }

   public CreateChangeReportTasksDefinitionBuilder andToSiblingWorkType(WorkType workType,
      ChangeReportTaskNameProviderToken nameProviderId) {
      createTasksDef.getChgRptOptions().andToSiblingWorkType(workType, nameProviderId);
      return this;
   }

   public CreateTasksDefinitionBuilder andHelper(IAtsTaskSetDefinitionHelper helper) {
      createTasksDef.andHelper(helper);
      return this;
   }

}

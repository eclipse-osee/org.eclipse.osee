/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.program;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.config.copy.CopyAtsConfigurationOperation;
import org.eclipse.osee.ats.ide.config.copy.CopyAtsValidation;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Donald G. Dunne
 */
public class CopyCommonAtsConfigurationOperation extends CopyAtsConfigurationOperation {

   private final AtsConfigData configData;

   public CopyCommonAtsConfigurationOperation(AtsConfigData data, XResultData resultData) {
      super(data, resultData);
      configData = data;
   }

   @Override
   protected CopyAtsValidation getCopyAtsValidation() {
      return new CopyCommonAtsValidation(configData, resultData);
   }

   @Override
   protected IAtsActionableItem createActionableItems(IAtsChangeSet changes, IAtsActionableItem fromAi,
      IAtsActionableItem parentAiArt) {
      IAtsActionableItem newAi = super.createActionableItems(changes, fromAi, parentAiArt);
      if (newAi != null) {
         Artifact newAiArt = AtsApiService.get().getQueryServiceIde().getArtifact(newAi);
         for (Attribute<?> attr : newAiArt.getAttributes(CoreAttributeTypes.StaticId)) {
            String newStaticId = attr.getDisplayableString().replaceAll(
               AtsApiService.get().getProgramService().getNamespace(configData.getFromProgram()),
               AtsApiService.get().getProgramService().getNamespace(configData.getNewProgram()));
            if (!newStaticId.equals(attr.getValue())) {
               attr.setFromString(newStaticId);
               resultData.log("   - Converted static id to " + attr.getDisplayableString());
            }
         }
      }
      return newAi;
   }

   @Override
   protected IAtsTeamDefinition createTeamDefinitions(IAtsChangeSet changes, IAtsTeamDefinition fromTeamDef,
      IAtsTeamDefinition parentTeamDef) {
      IAtsTeamDefinition newTeamDef = super.createTeamDefinitions(changes, fromTeamDef, parentTeamDef);
      Artifact newTeamDefArt = AtsApiService.get().getQueryServiceIde().getArtifact(newTeamDef);

      for (Attribute<?> attr : newTeamDefArt.getAttributes(CoreAttributeTypes.StaticId)) {
         String newStaticId = attr.getDisplayableString().replaceAll(
            AtsApiService.get().getProgramService().getNamespace(configData.getFromProgram()),
            AtsApiService.get().getProgramService().getNamespace(configData.getNewProgram()));
         if (!newStaticId.equals(attr.getValue())) {
            attr.setFromString(newStaticId);
            resultData.log("   - Converted static id to " + attr.getDisplayableString());
         }
      }
      return newTeamDef;
   }
}

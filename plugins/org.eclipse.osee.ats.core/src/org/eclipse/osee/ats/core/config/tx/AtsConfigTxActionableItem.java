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

package org.eclipse.osee.ats.core.config.tx;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.Csci;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.config.tx.AtsActionableItemArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsActionableItemArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxActionableItem;
import org.eclipse.osee.ats.api.config.tx.IAtsProgramArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigTxActionableItem extends AbstractAtsConfigTxObject<IAtsConfigTxActionableItem> implements IAtsConfigTxActionableItem {

   private final IAtsActionableItem ai;

   public AtsConfigTxActionableItem(IAtsObject atsObject, AtsApi atsApi, IAtsChangeSet changes, IAtsConfigTx cfgTx) {
      super(atsObject, atsApi, changes, cfgTx);
      Conditions.assertTrue(atsObject instanceof IAtsActionableItem, "AtsObject must be of type IAtsActionableItem");
      ai = (IAtsActionableItem) atsObject;
   }

   @Override
   public IAtsConfigTxActionableItem createChildActionableItem(IAtsActionableItemArtifactToken childTok) {
      IAtsConfigTxActionableItem child = cfgTx.createActionableItem(childTok);
      changes.relate(ai, CoreRelationTypes.DefaultHierarchical_Child, child.getAi());
      return child;
   }

   @Override
   public IAtsConfigTxActionableItem createChildActionableItem(String name) {
      return createChildActionableItem(AtsActionableItemArtifactToken.valueOf(Lib.generateArtifactIdAsInt(), name));
   }

   @Override
   public IAtsConfigTxActionableItem andActive(boolean active) {
      changes.setSoleAttributeValue(ai, AtsAttributeTypes.Active, active);
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andActionable(boolean actionable) {
      changes.setSoleAttributeValue(ai, AtsAttributeTypes.Actionable, actionable);
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andTeamDef(IAtsTeamDefinitionArtifactToken teamDefTok) {
      IAtsTeamDefinition teamDef = cfgTx.getTeamDef(teamDefTok);
      if (teamDef == null || teamDef.isInvalid()) {
         teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(teamDefTok);
      }
      Conditions.assertNotNull(teamDef, "Team Definition must be created before AI %s", ai);
      changes.relate(ai, AtsRelationTypes.TeamActionableItem_TeamDefinition, teamDef);
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andTeamDef(String name) {
      IAtsTeamDefinition teamDef = cfgTx.getTeamDef(name);
      if (teamDef == null || teamDef.isInvalid()) {
         teamDef = atsApi.getTeamDefinitionService().getTeamDefinition(name);
      }
      Conditions.assertNotNull(teamDef, "Team Definition must be created before AI %s", ai);
      changes.relate(ai, AtsRelationTypes.TeamActionableItem_TeamDefinition, teamDef);
      return this;
   }

   @Override
   public IAtsActionableItem getAi() {
      return ai;
   }

   @Override
   public IAtsConfigTxActionableItem andChildAis(String... aiNames) {
      for (String aiName : aiNames) {
         IAtsConfigTxActionableItem childAi = createChildActionableItem(aiName);
         changes.addChild(ai, childAi.getAi());
      }
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andChildAis(IAtsActionableItemArtifactToken... ais) {
      for (IAtsActionableItemArtifactToken cai : ais) {
         IAtsConfigTxActionableItem childAi = createChildActionableItem(cai);
         changes.addChild(ai, childAi.getAi());
      }
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andWorkType(WorkType workType) {
      changes.addAttribute(ai, AtsAttributeTypes.WorkType, workType.name());
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andProgram(IAtsProgramArtifactToken program) {
      changes.addAttribute(ai, AtsAttributeTypes.ProgramId, program);
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andCsci(Csci... cscis) {
      for (Csci csci : cscis) {
         changes.addAttribute(ai, AtsAttributeTypes.CSCI, csci.name());
      }
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andAccessContexts(AccessContextToken... contextIds) {
      for (AccessContextToken id : contextIds) {
         and(CoreAttributeTypes.AccessContextId, String.format("%s, %s", id.getIdString(), id.getName()));
      }
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andTag(String... tags) {
      for (String tag : tags) {
         and(CoreAttributeTypes.StaticId, tag);
      }
      return this;
   }

}

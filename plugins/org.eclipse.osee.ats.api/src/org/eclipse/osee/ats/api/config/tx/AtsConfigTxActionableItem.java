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
package org.eclipse.osee.ats.api.config.tx;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinition;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
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
      changes.addChild(ai, child.getAi());
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
   public IAtsConfigTxActionableItem andRules(RuleDefinition... rules) {
      List<Object> ruleStrs = new ArrayList<>();
      for (RuleDefinition rule : rules) {
         ruleStrs.add(rule.getName());
      }
      changes.setAttributeValues(ai, AtsAttributeTypes.RuleDefinition, ruleStrs);
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andAccessContextId(String contextId) {
      changes.setSoleAttributeValue(ai, CoreAttributeTypes.AccessContextId, contextId);
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
      changes.relate(ai, AtsRelationTypes.TeamActionableItem_Team, teamDef);
      return this;
   }

   @Override
   public IAtsConfigTxActionableItem andTeamDef(String name) {
      IAtsTeamDefinition teamDef = cfgTx.getTeamDef(name);
      if (teamDef == null || teamDef.isInvalid()) {
         teamDef = atsApi.getTeamDefinitionService().getTeamDefinition(name);
      }
      Conditions.assertNotNull(teamDef, "Team Definition must be created before AI %s", ai);
      changes.relate(ai, AtsRelationTypes.TeamActionableItem_Team, teamDef);
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

}

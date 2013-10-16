/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.store;

import java.util.Collection;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.IAtsUserAdmin;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.IVersionFactory;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemArtifactReader extends AbstractAtsArtifactReader<IAtsActionableItem> {

   private final IAtsUserAdmin userAdmin;

   public ActionableItemArtifactReader(IActionableItemFactory actionableItemFactory, ITeamDefinitionFactory teamDefFactory, IVersionFactory versionFactory, IAtsUserAdmin userAdmin) {
      super(actionableItemFactory, teamDefFactory, versionFactory);
      this.userAdmin = userAdmin;
   }

   @Override
   public IAtsActionableItem load(AtsArtifactConfigCache cache, Artifact aiArt) throws OseeCoreException {
      IAtsActionableItem aia = getOrCreateActionableItem(cache, aiArt);
      aia.setName(aiArt.getName());
      aia.setActive(aiArt.getSoleAttributeValue(AtsAttributeTypes.Active, false));
      aia.setActionable(aiArt.getSoleAttributeValue(AtsAttributeTypes.Actionable, false));
      aia.setDescription(aiArt.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
      Collection<Artifact> teamDefArts = aiArt.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team);
      if (!teamDefArts.isEmpty()) {
         Artifact teamDefArt = teamDefArts.iterator().next();
         IAtsTeamDefinition teamDef = getOrCreateTeamDefinition(cache, teamDefArt);
         aia.setTeamDefinition(teamDef);
         if (!teamDef.getActionableItems().contains(aia)) {
            teamDef.getActionableItems().add(aia);
         }
      }
      for (String staticId : aiArt.getAttributesToStringList(CoreAttributeTypes.StaticId)) {
         aia.getStaticIds().add(staticId);
      }
      Artifact parentAiArt = aiArt.getParent();
      if (parentAiArt != null && parentAiArt.isOfType(AtsArtifactTypes.ActionableItem)) {
         IAtsActionableItem parentAi = getOrCreateActionableItem(cache, parentAiArt);
         aia.setParentActionableItem(parentAi);
         parentAi.getChildrenActionableItems().add(aia);
      }
      for (Artifact userArt : aiArt.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User)) {
         IAtsUser user = userAdmin.getUserFromOseeUser((User) userArt);
         aia.getSubscribed().add(user);
      }
      for (Artifact userArt : aiArt.getRelatedArtifacts(AtsRelationTypes.ActionableItemLead_Lead)) {
         IAtsUser user = userAdmin.getUserFromOseeUser((User) userArt);
         aia.getLeads().add(user);
      }
      for (Artifact childAiArt : aiArt.getChildren()) {
         if (childAiArt.isOfType(AtsArtifactTypes.ActionableItem)) {
            IAtsActionableItem childAi = getOrCreateActionableItem(cache, childAiArt);
            aia.getChildrenActionableItems().add(childAi);
            childAi.setParentActionableItem(aia);
         }
      }
      return aia;
   }
}

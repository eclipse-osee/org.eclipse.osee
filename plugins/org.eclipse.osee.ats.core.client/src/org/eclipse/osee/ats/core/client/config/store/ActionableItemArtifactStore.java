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
package org.eclipse.osee.ats.core.client.config.store;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.config.AtsObjectsClient;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemArtifactStore extends ArtifactAtsObjectStore {
   AtsConfigCache cache = AtsConfigCache.instance;

   public ActionableItemArtifactStore(IAtsActionableItem teamDef) {
      super(teamDef, AtsArtifactTypes.ActionableItem, AtsUtilCore.getAtsBranchToken());
   }

   public ActionableItemArtifactStore(Artifact artifact, AtsConfigCache atsConfigCache) throws OseeCoreException {
      super(null, AtsArtifactTypes.ActionableItem, AtsUtilCore.getAtsBranchToken());
      this.artifact = artifact;
      if (atsConfigCache != null) {
         cache = atsConfigCache;
      }
      load();
   }

   @Override
   public Result saveToArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact aiArt = getArtifact();
      if (aiArt == null) {
         throw new OseeArgumentException("Actionable Item must be created first before save");
      }
      IAtsActionableItem aia = getActionableItem();
      aiArt.setName(aia.getName());
      aiArt.setSoleAttributeValue(AtsAttributeTypes.Active, aia.isActive());
      boolean actionable = aiArt.getSoleAttributeValue(AtsAttributeTypes.Actionable, false);
      if (actionable != aia.isActionable()) {
         aiArt.setSoleAttributeValue(AtsAttributeTypes.Actionable, aia.isActionable());
      }
      if (Strings.isValid(aia.getDescription())) {
         aiArt.setSoleAttributeValue(AtsAttributeTypes.Description, aia.getDescription());
      }

      // set new team definition if necessary
      if (aia.getTeamDefinition() != null) {
         Artifact teamDefArt = new TeamDefinitionArtifactStore(aia.getTeamDefinition()).getArtifact();
         if (teamDefArt != null && !teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_ActionableItem).contains(
            aiArt)) {
            aiArt.deleteRelations(AtsRelationTypes.TeamActionableItem_Team);
            aiArt.addRelation(AtsRelationTypes.TeamActionableItem_Team, teamDefArt);
            teamDefArt.persist(transaction);
         }
      }

      // set new children team defs if changed
      List<String> newGuids = AtsObjects.toGuids(aia.getChildrenActionableItems());
      List<String> currGuids = Artifacts.toGuids(aiArt.getChildren());
      // remove curr children that are not part of new children
      for (Artifact child : aiArt.getChildren()) {
         if (child.isOfType(AtsArtifactTypes.ActionableItem)) {
            if (!newGuids.contains(child.getGuid())) {
               aiArt.deleteRelation(CoreRelationTypes.Default_Hierarchical__Child, child);
            }
         }
      }
      // add new children that are not part of curr children
      for (String newGuid : newGuids) {
         if (!currGuids.contains(newGuid)) {
            Artifact newArt = null;
            IAtsActionableItem newAi = cache.getSoleByGuid(newGuid, IAtsActionableItem.class);
            if (newAi != null) {
               newArt = AtsObjectsClient.getSoleArtifact(newAi);
            }
            // if not persisted yet, it should be in artifact cache
            if (newArt == null) {
               newArt = ArtifactCache.getActive(newGuid, AtsUtilCore.getAtsBranchToken());
            }
            aiArt.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
         }
      }

      // update relations for versions and users
      setRelationsOfType(aiArt, aia.getSubscribed(), AtsRelationTypes.SubscribedUser_User);
      setRelationsOfType(aiArt, aia.getLeads(), AtsRelationTypes.ActionableItemLead_Lead);

      // update staticIds
      if (!aia.getStaticIds().isEmpty()) {
         aiArt.setAttributeValues(CoreAttributeTypes.StaticId, aia.getStaticIds());
      }

      // set parent artifact to top team def
      if (aia.getParentActionableItem() == null && !aia.getGuid().equals(
         ActionableItems.getTopActionableItem().getGuid())) {
         // if parent is null, add to top team definition
         Artifact topAIArt = AtsObjectsClient.getSoleArtifact(ActionableItems.getTopActionableItem());
         topAIArt.addChild(aiArt);
         topAIArt.persist(transaction);
      } else {
         // else reset parent if necessary
         Artifact parentAiArt = aiArt.getParent();
         if (parentAiArt != null) {
            if (parentAiArt.isOfType(AtsArtifactTypes.ActionableItem)) {
               if (!parentAiArt.getGuid().equals(aia.getParentActionableItem().getGuid())) {
                  Artifact newParentAIArt = AtsObjectsClient.getSoleArtifact(aia);
                  newParentAIArt.addChild(aiArt);
                  newParentAIArt.persist(transaction);
                  parentAiArt.persist(transaction);
               }
            }
         }
      }
      aiArt.persist(transaction);
      cache.cache(aia);
      return Result.TrueResult;
   }

   public void load() throws OseeCoreException {
      Artifact aiArt = getArtifact();
      if (aiArt != null) {
         IAtsActionableItem aia = cache.getActionableItemFactory().getOrCreate(aiArt.getGuid(), aiArt.getName());
         aia.setHumanReadableId(aiArt.getHumanReadableId());
         aia.setName(aiArt.getName());
         atsObject = aia;
         aia.setActive(aiArt.getSoleAttributeValue(AtsAttributeTypes.Active, false));
         aia.setActionable(aiArt.getSoleAttributeValue(AtsAttributeTypes.Actionable, false));
         aia.setDescription(aiArt.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
         Collection<Artifact> teamDefArts = aiArt.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team);
         if (!teamDefArts.isEmpty()) {
            Artifact teamDefArt = teamDefArts.iterator().next();
            IAtsTeamDefinition teamDef =
               cache.getTeamDefinitionFactory().getOrCreate(teamDefArt.getGuid(), teamDefArt.getName());
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
            IAtsActionableItem parentAi =
               cache.getActionableItemFactory().getOrCreate(parentAiArt.getGuid(), parentAiArt.getName());
            aia.setParentActionableItem(parentAi);
            parentAi.getChildrenActionableItems().add(aia);
         }
         for (Artifact userArt : aiArt.getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User)) {
            IAtsUser user = AtsUsersClient.getUserFromOseeUser((User) userArt);
            aia.getSubscribed().add(user);
         }
         for (Artifact userArt : aiArt.getRelatedArtifacts(AtsRelationTypes.ActionableItemLead_Lead)) {
            IAtsUser user = AtsUsersClient.getUserFromOseeUser((User) userArt);
            aia.getLeads().add(user);
         }
         for (Artifact childAiArt : aiArt.getChildren()) {
            if (childAiArt.isOfType(AtsArtifactTypes.ActionableItem)) {
               IAtsActionableItem childAi =
                  cache.getActionableItemFactory().getOrCreate(childAiArt.getGuid(), childAiArt.getName());
               aia.getChildrenActionableItems().add(childAi);
               childAi.setParentActionableItem(aia);
            }
         }
      }
   }

   public IAtsActionableItem getTeamDef() {
      return (IAtsActionableItem) atsObject;
   }

   public IAtsActionableItem getActionableItem() {
      return (IAtsActionableItem) atsObject;
   }

}

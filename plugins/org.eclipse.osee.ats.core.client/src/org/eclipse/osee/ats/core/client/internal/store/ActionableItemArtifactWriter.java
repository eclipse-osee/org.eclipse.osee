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

import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemArtifactWriter extends AbstractAtsArtifactWriter<IAtsActionableItem> {

   @Override
   public Artifact store(IAtsActionableItem ai, AtsArtifactConfigCache cache, IAtsChangeSet changes) throws OseeCoreException {
      Artifact artifact = getArtifactOrCreate(cache, AtsArtifactTypes.ActionableItem, ai, changes);
      store(ai, artifact, cache, changes);
      return artifact;
   }

   @Override
   public Artifact store(IAtsActionableItem ai, Artifact artifact, AtsArtifactConfigCache cache, IAtsChangeSet changes) throws OseeCoreException {
      artifact.setName(ai.getName());
      artifact.setSoleAttributeValue(AtsAttributeTypes.Active, ai.isActive());
      boolean actionable = artifact.getSoleAttributeValue(AtsAttributeTypes.Actionable, false);
      if (actionable != ai.isActionable()) {
         artifact.setSoleAttributeValue(AtsAttributeTypes.Actionable, ai.isActionable());
      }
      if (Strings.isValid(ai.getDescription())) {
         artifact.setSoleAttributeValue(AtsAttributeTypes.Description, ai.getDescription());
      }

      // set new team definition if necessary
      if (ai.getTeamDefinition() != null) {
         Artifact teamDefArt = cache.getArtifact(ai.getTeamDefinition());
         if (teamDefArt != null && !teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_ActionableItem).contains(
            artifact)) {
            artifact.deleteRelations(AtsRelationTypes.TeamActionableItem_Team);
            artifact.addRelation(AtsRelationTypes.TeamActionableItem_Team, teamDefArt);
            changes.add(teamDefArt);
         }
      }

      // set new children team defs if changed
      List<String> newGuids = AtsObjects.toGuids(ai.getChildrenActionableItems());
      List<String> currGuids = Artifacts.toGuids(artifact.getChildren());
      // remove curr children that are not part of new children
      for (Artifact child : artifact.getChildren()) {
         if (child.isOfType(AtsArtifactTypes.ActionableItem)) {
            if (!newGuids.contains(child.getGuid())) {
               artifact.deleteRelation(CoreRelationTypes.Default_Hierarchical__Child, child);
            }
         }
      }
      // add new children that are not part of curr children
      for (String newGuid : newGuids) {
         if (!currGuids.contains(newGuid)) {
            Artifact newArt = null;
            IAtsActionableItem newAi = cache.getSoleByGuid(newGuid, IAtsActionableItem.class);
            if (newAi != null) {
               newArt = cache.getSoleArtifact(newAi);
            }
            // if not persisted yet, it should be in artifact cache
            if (newArt == null) {
               newArt = ArtifactCache.getActive(newGuid, AtsUtilCore.getAtsBranch());
            }
            artifact.addRelation(CoreRelationTypes.Default_Hierarchical__Child, newArt);
         }
      }

      // update relations for versions and users
      setRelationsOfType(cache, artifact, ai.getSubscribed(), AtsRelationTypes.SubscribedUser_User);
      setRelationsOfType(cache, artifact, ai.getLeads(), AtsRelationTypes.ActionableItemLead_Lead);

      // update staticIds
      if (!ai.getStaticIds().isEmpty()) {
         artifact.setAttributeValues(CoreAttributeTypes.StaticId, ai.getStaticIds());
      }

      // set parent artifact to top team def
      if (ai.getParentActionableItem() == null && !ai.getGuid().equals(
         ActionableItems.getTopActionableItem(AtsClientService.get().getConfig()).getGuid())) {
         // if parent is null, add to top team definition
         Artifact topAIArt =
            cache.getSoleArtifact(ActionableItems.getTopActionableItem(AtsClientService.get().getConfig()));
         topAIArt.addChild(artifact);
         changes.add(topAIArt);
      } else {
         // else reset parent if necessary
         Artifact parentAiArt = artifact.getParent();
         if (parentAiArt != null) {
            if (parentAiArt.isOfType(AtsArtifactTypes.ActionableItem)) {
               if (!parentAiArt.getGuid().equals(ai.getParentActionableItem().getGuid())) {
                  Artifact newParentAIArt = cache.getSoleArtifact(ai);
                  newParentAIArt.addChild(artifact);
                  changes.add(newParentAIArt);
                  changes.add(parentAiArt);
               }
            }
         }
      }
      changes.add(artifact);
      cache.cache(ai);
      return artifact;
   }

}

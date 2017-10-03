/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.agile;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class AgileItemWriter {

   private final IAtsServices services;
   private final JaxAgileItem newItem;
   private final IAgileService agileService;

   public AgileItemWriter(IAtsServices services, IAgileService agileService, JaxAgileItem newItem) {
      this.services = services;
      this.agileService = agileService;
      this.newItem = newItem;
   }

   public JaxAgileItem write() {
      IAtsChangeSet changes =
         services.getStoreService().createAtsChangeSet("Update new Agile Item", AtsCoreUsers.SYSTEM_USER);

      if (newItem.isSetFeatures()) {
         Collection<IAgileFeatureGroup> features = agileService.getAgileFeatureGroups(newItem.getFeatures());
         List<ArtifactToken> featureArts = new LinkedList<>();
         for (IAgileFeatureGroup feature : features) {
            featureArts.add(feature.getStoreObject());
         }
         for (ArtifactToken awa : services.getArtifacts(newItem.getUuids())) {
            for (IAgileFeatureGroup feature : features) {
               ArtifactToken featureArt = feature.getStoreObject();
               if (!services.getRelationResolver().areRelated(featureArt,
                  AtsRelationTypes.AgileFeatureToItem_FeatureGroup, awa)) {
                  changes.relate(feature, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
               }
            }
            for (ArtifactToken featureArt : services.getRelationResolver().getRelated(awa,
               AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
               if (!featureArts.contains(featureArt)) {
                  changes.unrelate(featureArt, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
               }
            }
         }
      } else if (newItem.isRemoveFeatures()) {
         for (ArtifactToken awa : services.getArtifacts(newItem.getUuids())) {
            for (ArtifactToken feature : services.getRelationResolver().getRelated(awa,
               AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
               changes.unrelate(feature, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
            }
         }
      }

      if (newItem.isSetSprint()) {
         ArtifactToken sprintArt = services.getArtifact(newItem.getSprintUuid());
         IAgileSprint sprint = services.getAgileService().getAgileSprint(sprintArt);
         for (ArtifactToken awa : services.getArtifacts(newItem.getUuids())) {
            if (sprint != null) {
               changes.setRelation(sprint, AtsRelationTypes.AgileSprintToItem_AtsItem, awa);
            } else {
               changes.unrelateAll(awa, AtsRelationTypes.AgileSprintToItem_AtsItem);
            }
            changes.add(sprint);
         }
      }

      if (newItem.isSetBacklog()) {
         ArtifactToken backlogArt = services.getArtifact(newItem.getBacklogUuid());
         IAgileSprint backlog = services.getAgileService().getAgileSprint(backlogArt);
         for (ArtifactToken awa : services.getArtifacts(newItem.getUuids())) {
            if (backlog != null) {
               changes.setRelation(backlog, AtsRelationTypes.Goal_Member, awa);
            } else {
               changes.unrelateAll(awa, AtsRelationTypes.Goal_Member);
            }
            changes.add(backlog);
         }
      }

      if (!changes.isEmpty()) {
         changes.execute();
      }
      return newItem;
   }
}

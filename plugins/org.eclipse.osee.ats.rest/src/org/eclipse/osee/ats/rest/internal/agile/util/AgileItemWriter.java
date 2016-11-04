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
package org.eclipse.osee.ats.rest.internal.agile.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.JaxAgileItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.AtsChangeSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AgileItemWriter {

   private final IAtsServer atsServer;
   private final JaxAgileItem newItem;
   private final IAgileService agileService;

   public AgileItemWriter(IAtsServer atsServer, IAgileService agileService, JaxAgileItem newItem) {
      this.atsServer = atsServer;
      this.agileService = agileService;
      this.newItem = newItem;
   }

   public JaxAgileItem write() {
      IAtsChangeSet changes = (AtsChangeSet) atsServer.getStoreService().createAtsChangeSet("Update new Agile Item",
         AtsCoreUsers.SYSTEM_USER);

      if (newItem.isSetFeatures()) {
         Collection<IAgileFeatureGroup> features = agileService.getAgileFeatureGroups(newItem.getFeatures());
         List<ArtifactReadable> featureArts = new LinkedList<>();
         for (IAgileFeatureGroup feature : features) {
            featureArts.add((ArtifactReadable) feature.getStoreObject());
         }
         for (ArtifactReadable awa : atsServer.getArtifacts(newItem.getUuids())) {
            for (IAgileFeatureGroup feature : features) {
               ArtifactReadable featureArt = (ArtifactReadable) feature.getStoreObject();
               if (!featureArt.areRelated(AtsRelationTypes.AgileFeatureToItem_FeatureGroup, awa)) {
                  changes.relate(feature, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
               }
            }
            for (ArtifactReadable featureArt : awa.getRelated(AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
               if (!featureArts.contains(featureArt)) {
                  changes.unrelate(featureArt, AtsRelationTypes.AgileFeatureToItem_AtsItem, awa);
               }
            }
         }
      } else if (newItem.isRemoveFeatures()) {
         for (ArtifactReadable awa : atsServer.getArtifacts(newItem.getUuids())) {
            for (ArtifactReadable feature : awa.getRelated(AtsRelationTypes.AgileFeatureToItem_FeatureGroup)) {
               changes.unrelate(feature, AtsRelationTypes.AgileFeatureToItem_FeatureGroup, awa);
            }
         }
      }

      if (newItem.isSetSprint()) {
         ArtifactReadable sprintArt = atsServer.getArtifact(newItem.getSprintUuid());
         IAgileSprint sprint = atsServer.getAgileService().getAgileSprint(sprintArt);
         for (ArtifactReadable awa : atsServer.getArtifacts(newItem.getUuids())) {
            if (sprint != null) {
               changes.setRelation(sprint, AtsRelationTypes.AgileSprintToItem_AtsItem, awa);
            } else {
               changes.unrelateAll(awa, AtsRelationTypes.AgileSprintToItem_AtsItem);
            }
            changes.add(sprint);
         }
      }

      if (newItem.isSetBacklog()) {
         ArtifactReadable backlogArt = atsServer.getArtifact(newItem.getBacklogUuid());
         IAgileSprint backlog = atsServer.getAgileService().getAgileSprint(backlogArt);
         for (ArtifactReadable awa : atsServer.getArtifacts(newItem.getUuids())) {
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

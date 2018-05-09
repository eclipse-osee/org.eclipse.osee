/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.insertion.InsertionActivityEndpointApi;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * Donald G. Dunne
 */
public class InsertionActivityEndpointImpl extends BaseConfigEndpointImpl<JaxInsertionActivity> implements InsertionActivityEndpointApi {

   private final long insertionId;

   public InsertionActivityEndpointImpl(AtsApi atsApi) {
      this(atsApi, 0L);
   }

   public InsertionActivityEndpointImpl(AtsApi atsApi, long insertionId) {
      super(AtsArtifactTypes.InsertionActivity, null, atsApi);
      this.insertionId = insertionId;
   }

   @PUT
   @Override
   public Response update(JaxInsertionActivity activity) throws Exception {
      ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(activity.getId());
      if (artifact == null) {
         throw new OseeStateException("Artifact with id %d not found", activity.getId());
      }
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create " + artifactType.getName(), AtsCoreUsers.SYSTEM_USER);
      ArtifactToken configArtifact = changes.createArtifact(artifactType, activity.getName(), activity.getId());
      IAtsInsertionActivity insertionActivity = atsApi.getProgramService().getInsertionActivityById(configArtifact);
      if (!configArtifact.getName().equals(activity.getName())) {
         changes.setSoleAttributeValue(insertionActivity, CoreAttributeTypes.Name, activity.getName());
      }
      changes.execute();
      return Response.created(new URI("/" + activity.getId())).build();
   }

   @Override
   public JaxInsertionActivity getConfigObject(ArtifactId artifact) {
      JaxInsertionActivity jaxInsertion = new JaxInsertionActivity();
      IAtsInsertionActivity insertion = atsApi.getProgramService().getInsertionActivityById(artifact);
      jaxInsertion.setName(insertion.getName());
      jaxInsertion.setId(insertion.getId());
      jaxInsertion.setActive(insertion.isActive());
      jaxInsertion.setDescription(insertion.getDescription());
      return jaxInsertion;
   }

   @Override
   public List<JaxInsertionActivity> getObjects() {
      List<JaxInsertionActivity> insertions = new ArrayList<>();
      if (insertionId == 0L) {
         for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artifactType)) {
            insertions.add(getConfigObject(art));
         }
      } else {
         for (ArtifactToken activityArt : atsApi.getRelationResolver().getRelated(
            atsApi.getQueryService().getArtifact(insertionId),
            AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity)) {
            JaxInsertionActivity activity = getConfigObject(activityArt);
            activity.setInsertionId(insertionId);
            insertions.add(activity);
         }
      }
      return insertions;
   }

   @Override
   protected void create(JaxInsertionActivity jaxInsertionActivity, ArtifactId insertionActivityArtId, IAtsChangeSet changes) {
      ArtifactReadable insertionActivityArt = (ArtifactReadable) insertionActivityArtId;
      if (insertionActivityArt.getRelatedCount(AtsRelationTypes.InsertionToInsertionActivity_Insertion) == 0) {
         ArtifactReadable insertionArt =
            (ArtifactReadable) atsApi.getQueryService().getArtifact(jaxInsertionActivity.getInsertionId());
         changes.relate(insertionArt, AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity,
            insertionActivityArt);
      }
   }

}

/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.InsertionActivityEndpointApi;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class InsertionActivityEndpointImpl extends BaseConfigEndpointImpl<JaxInsertionActivity> implements InsertionActivityEndpointApi {

   private final long insertionActId;

   public InsertionActivityEndpointImpl(AtsApi atsApi) {
      this(atsApi, 0L);
   }

   public InsertionActivityEndpointImpl(AtsApi atsApi, long insertionId) {
      super(AtsArtifactTypes.InsertionActivity, null, atsApi);
      this.insertionActId = insertionId;
   }

   @Override
   public List<JaxInsertionActivity> get() {
      return getConfigs();
   }

   @Override
   public JaxInsertionActivity getConfig(ArtifactId artifact) {
      return atsApi.getAgileService().getInsertionActivity(artifact);
   }

   @Override
   public JaxInsertionActivity getConfig(long id) {
      JaxInsertionActivity jInsertionAct = super.getConfig(id);
      ArtifactToken insertion = atsApi.getRelationResolver().getRelatedOrNull(ArtifactId.valueOf(id),
         AtsRelationTypes.InsertionToInsertionActivity_Insertion);
      jInsertionAct.setInsertionId(insertion.getId());
      return jInsertionAct;
   }

   @Override
   public List<JaxInsertionActivity> getConfigs() {
      List<JaxInsertionActivity> insertionActs = new ArrayList<>();
      if (insertionActId == 0L) {
         for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artifactType)) {
            insertionActs.add(getConfig(art));
         }
      } else {
         for (ArtifactToken activityArt : atsApi.getRelationResolver().getRelated(
            atsApi.getQueryService().getArtifact(insertionActId),
            AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity)) {
            JaxInsertionActivity activity = getConfig(activityArt);
            activity.setInsertionId(insertionActId);
            insertionActs.add(activity);
         }
      }
      return insertionActs;
   }

   @Override
   public JaxInsertionActivity update(JaxInsertionActivity insertAct) {
      return createConfig(insertAct);
   }

   @Override
   public void delete(long id) {
      deleteConfig(id);
   }

   @Override
   public JaxInsertionActivity create(JaxInsertionActivity insertAct) {
      return createConfig(insertAct);
   }

   @Override
   protected void createConfigExt(JaxInsertionActivity jaxInsertAct, ArtifactId insertionActArtId,
      IAtsChangeSet changes) {
      ArtifactReadable insertionActArt = (ArtifactReadable) insertionActArtId;
      if (insertionActArt.getRelatedCount(AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity) == 0) {
         ArtifactReadable insertionArt =
            (ArtifactReadable) atsApi.getQueryService().getArtifact(jaxInsertAct.getInsertionId());
         if (insertionArt.getRelatedCount(AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity) == 0) {
            changes.relate(insertionArt, AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity,
               insertionActArt);
         }
      }
   }

}

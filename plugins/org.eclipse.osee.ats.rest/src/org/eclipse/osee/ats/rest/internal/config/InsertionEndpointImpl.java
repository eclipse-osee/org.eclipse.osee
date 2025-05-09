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
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class InsertionEndpointImpl extends BaseConfigEndpointImpl<JaxInsertion> implements InsertionEndpointApi {

   private final long programId;

   public InsertionEndpointImpl(AtsApi atsApi) {
      this(atsApi, 0L);
   }

   public InsertionEndpointImpl(AtsApi atsApi, long programId) {
      super(AtsArtifactTypes.Insertion, null, atsApi);
      this.programId = programId;
   }

   @Override
   public List<JaxInsertion> get() {
      return getConfigs();
   }

   @Override
   public JaxInsertion create(JaxInsertion insertion) {
      return createConfig(insertion);
   }

   @Override
   public void delete(long id) {
      deleteConfig(id);
   }

   @Override
   public JaxInsertion update(JaxInsertion insertion) {
      return createConfig(insertion);
   }

   @Override
   public JaxInsertion getConfig(ArtifactId artifact) {
      return atsApi.getAgileService().getInsertion(artifact);
   }

   @Override
   public JaxInsertion getConfig(long id) {
      JaxInsertion jInsertion = super.getConfig(id);
      ArtifactToken program = atsApi.getRelationResolver().getRelatedOrNull(ArtifactId.valueOf(id),
         AtsRelationTypes.ProgramToInsertion_Program);
      jInsertion.setProgramId(program.getId());
      return jInsertion;
   }

   @Override
   public List<JaxInsertion> getConfigs() {
      List<JaxInsertion> insertions = new ArrayList<>();
      if (programId == 0L) {
         for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artifactType)) {
            insertions.add(getConfig(art));
         }
      } else {
         for (ArtifactToken insertionArt : atsApi.getRelationResolver().getRelated(
            atsApi.getQueryService().getArtifact(programId), AtsRelationTypes.ProgramToInsertion_Insertion)) {
            JaxInsertion insertion = atsApi.getAgileService().getInsertion(insertionArt);
            insertion.setProgramId(programId);
            insertions.add(insertion);
         }
      }
      return insertions;
   }

   @Override
   protected void createConfigExt(JaxInsertion jaxInsertion, ArtifactId insertionArtId, IAtsChangeSet changes) {
      ArtifactReadable insertionArt = (ArtifactReadable) insertionArtId;
      if (insertionArt.getRelatedCount(AtsRelationTypes.ProgramToInsertion_Program) == 0) {
         ArtifactReadable programArt =
            (ArtifactReadable) atsApi.getQueryService().getArtifact(jaxInsertion.getProgramId());
         changes.relate(programArt, AtsRelationTypes.ProgramToInsertion_Insertion, insertionArt);
      }
   }

   @Override
   public InsertionActivityEndpointApi getInsertionActivity(long insertionId) {
      return new InsertionActivityEndpointImpl(atsApi, insertionId);
   }
}

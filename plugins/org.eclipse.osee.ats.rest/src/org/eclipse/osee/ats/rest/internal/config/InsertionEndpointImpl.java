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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.InsertionActivityEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

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

   @PUT
   @Override
   public Response update(JaxInsertion jaxInsertion) throws Exception {
      ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(jaxInsertion.getId());
      if (artifact == null) {
         throw new OseeStateException("Artifact with id %d not found", jaxInsertion.getIdString());
      }
      IAtsChangeSet changes =
         atsApi.getStoreService().createAtsChangeSet("Create " + artifactType.getName(), AtsCoreUsers.SYSTEM_USER);
      ArtifactToken insertionArt = changes.createArtifact(artifactType, jaxInsertion.getName(), jaxInsertion.getId());
      IAtsInsertion insertion = atsApi.getProgramService().getInsertionById(insertionArt);
      if (!insertionArt.getName().equals(jaxInsertion.getName())) {
         changes.setSoleAttributeValue(insertion, CoreAttributeTypes.Name, jaxInsertion.getName());
      }
      changes.execute();
      return Response.created(new URI("/" + jaxInsertion.getIdString())).build();
   }

   @Override
   public JaxInsertion getConfigObject(ArtifactId artifact) {
      JaxInsertion jaxInsertion = new JaxInsertion();
      IAtsInsertion insertion = atsApi.getProgramService().getInsertionById(artifact);
      jaxInsertion.setName(insertion.getName());
      jaxInsertion.setId(insertion.getId());
      jaxInsertion.setActive(insertion.isActive());
      jaxInsertion.setDescription(insertion.getDescription());
      return jaxInsertion;
   }

   @Override
   public List<JaxInsertion> getObjects() {
      List<JaxInsertion> insertions = new ArrayList<>();
      if (programId == 0L) {
         for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artifactType)) {
            insertions.add(getConfigObject(art));
         }
      } else {
         for (ArtifactToken insertionArt : atsApi.getRelationResolver().getRelated(
            atsApi.getQueryService().getArtifact(programId), AtsRelationTypes.ProgramToInsertion_Insertion)) {
            JaxInsertion insertion = getConfigObject(insertionArt);
            insertion.setProgramId(programId);
            insertions.add(insertion);
         }
      }
      return insertions;
   }

   @Override
   protected void create(JaxInsertion jaxInsertion, ArtifactId insertionArtId, IAtsChangeSet changes) {
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

/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.query;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceClient {

   private final AtsApi atsApi;
   private final IAtsQueryService queryService;

   public AtsQueryServiceClient(AtsApi atsApi) {
      this.atsApi = atsApi;
      this.queryService = atsApi.getQueryService();
   }

   private List<Artifact> toArtifacts(Collection<ArtifactToken> artifacts) {
      List<Artifact> results = new LinkedList<>();
      for (ArtifactToken artTok : artifacts) {
         Artifact art = toArtifact(artTok);
         if (art.isValid()) {
            results.add(art);
         }
      }
      return results;
   }

   private Artifact toArtifact(ArtifactId artId) {
      Artifact result = null;
      if (artId != null) {
         if (artId instanceof Artifact) {
            result = (Artifact) artId;
         } else if (artId.isValid()) {
            ArtifactId dbArt = AtsClientService.get().getQueryServiceClient().getArtifact(artId);
            if (dbArt instanceof Artifact) {
               result = (Artifact) dbArt;
            }
         }
      }
      return result;
   }

   public Artifact toArtifact(IAtsObject atsObject) {
      return toArtifact(atsObject.getStoreObject());
   }

   public Artifact getRealArtifact(TreeItem item) {
      Artifact result = null;
      if (item.getData() instanceof ArtifactId) {
         result = toArtifact((ArtifactId) item.getData());
      }
      return result;
   }

   public Collection<Artifact> getArtifacts(List<ArtifactId> ids, BranchId branch) {
      return toArtifacts(queryService.getArtifacts(ids, branch));
   }

   public Collection<Artifact> getArtifactsFromQuery(String query, Object... data) {
      return toArtifacts(queryService.getArtifactsFromQuery(query, data));
   }

   public Collection<Artifact> getArtifacts(BranchId branch, boolean includeInherited, IArtifactType... artifactType) {
      return toArtifacts(queryService.getArtifacts(branch, includeInherited, artifactType));
   }

   public Artifact getArtifactToken(ArtifactId artifactId) {
      return toArtifact(queryService.getArtifactToken(artifactId));
   }

   public List<Artifact> getArtifactTokensFromQuery(String query, Object... data) {
      return toArtifacts(queryService.getArtifactTokensFromQuery(query, data));
   }

   public Collection<Artifact> getRelatedToTokens(BranchId branch, ArtifactId artifact, RelationTypeSide relationType, ArtifactTypeId artifactType) {
      return toArtifacts(queryService.getRelatedToTokens(branch, artifact, relationType, artifactType));
   }

   public List<Artifact> getArtifactsByIds(String ids) {
      return toArtifacts(queryService.getArtifactsByIds(ids));
   }

   public Artifact getArtifactById(String id) {
      return toArtifact(queryService.getArtifactById(id));
   }

   public Artifact getArtifactByAtsId(String id) {
      return toArtifact(queryService.getArtifactByAtsId(id));
   }

   public Artifact getArtifactByIdOrAtsId(String id) {
      return toArtifact(queryService.getArtifactByIdOrAtsId(id));
   }

   public Artifact getArtifactByLegacyPcrId(String id) {
      return toArtifact(queryService.getArtifactByLegacyPcrId(id));
   }

   public Collection<Artifact> getArtifactsByLegacyPcrId(String id) {
      return toArtifacts(queryService.getArtifactsByLegacyPcrId(id));
   }

   public List<Artifact> getArtifacts(IArtifactType artifactType) {
      return toArtifacts(queryService.getArtifacts(artifactType));
   }

   public Artifact getConfigArtifact(IAtsConfigObject atsConfigObject) {
      return toArtifact(queryService.getConfigArtifact(atsConfigObject));
   }

   public Artifact getArtifact(ArtifactId artifact, BranchId branch) {
      return toArtifact(queryService.getArtifact(artifact, branch));
   }

   public Collection<Artifact> getArtifacts(Collection<Long> ids) {
      return toArtifacts(queryService.getArtifacts(ids));
   }

   public Artifact getArtifactByName(ArtifactTypeId artifactType, String name) {
      return toArtifact(queryService.getArtifactByName(artifactType, name));
   }

   public Artifact getArtifact(ArtifactId artifact, BranchId branch, DeletionFlag deletionFlag) {
      return toArtifact(queryService.getArtifact(artifact, branch, deletionFlag));
   }

   public Artifact getHistoricalArtifactOrNull(ArtifactId artifact, TransactionToken transaction, DeletionFlag deletionFlag) {
      return toArtifact(queryService.getHistoricalArtifactOrNull(artifact, transaction, deletionFlag));
   }

   public Artifact getArtifactByGuid(String guid) {
      return toArtifact(queryService.getArtifactByGuid(guid));
   }

   public Collection<Artifact> getArtifactsByIdsOrAtsIds(String searchStr) {
      return toArtifacts(queryService.getArtifactsByIdsOrAtsIds(searchStr));
   }

   /**
    * @return artifact from current ATS branch
    */
   public Artifact getArtifact(ArtifactId artifactId) {
      if (artifactId instanceof Artifact) {
         return (Artifact) artifactId;
      }
      return getArtifact(artifactId, atsApi.getAtsBranch());
   }

   public Artifact getArtifact(Object obj) {
      Artifact result = null;
      if (obj instanceof Artifact) {
         result = (Artifact) obj;
      } else if (obj instanceof IAtsObject) {
         result = getArtifact(((IAtsObject) obj).getStoreObject(), atsApi.getAtsBranch());
      } else if (obj instanceof ArtifactId) {
         result = getArtifact(((ArtifactId) obj), atsApi.getAtsBranch());
      } else if (obj instanceof Id) {
         result = getArtifact(((Id) obj).getId());
      } else if (obj instanceof Long) {
         result = getArtifact(ArtifactId.valueOf((Long) obj));
      }
      return result;
   }

   public Artifact getArtifact(TreeItem item) {
      return getArtifact(item.getData());
   }

}

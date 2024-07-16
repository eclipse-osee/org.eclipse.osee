/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.query;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceIde {

   private final AtsApi atsApi;
   private final IAtsQueryService queryService;

   public AtsQueryServiceIde(AtsApi atsApi) {
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
            ArtifactId dbArt = atsApi.getQueryService().getArtifact(artId);
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

   public Collection<Artifact> getArtifacts(BranchId branch, boolean includeInherited,
      ArtifactTypeToken... artifactType) {
      return toArtifacts(queryService.getArtifacts(branch, includeInherited, artifactType));
   }

   public Artifact getArtifactToken(ArtifactId artifactId) {
      return toArtifact(queryService.getArtifactToken(artifactId));
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

   public List<Artifact> getArtifacts(ArtifactTypeToken artifactType) {
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

   public Artifact getArtifactByName(ArtifactTypeToken artifactType, String name) {
      return toArtifact(queryService.getArtifactByName(artifactType, name));
   }

   public Artifact getArtifact(ArtifactId artifact, BranchId branch, DeletionFlag deletionFlag) {
      return toArtifact(queryService.getArtifact(artifact, branch, deletionFlag));
   }

   public Artifact getHistoricalArtifactOrNull(ArtifactId artifact, TransactionToken transaction,
      DeletionFlag deletionFlag) {
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
         if (obj instanceof IAtsObject) {
            result.setArtifactType(((IAtsObject) obj).getArtifactType());
         }
      } else if (obj instanceof IAtsObject) {
         result = getArtifact(((IAtsObject) obj).getStoreObject(), atsApi.getAtsBranch());
      } else if (obj instanceof ArtifactId) {
         result = getArtifact((ArtifactId) obj, atsApi.getAtsBranch());
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

   public TeamWorkFlowArtifact getTeamWfArt(IAtsTeamWorkflow teamWf) {
      TeamWorkFlowArtifact result = null;
      if (teamWf instanceof TeamWorkFlowArtifact) {
         result = (TeamWorkFlowArtifact) teamWf;
      }
      return result;
   }

   public Artifact getParentAction(ArtifactId artifact) {
      Artifact actionArt = null;
      if (artifact instanceof IAtsAction) {
         actionArt = (Artifact) (IAtsAction) artifact;
      } else if (artifact instanceof AbstractWorkflowArtifact) {
         actionArt = (Artifact) ((AbstractWorkflowArtifact) artifact).getParentAction().getStoreObject();
      }
      return actionArt;
   }

}

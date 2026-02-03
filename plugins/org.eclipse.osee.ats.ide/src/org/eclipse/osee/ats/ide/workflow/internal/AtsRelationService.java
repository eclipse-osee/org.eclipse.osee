/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.workflow.internal;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workdef.IAtsRelationService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * New relation resolver for IDE client. Calls will query server for answers, and for now load artifacts on client.
 *
 * @author Donald G. Dunne
 */
public class AtsRelationService implements IAtsRelationService<Artifact> {

   private final AtsApi atsApi;

   public AtsRelationService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public Collection<Artifact> getRelated(ArtifactId art, RelationTypeSide relation) {
      Collection<ArtifactToken> toks = getRelatedTokens(art, relation);
      return Collections.castAll(atsApi.getQueryService().getArtifacts(toks, atsApi.getAtsBranch()));
   }

   @Override
   public Collection<ArtifactToken> getRelatedTokens(ArtifactId art, RelationTypeSide relation) {
      Collection<ArtifactToken> toks =
         atsApi.getServerEndpoints().getRelationEp().getRelatedTokens(ArtifactId.valueOf(art.getId()), relation);
      return toks;
   }

}

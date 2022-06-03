/*********************************************************************
* Copyright (c) 2021 Boeing
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://no-click.mil/?https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Boeing - initial API and implementation
**********************************************************************/

package org.eclipse.osee.orcs.rest.internal;

import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Hugo Trejo, Torin Grenda, David Miller
 */
public class RelationEndpointImpl implements RelationEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;

   public RelationEndpointImpl(OrcsApi orcsApi, BranchId branch) {
      this.orcsApi = orcsApi;
      this.branch = branch;
   }

   @Override
   public Response createRelationByType(ArtifactId sideA, ArtifactId sideB, RelationTypeToken relationType) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch,
         String.format("RelationEndpoint REST api creating relation %s between %s and %s", relationType.getName(),
            sideA.getIdString(), sideB.getIdString()));
      tx.relate(sideA, relationType, sideB);
      tx.commit();

      return Response.ok().build();
   }

   @Override
   public List<ArtifactToken> getRelatedHierarchy(ArtifactId artifact, ArtifactId view) {
      List<ArtifactToken> ids = getRelated(artifact, CoreRelationTypes.DefaultHierarchical, RelationSide.SIDE_A, view);
      return ids;
   }

   private List<ArtifactToken> getRelated(ArtifactId artifact, RelationTypeToken relationType, RelationSide side, ArtifactId view) {
      RelationTypeSide rts = new RelationTypeSide(relationType, side);
      return orcsApi.getQueryFactory().fromBranch(branch, view).andRelatedTo(rts, artifact).asArtifactTokens();
   }

   @Override
   public List<ArtifactToken> getRelatedRecursive(ArtifactId artifact, RelationTypeToken relationType, ArtifactId view) {
      RelationTypeSide sideB = new RelationTypeSide(relationType, RelationSide.SIDE_B);
      return orcsApi.getQueryFactory().fromBranch(branch, view).andRelatedRecursive(sideB, artifact).asArtifactTokens();
   }
}

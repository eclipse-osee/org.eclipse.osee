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

package org.eclipse.osee.ats.rest.internal.store;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.store.AtsRelationEndpointApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsRelationEndpointImpl implements AtsRelationEndpointApi {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsRelationEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   public Collection<ArtifactToken> getRelatedTokens(ArtifactId artId, RelationTypeSide relation) {
      List<ArtifactReadable> arts =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(artId).asArtifact().getRelated(
            relation).getList();
      return Collections.castAll(arts);
   }

}

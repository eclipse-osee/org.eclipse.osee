/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.core.agile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileProgramFeature;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class AgileProgramFeature extends AtsConfigObject implements IAgileProgramFeature {

   public AgileProgramFeature(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact, AtsArtifactTypes.AgileProgramFeature);
   }

   @Override
   public List<Long> getStoryIds() {
      List<Long> ids = new ArrayList<>();
      for (Iterator<ArtifactToken> iterator =
         atsApi.getRelationResolver().getChildren(artifact).iterator(); iterator.hasNext();) {
         ArtifactToken child = iterator.next();
         if (child.isOfType(AtsArtifactTypes.AgileStory)) {
            ids.add(child.getId());
         }
      }
      return ids;
   }

   @Override
   public Long getProgramId() {
      return atsApi.getRelationResolver().getParent(artifact).getId();
   }

   public static IAgileProgramFeature construct(ArtifactId progFeatureArt, AtsApi atsApi) {
      return new AgileProgramFeature(atsApi.getLogger(), atsApi, atsApi.getQueryService().getArtifact(progFeatureArt));
   }

}

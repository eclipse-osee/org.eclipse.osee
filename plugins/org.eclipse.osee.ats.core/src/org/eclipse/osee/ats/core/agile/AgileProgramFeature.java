/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
         ArtifactId child = iterator.next();
         if (atsApi.getStoreService().isOfType(child, AtsArtifactTypes.AgileStory)) {
            ids.add(new Long(child.getId()));
         }
      }
      return ids;
   }

   @Override
   public Long getProgramId() {
      return atsApi.getRelationResolver().getParent(artifact).getId();
   }
}
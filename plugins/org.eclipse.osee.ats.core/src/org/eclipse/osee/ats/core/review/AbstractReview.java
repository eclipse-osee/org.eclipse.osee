/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.review;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractReview extends WorkItem implements IAtsAbstractReview {

   public AbstractReview(Log logger, AtsApi atsApi, ArtifactToken artifact, IArtifactType artifactType) {
      super(logger, atsApi, artifact, artifactType);
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems() {
      Set<IAtsActionableItem> ais = new HashSet<>();
      Collection<ArtifactId> artifactIds =
         atsApi.getAttributeResolver().getAttributeValues(artifact, AtsAttributeTypes.ActionableItemReference);
      for (ArtifactId aiId : artifactIds) {
         ArtifactId aiArt = atsApi.getConfigItem(aiId);
         IAtsActionableItem ai = atsApi.getConfigItemFactory().getActionableItem(aiArt);
         ais.add(ai);
      }
      return ais;
   }

   @Override
   public String getRelatedToState() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.RelatedToState, "");
   }
}

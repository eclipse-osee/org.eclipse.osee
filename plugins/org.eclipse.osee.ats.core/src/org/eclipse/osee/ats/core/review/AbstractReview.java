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
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractReview extends WorkItem implements IAtsAbstractReview {

   public AbstractReview(Log logger, IAtsServices services, ArtifactToken artifact) {
      super(logger, services, artifact);
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems()  {
      Set<IAtsActionableItem> ais = new HashSet<>();
      Collection<ArtifactId> artifactIds =
         services.getAttributeResolver().getAttributeValues(artifact, AtsAttributeTypes.ActionableItemReference);
      for (ArtifactId aiId : artifactIds) {
         ArtifactId aiArt = services.getConfigItem(aiId);
         IAtsActionableItem ai = services.getConfigItemFactory().getActionableItem(aiArt);
         ais.add(ai);
      }
      return ais;
   }

   @Override
   public String getRelatedToState() {
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.RelatedToState, "");
   }
}

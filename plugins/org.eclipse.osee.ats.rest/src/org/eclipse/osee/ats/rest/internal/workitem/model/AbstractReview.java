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
package org.eclipse.osee.ats.rest.internal.workitem.model;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractReview extends WorkItem implements IAtsAbstractReview {

   public AbstractReview(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems() throws OseeCoreException {
      Set<IAtsActionableItem> ais = new HashSet<>();
      for (Object aiGuidObj : ((ArtifactReadable) artifact).getAttributeValues(AtsAttributeTypes.ActionableItem)) {
         String aiGuid = (String) aiGuidObj;
         ArtifactId aiArt = services.getArtifactByGuid(aiGuid);
         IAtsActionableItem ai = services.getConfigItemFactory().getActionableItem(aiArt);
         ais.add(ai);
      }
      return ais;
   }

   @Override
   public String getRelatedToState() {
      return ((ArtifactReadable) artifact).getSoleAttributeValue(AtsAttributeTypes.RelatedToState, "");
   }
}

/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface INewActionListener {

   /**
    * Called after Action and team workflows are created and before persist of Action
    */
   public default void actionCreated(IAtsAction action) throws OseeCoreException {
      // for override
   }

   /**
    * Called after team workflow and initialized and before persist of Action
    */
   public default void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) throws OseeCoreException {
      // for override
   }

   /**
    * @return workflow id to use instead of default configured id
    */
   public default String getOverrideWorkDefinitionId(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      // for override
      return null;
   }

   /**
    * @return the artifact token to use for team workflow for applicableAis
    */
   public default ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
      // for override
      return null;
   }
}

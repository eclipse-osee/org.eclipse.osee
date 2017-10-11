/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.agile;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class AgileBacklog extends WorkItem implements IAgileBacklog {

   public AgileBacklog(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact);
   }

   @Override
   public boolean isActive() {
      return getStateMgr().getStateType().isInWork();
   }

   @Override
   public long getTeamUuid() {
      long result = 0;
      try {
         ArtifactId agileTeam =
            atsApi.getRelationResolver().getRelatedOrNull(artifact, AtsRelationTypes.AgileTeamToBacklog_AgileTeam);
         if (agileTeam != null) {
            result = agileTeam.getId();
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

}

/*********************************************************************
 * Copyright (c) 2015 Boeing
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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.workflow.WorkItem;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class AgileItem extends WorkItem implements IAgileItem {

   public AgileItem(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact, AtsArtifactTypes.AbstractWorkflowArtifact);
   }

   @Override
   public long getTeamId() {
      return 0;
   }
}
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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileProgramBacklogItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class AgileProgramBacklogItem extends AtsConfigObject implements IAgileProgramBacklogItem {

   public AgileProgramBacklogItem(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact, AtsArtifactTypes.AgileProgramBacklogItem);
   }

   @Override
   public Long getBacklogId() {
      return null;
   }

   public static IAgileProgramBacklogItem construct(ArtifactId artifact, AtsApi atsApi) {
      return new AgileProgramBacklogItem(atsApi.getLogger(), atsApi, atsApi.getQueryService().getArtifact(artifact));
   }

}

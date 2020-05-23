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

package org.eclipse.osee.ats.core.insertion;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author David W. Miller
 */
public class Insertion extends AtsConfigObject implements IAtsInsertion {

   private long programId;

   public Insertion(Log logger, AtsApi atsServices, ArtifactToken artifact) {
      super(logger, atsServices, artifact, AtsArtifactTypes.Insertion);
   }

   @Override
   public long getProgramId() {
      return programId;
   }

   public void setProgramId(long programId) {
      this.programId = programId;
   }
}
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
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author David W. Miller
 */
public class InsertionActivity extends AtsConfigObject implements IAtsInsertionActivity {

   private long insertionId;

   public InsertionActivity(Log logger, AtsApi atsApiServer, ArtifactToken artifact) {
      super(logger, atsApiServer, artifact, AtsArtifactTypes.InsertionActivity);
   }

   @Override
   public long getInsertionId() {
      return insertionId;
   }

   public void setInsertionId(long insertionId) {
      this.insertionId = insertionId;
   }
}

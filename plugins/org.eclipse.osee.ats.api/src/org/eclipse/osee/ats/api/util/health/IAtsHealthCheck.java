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

package org.eclipse.osee.ats.api.util.health;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsHealthCheck {

   /**
    * @param changes if not null, then fix if possible
    */
   public default void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
      // do nothing
   }

   /**
    * Implement to make single check on whole database. These are good for queries on things other that Work Items and
    * hard coded SQL queries.
    */
   default public void check(HealthCheckResults results, AtsApi atsApi) {
      // do nothing
   }

   public default String getName() {
      return getClass().getSimpleName();
   }

   public default void error(HealthCheckResults results, IAtsWorkItem workItem, String format, Object... data) {
      results.log(workItem.getStoreObject(), getClass().getSimpleName(),
         String.format("Error: " + format + " for " + workItem.getAtsId(), data));
   }

}

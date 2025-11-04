/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem.operations;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchDataResults;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;

/**
 * @author Donald G. Dunne
 */
public class ActionQueryOperations {

   private final AtsSearchData data;
   private final AtsApi atsApi;

   public ActionQueryOperations(AtsSearchData data, AtsApi atsApi) {
      this.data = data;
      this.atsApi = atsApi;
   }

   public XResultData getIds() {
      ElapsedTime time = new ElapsedTime(getClass().getSimpleName() + " - search");
      AtsSearchDataResults results = atsApi.getQueryService().getArtifactsNew(data, null);
      System.err.println("Results: " + results.getArtifacts().size());
      time.endSec();
      XResultData rd = new XResultData();
      for (ArtifactToken art : results.getArtifacts()) {
         rd.getIds().add(art.getIdString());
      }
      return rd;
   }

}

/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractDerivedFromColumn extends AbstractServicesColumn {

   public AbstractDerivedFromColumn(AtsApi atsApi) {
      super(atsApi);
   }

   public ArtifactToken getDerivedFrom(Object obj) {
      return getDerivedFrom(obj, AtsApiService.get());
   }

   /**
    * @return derived from artifact or sentinel
    */
   public static ArtifactToken getDerivedFrom(Object obj, AtsApi atsApi) {
      ArtifactToken derivedFrom = ArtifactToken.SENTINEL;
      if (obj instanceof IAtsWorkItem) {
         derivedFrom =
            atsApi.getRelationResolver().getRelatedOrSentinel((IAtsWorkItem) obj, AtsRelationTypes.Derive_From);
      }
      return derivedFrom;
   }
}

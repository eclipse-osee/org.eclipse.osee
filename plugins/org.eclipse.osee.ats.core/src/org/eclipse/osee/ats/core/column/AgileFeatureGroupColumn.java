/*********************************************************************
 * Copyright (c) 2016 Boeing
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
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;

/**
 * @author Donald G. Dunne
 */
public class AgileFeatureGroupColumn extends RelationColumn {

   public AgileFeatureGroupColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.AgileFeatureGroupColumn, AtsRelationTypes.AgileFeatureToItem_AgileFeatureGroup,
         atsApi);
   }

}

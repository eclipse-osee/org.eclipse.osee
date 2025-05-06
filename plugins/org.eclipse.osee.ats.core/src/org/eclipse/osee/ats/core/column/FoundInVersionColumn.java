/*********************************************************************
 * Copyright (c) 2019 Boeing
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
 * @author Jeremy A. Midvidy
 */
public class FoundInVersionColumn extends RelationColumn {

   public FoundInVersionColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.FoundInVersionColumn, AtsRelationTypes.TeamWorkflowToFoundInVersion_Version, atsApi);
   }

}

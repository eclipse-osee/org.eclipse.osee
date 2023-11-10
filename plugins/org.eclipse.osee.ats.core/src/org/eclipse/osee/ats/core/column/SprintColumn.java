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

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class SprintColumn extends AtsCoreCodeColumn {

   public SprintColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.SprintColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      return getTextValue(atsObject, atsApi);
   }

   public static String getTextValue(IAtsObject atsObject, AtsApi atsApi) {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         Collection<ArtifactToken> sprints =
            atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.AgileSprintToItem_AgileSprint);
         return Collections.toString("; ", sprints);
      }
      return result;
   }

}

/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class RelationColumn extends AbstractServicesColumn {

   private final RelationTypeSide relationTypeSide;

   public RelationColumn(RelationTypeSide relationTypeSide, AtsApi atsApi) {
      super(atsApi);
      this.relationTypeSide = relationTypeSide;
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         Set<String> related = new HashSet<>();
         for (ArtifactToken featureGroup : atsApi.getRelationResolver().getRelated(workItem, relationTypeSide)) {
            related.add(featureGroup.getName());
         }
         result = Collections.toString(", ", related);
      }
      return result;
   }

}

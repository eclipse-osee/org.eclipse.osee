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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class DerivedFromAtsIdColumn extends AbstractDerivedFromColumn {

   public DerivedFromAtsIdColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   String getText(IAtsObject atsObject) throws Exception {
      return getDerivedFromAtsId(atsObject, atsApi);
   }

   public static String getDerivedFromAtsId(Object element, AtsApi atsApi) {
      ArtifactToken derivedFrom = getDerivedFrom(element, atsApi);
      if (derivedFrom.isValid()) {
         return atsApi.getAttributeResolver().getSoleAttributeValueAsString(derivedFrom, AtsAttributeTypes.AtsId, "");
      }
      return "";
   }

}

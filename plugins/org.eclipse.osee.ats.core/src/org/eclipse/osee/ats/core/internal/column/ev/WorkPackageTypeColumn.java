/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.internal.column.ev;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageType;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageTypeColumn extends AbstractRelatedWorkPackageColumn {

   public WorkPackageTypeColumn(IAtsEarnedValueServiceProvider earnedValueServiceProvider, AtsApi atsApi) {
      super(earnedValueServiceProvider, atsApi);
   }

   @Override
   protected String getColumnValue(IAtsWorkPackage workPkg) {
      try {
         if (workPkg.getWorkPackageType() == null) {
            return null;
         }
         return workPkg.getWorkPackageType().name();
      } catch (OseeCoreException ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
   }

   @Override
   protected String getColumnValue(ArtifactToken wpArt) {
      if (atsApi == null) {
         return "";
      }
      String value = atsApi.getAttributeResolver().getSoleAttributeValue(wpArt, AtsAttributeTypes.WorkPackageType, "");
      AtsWorkPackageType type = AtsWorkPackageType.None;
      if (Strings.isValid(value)) {
         try {
            type = AtsWorkPackageType.valueOf(value);
            return type.name();
         } catch (Exception ex) {
            // do nothing
         }
      }
      return type.name();
   }

}

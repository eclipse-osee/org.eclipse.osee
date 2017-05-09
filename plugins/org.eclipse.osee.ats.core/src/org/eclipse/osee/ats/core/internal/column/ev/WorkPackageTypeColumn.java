/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.column.ev;

import org.eclipse.osee.ats.api.IAtsServices;
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

   public WorkPackageTypeColumn(IAtsEarnedValueServiceProvider earnedValueServiceProvider, IAtsServices services) {
      super(earnedValueServiceProvider, services);
   }

   @Override
   protected String getColumnValue(IAtsWorkPackage workPkg) {
      try {
         return workPkg.getWorkPackageType().name();
      } catch (OseeCoreException ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
   }

   @Override
   protected String getColumnValue(ArtifactToken wpArt) {
      if (services == null) {
         return "";
      }
      String value =
         services.getAttributeResolver().getSoleAttributeValue(wpArt, AtsAttributeTypes.WorkPackageType, "");
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

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

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Provides for display of ActivityId and ActivityName from related Work Package
 * 
 * @author Donald G. Dunne
 */
public abstract class AbstractRelatedWorkPackageColumn implements IAtsColumn {

   private final IAtsEarnedValueServiceProvider earnedValueServiceProvider;

   public AbstractRelatedWorkPackageColumn(IAtsEarnedValueServiceProvider earnedValueServiceProvider) {
      this.earnedValueServiceProvider = earnedValueServiceProvider;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      try {
         if (atsObject instanceof IAtsWorkItem) {
            IAtsWorkPackage workPkg =
               earnedValueServiceProvider.getEarnedValueService().getWorkPackage((IAtsWorkItem) atsObject);
            if (workPkg != null) {
               result = getColumnValue(workPkg);
            }
         }
      } catch (OseeCoreException ex) {
         result = AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }

   protected abstract String getColumnValue(IAtsWorkPackage workPkg) throws OseeCoreException;

}

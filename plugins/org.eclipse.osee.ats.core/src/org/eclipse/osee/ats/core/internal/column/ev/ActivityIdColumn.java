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
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.core.column.IActivityIdUtility;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Provides for display of ActivityId and ActivityName from related Work Package
 *
 * @author Donald G. Dunne
 */
public class ActivityIdColumn implements IActivityIdUtility {

   private final IAtsEarnedValueServiceProvider earnedValueServiceProvider;

   public ActivityIdColumn(IAtsEarnedValueServiceProvider earnedValueServiceProvider) {
      this.earnedValueServiceProvider = earnedValueServiceProvider;
   }

   @Override
   public String getWorkPackageId(Object object) {
      return earnedValueServiceProvider.getEarnedValueService().getWorkPackageId((IAtsWorkItem) object);
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      try {
         IAtsWorkPackage workPackage = null;
         if (atsObject instanceof IAtsWorkItem) {
            workPackage = earnedValueServiceProvider.getEarnedValueService().getWorkPackage((IAtsWorkItem) atsObject);
            result = getText(workPackage);
         }
      } catch (OseeCoreException ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }

   private String getText(IAtsWorkPackage workPackage) {
      String result;
      result = String.format("%s - %s", workPackage.getActivityId(), workPackage.getActivityName());
      return result;
   }

   @Override
   public String getColumnText(IAtsWorkPackage workPackage) {
      return getText(workPackage);
   }

   @Override
   public IAtsWorkPackage getWorkPackage(IAtsObject atsObject) throws OseeCoreException {
      IAtsWorkPackage workPackage = null;
      // Children work items inherit the work packages of their parent team workflow
      if (atsObject instanceof IAtsWorkItem) {
         workPackage = earnedValueServiceProvider.getEarnedValueService().getWorkPackage((IAtsWorkItem) atsObject);
      }
      return workPackage;
   }

   @Override
   public String getDescription() {
      return "Provides Activity Id and Name from the selected Work Package related to the selected workflow.";
   }

}

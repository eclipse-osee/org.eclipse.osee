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

import java.util.Map;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.eclipse.osee.ats.api.column.IWorkPackageColumn;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Provides for display of ActivityId and ActivityName from related Work Package
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractRelatedWorkPackageColumn implements IAtsColumn, IWorkPackageColumn {

   private final IAtsEarnedValueServiceProvider earnedValueServiceProvider;
   private Map<String, ArtifactToken> guidToWorkPackage;
   protected final IAtsServices services;

   public AbstractRelatedWorkPackageColumn(IAtsEarnedValueServiceProvider earnedValueServiceProvider, IAtsServices services) {
      this.earnedValueServiceProvider = earnedValueServiceProvider;
      this.services = services;
   }

   /**
    * Set optional map to use as a cache of work item id (Long) or work package guid (String) to Insertion artifact.
    */
   @Override
   public void setIdToWorkPackageCache(Map<String, ArtifactToken> guidToWorkPackage) {
      this.guidToWorkPackage = guidToWorkPackage;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      try {
         if (atsObject instanceof IAtsWorkItem) {
            String workPackageId =
               earnedValueServiceProvider.getEarnedValueService().getWorkPackageId((IAtsWorkItem) atsObject);
            if (workPackageId != null) {
               if (guidToWorkPackage != null) {
                  if (Strings.isValid(workPackageId)) {
                     ArtifactToken wpArt = guidToWorkPackage.get(workPackageId);
                     result = getColumnValue(wpArt);
                     if (Strings.isInValid(result)) {
                        IAtsWorkPackage workPkg =
                           earnedValueServiceProvider.getEarnedValueService().getWorkPackage(wpArt);
                        if (workPkg != null) {
                           result = getColumnValue(workPkg);
                        }
                     }
                  }
               }
               if (Strings.isInValid(result)) {
                  IAtsWorkPackage workPkg =
                     earnedValueServiceProvider.getEarnedValueService().getWorkPackage((IAtsWorkItem) atsObject);
                  if (workPkg != null) {
                     result = getColumnValue(workPkg);
                     if (guidToWorkPackage != null) {
                        guidToWorkPackage.put(workPackageId, workPkg.getStoreObject());
                     }
                  }
               }
            }
         }
      } catch (OseeCoreException ex) {
         result = AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }

   protected abstract String getColumnValue(ArtifactToken wpArt);

   protected abstract String getColumnValue(IAtsWorkPackage workPkg);

}

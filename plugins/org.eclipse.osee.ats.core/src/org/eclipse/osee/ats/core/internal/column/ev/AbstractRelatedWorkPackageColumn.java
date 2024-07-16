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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumn;
import org.eclipse.osee.ats.api.column.IWorkPackageColumn;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Provides for display of ActivityId and ActivityName from related Work Package
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractRelatedWorkPackageColumn implements AtsColumn, IWorkPackageColumn {

   private final IAtsEarnedValueServiceProvider earnedValueServiceProvider;
   private Map<ArtifactId, ArtifactToken> idToWorkPackage = new HashMap<>();
   protected final AtsApi atsApi;

   public AbstractRelatedWorkPackageColumn(IAtsEarnedValueServiceProvider earnedValueServiceProvider, AtsApi atsApi) {
      this.earnedValueServiceProvider = earnedValueServiceProvider;
      this.atsApi = atsApi;
   }

   /**
    * Set optional map to use as a cache of work item id (Long) or work package id (String) to Insertion artifact.
    */
   @Override
   public void setIdToWorkPackageCache(Map<ArtifactId, ArtifactToken> idToWorkPackage) {
      this.idToWorkPackage = idToWorkPackage;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      try {
         if (atsObject instanceof IAtsWorkItem) {
            ArtifactId workPackageId =
               earnedValueServiceProvider.getEarnedValueService().getWorkPackageId((IAtsWorkItem) atsObject);
            if (workPackageId.isValid()) {
               if (idToWorkPackage != null) {
                  ArtifactToken wpArt = idToWorkPackage.get(workPackageId);
                  if (wpArt != null) {
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
                     if (idToWorkPackage != null) {
                        idToWorkPackage.put(workPackageId, workPkg.getStoreObject());
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

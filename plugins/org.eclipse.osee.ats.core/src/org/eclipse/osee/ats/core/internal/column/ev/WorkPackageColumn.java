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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.IWorkPackageColumn;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.core.column.IWorkPackageUtility;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Provides for display of Id and Name from related Work Package
 *
 * @author Donald G. Dunne
 */
public class WorkPackageColumn implements IWorkPackageUtility, IWorkPackageColumn {

   private final IAtsEarnedValueServiceProvider earnedValueServiceProvider;
   private Map<ArtifactId, ArtifactToken> guidToWorkPackage;

   public WorkPackageColumn(IAtsEarnedValueServiceProvider earnedValueServiceProvider) {
      this.earnedValueServiceProvider = earnedValueServiceProvider;
   }

   @Override
   public ArtifactId getWorkPackageId(Object object) {
      return earnedValueServiceProvider.getEarnedValueService().getWorkPackageId((IAtsWorkItem) object);
   }

   /**
    * Set optional map to use as a cache of work item id (Long) or work package guid (String) to Insertion artifact.
    */
   @Override
   public void setIdToWorkPackageCache(Map<ArtifactId, ArtifactToken> guidToWorkPackage) {
      this.guidToWorkPackage = guidToWorkPackage;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      try {
         IAtsWorkPackage workPackage = null;
         if (atsObject instanceof IAtsWorkItem) {
            ArtifactId workPackageId =
               earnedValueServiceProvider.getEarnedValueService().getWorkPackageId((IAtsWorkItem) atsObject);
            if (guidToWorkPackage != null) {
               if (workPackageId.isValid()) {
                  ArtifactToken wpArt = guidToWorkPackage.get(workPackageId);
                  workPackage = earnedValueServiceProvider.getEarnedValueService().getWorkPackage(wpArt);
               }
            }
            if (workPackage == null) {
               workPackage =
                  earnedValueServiceProvider.getEarnedValueService().getWorkPackage((IAtsWorkItem) atsObject);
               if (workPackage != null && guidToWorkPackage != null) {
                  guidToWorkPackage.put(workPackageId, workPackage.getStoreObject());
               }
            }
            if (workPackage != null) {
               result = getText(workPackage);
            }
         }
      } catch (OseeCoreException ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }

   private String getText(IAtsWorkPackage workPackage) {
      String result = "";
      if (workPackage != null) {
         String activityId = Strings.isValid(workPackage.getActivityId()) ? workPackage.getActivityId() : "";
         String activityName = Strings.isValid(workPackage.getActivityName()) ? workPackage.getActivityName() : "";
         result = String.format("%s - %s", activityId, activityName);
      }
      return result;
   }

   @Override
   public String getColumnText(IAtsWorkPackage workPackage) {
      return getText(workPackage);
   }

   @Override
   public IAtsWorkPackage getWorkPackage(IAtsObject atsObject) throws OseeCoreException {
      IAtsWorkPackage workPackage = null;
      ArtifactId workPackageId =
         earnedValueServiceProvider.getEarnedValueService().getWorkPackageId((IAtsWorkItem) atsObject);
      if (guidToWorkPackage != null) {
         if (workPackageId.isValid()) {
            ArtifactToken wpArt = guidToWorkPackage.get(workPackageId);
            workPackage = earnedValueServiceProvider.getEarnedValueService().getWorkPackage(wpArt);
            if (workPackage != null) {
               return workPackage;
            }
         }
      }
      // Children work items inherit the work packages of their parent team workflow
      if (atsObject instanceof IAtsWorkItem) {
         workPackage = earnedValueServiceProvider.getEarnedValueService().getWorkPackage((IAtsWorkItem) atsObject);
      }
      return workPackage;
   }
}

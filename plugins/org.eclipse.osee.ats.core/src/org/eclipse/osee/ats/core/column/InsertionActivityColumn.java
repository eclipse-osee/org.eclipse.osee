/*********************************************************************
 * Copyright (c) 2015 Boeing
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

import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.ats.core.config.WorkPackageUtility;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class InsertionActivityColumn extends AtsCoreCodeColumn {

   private Map<Object, ArtifactToken> idToInsertionActivity;

   public InsertionActivityColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.InsertionActivityColumn, atsApi);
   }

   /**
    * Set optional map to use as a cache of work item id (Long) or work package id (String) to Insertion Activity
    * artifact.
    */
   public void setIdToInsertionActivityCache(Map<Object, ArtifactToken> idToInsertionActivity) {
      this.idToInsertionActivity = idToInsertionActivity;
   }

   @Override
   public String getText(IAtsObject atsObject) {
      String format = "%s";
      if (atsApi.getStoreService().isDeleted(atsObject)) {
         format = "<Deleted> %s";
      }
      return String.format(format,
         getInsertionActivityStr(atsObject, atsApi, CountryColumn.getUtil(), idToInsertionActivity));
   }

   public static String getInsertionActivityStr(IAtsObject atsObject, AtsApi atsApi) {
      return getInsertionActivityStr(atsObject, atsApi, CountryColumn.getUtil());
   }

   public static String getInsertionActivityStr(IAtsObject atsObject, AtsApi atsApi, WorkPackageUtility util) {
      return getInsertionActivityStr(atsObject, atsApi, util, null);
   }

   public static String getInsertionActivityStr(IAtsObject atsObject, AtsApi atsApi, WorkPackageUtility utilMap,
      Map<Object, ArtifactToken> idToInsertionActivity) {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         if (idToInsertionActivity != null) {
            ArtifactToken insertionArt = idToInsertionActivity.get(atsObject.getId());
            if (insertionArt != null) {
               result = insertionArt.getName();
            }
         }
         if (Strings.isInValid(result)) {
            IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
            Pair<IAtsInsertionActivity, Boolean> insertionActivity = utilMap.getInsertionActivity(atsApi, workItem);
            if (insertionActivity.getFirst() != null) {
               result = String.format("%s%s", insertionActivity.getFirst().getName(),
                  insertionActivity.getSecond() ? " (I)" : "");
            }
         }
      } else if (atsObject instanceof IAtsWorkPackage) {
         if (idToInsertionActivity != null) {
            ArtifactToken insertionActivityArt = idToInsertionActivity.get(((IAtsWorkPackage) atsObject).getId());
            if (insertionActivityArt != null) {
               result = insertionActivityArt.getName();
            }
         }
         if (Strings.isInValid(result)) {
            IAtsWorkPackage workPackage = (IAtsWorkPackage) atsObject;
            IAtsInsertionActivity insertionActivity = atsApi.getProgramService().getInsertionActivity(workPackage);
            if (insertionActivity != null) {
               result = insertionActivity.getName();
            }
         }
      }
      return result;
   }

}

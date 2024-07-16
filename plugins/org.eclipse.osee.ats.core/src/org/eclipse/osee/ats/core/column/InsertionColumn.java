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
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.ats.core.config.WorkPackageUtility;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class InsertionColumn extends AtsCoreCodeColumn {

   private Map<Object, ArtifactToken> idToInsertion;

   public InsertionColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.InsertionColumn, atsApi);
   }

   /**
    * Set optional map to use as a cache of work item id (Long) or work package id (String) to Insertion artifact.
    */
   public void setIdToInsertionCache(Map<Object, ArtifactToken> idToInsertion) {
      this.idToInsertion = idToInsertion;
   }

   @Override
   public String getText(IAtsObject atsObject) {
      String format = "%s";
      if (atsApi.getStoreService().isDeleted(atsObject)) {
         format = "<Deleted> %s";
      }
      return String.format(format, getInsertionStr(atsObject, atsApi, CountryColumn.getUtil(), idToInsertion));
   }

   public static String getInsertionStr(IAtsObject atsObject, AtsApi atsApi) {
      return getInsertionStr(atsObject, atsApi, CountryColumn.getUtil());
   }

   public static String getInsertionStr(IAtsObject atsObject, AtsApi atsApi, WorkPackageUtility util) {
      return getInsertionStr(atsObject, atsApi, util, null);
   }

   public static String getInsertionStr(IAtsObject atsObject, AtsApi atsApi, WorkPackageUtility util,
      Map<Object, ArtifactToken> idToInsertion) {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         if (idToInsertion != null) {
            ArtifactToken insertionArt = idToInsertion.get(atsObject.getId());
            if (insertionArt != null) {
               result = insertionArt.getName();
            }
         }
         if (Strings.isInValid(result)) {
            IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
            Pair<IAtsInsertion, Boolean> insertion = util.getInsertion(atsApi, workItem);
            if (insertion.getFirst() != null) {
               result = String.format("%s%s", insertion.getFirst().getName(), insertion.getSecond() ? " (I)" : "");
            }
         }
      } else if (atsObject instanceof IAtsWorkPackage) {
         if (idToInsertion != null) {
            ArtifactToken insertionArt = idToInsertion.get(((IAtsWorkPackage) atsObject).getId());
            if (insertionArt != null) {
               result = insertionArt.getName();
            }
         }
         if (Strings.isInValid(result)) {
            IAtsWorkPackage workPackage = (IAtsWorkPackage) atsObject;
            IAtsInsertionActivity insertionActivity = atsApi.getProgramService().getInsertionActivity(workPackage);
            if (insertionActivity != null) {
               IAtsInsertion insertion = atsApi.getProgramService().getInsertion(insertionActivity);
               if (insertion != null) {
                  result = insertion.getName();
               }
            }
         }
      }
      return result;
   }

}

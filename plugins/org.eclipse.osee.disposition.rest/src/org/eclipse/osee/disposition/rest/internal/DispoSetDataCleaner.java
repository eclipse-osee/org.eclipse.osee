/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Angel Avila
 */
public class DispoSetDataCleaner {

   public static List<DispoItem> cleanUpPcrTypes(List<DispoItem> items) {
      List<DispoItem> toModify = new ArrayList<DispoItem>();
      for (DispoItem item : items) {
         JSONArray annotationsList = item.getAnnotationsList();
         if (checkForBadReqType(annotationsList)) {
            try {
               JSONArray modifiedAnnotations = cleanAnnotationsList(annotationsList);
               DispoItemData modifiedItem = new DispoItemData();
               modifiedItem.setAnnotationsList(modifiedAnnotations);
               modifiedItem.setGuid(item.getGuid());

               toModify.add(modifiedItem);
            } catch (JSONException ex) {
               throw new OseeCoreException(ex);
            }
         } else {
            // do nothing
         }
      }
      return toModify;
   }

   private static boolean checkForBadReqType(JSONArray annotations) {
      if (containsBadWord(annotations.toString())) {
         return true;
      } else {
         return false;
      }
   }

   private static JSONArray cleanAnnotationsList(JSONArray annotations) throws JSONException {
      for (int i = 0; i < annotations.length(); i++) {
         DispoAnnotationData annotation = DispoUtil.jsonObjToDispoAnnotationData(annotations.getJSONObject(i));
         if (containsBadWord(annotation.getResolutionType())) {
            annotation.setResolutionType("Requirement");
         }
         annotations.put(annotation.getIndex(), DispoUtil.annotationToJsonObj(annotation));
      }
      return annotations;
   }

   private static boolean containsBadWord(String toCheck) {
      String allUpperCase = toCheck.toUpperCase();
      if (allUpperCase.contains("\"REQ\"") || allUpperCase.equals("REQ")) {
         return true;
      } else {
         return false;
      }
   }
}

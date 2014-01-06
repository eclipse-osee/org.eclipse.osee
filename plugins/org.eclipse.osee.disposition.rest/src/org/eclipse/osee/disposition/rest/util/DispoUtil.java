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
package org.eclipse.osee.disposition.rest.util;

import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetData.DispositionOperationsEnum;
import org.eclipse.osee.disposition.model.LocationRange;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public final class DispoUtil {

   private DispoUtil() {
      //
   }

   public static JSONObject asJSONOjbect(String value) {
      try {
         return new JSONObject(value);
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static JSONArray asJSONArray(String value) {
      try {
         return new JSONArray(value);
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static JSONArray mergeNotesList(JSONArray originalNotesList, JSONArray modifiedNotesList) {
      return originalNotesList.put(modifiedNotesList);
   }

   public static String getJsonArrayString(JSONArray jsonArray) throws JSONException {
      StringBuilder sb = new StringBuilder();

      int arraySize = jsonArray.length();
      for (int i = 0; i < arraySize; i++) {
         Object obj = jsonArray.get(i);
         JSONObject annotationJSsonObject = new JSONObject(obj);
         sb.append(annotationJSsonObject.toString());
         sb.append(",");
      }

      sb.deleteCharAt(sb.length() - 1);
      return sb.toString();
   }

   public static DispoSetData setArtToSetData(DispoSet dispoSet) {
      DispoSetData dispoSetData = new DispoSetData();
      if (dispoSet != null) {
         dispoSetData.setName(dispoSet.getName());
         dispoSetData.setImportPath(dispoSet.getImportPath());
         dispoSetData.setNotesList(dispoSet.getNotesList());
         dispoSetData.setGuid(dispoSet.getGuid());
         dispoSetData.setStatusCount(dispoSet.getStatusCount());
      } else {
         dispoSetData = null;
      }
      return dispoSetData;
   }

   public static DispoItemData itemArtToItemData(DispoItem dispoItemArt) {
      DispoItemData dispoItemData = new DispoItemData();
      dispoItemData.setName(dispoItemArt.getName());
      dispoItemData.setGuid(dispoItemArt.getGuid());
      dispoItemData.setCreationDate(dispoItemArt.getCreationDate());
      dispoItemData.setLastUpdate(dispoItemArt.getLastUpdate());
      dispoItemData.setStatus(dispoItemArt.getStatus());
      dispoItemData.setAnnotationsList(dispoItemArt.getAnnotationsList());
      dispoItemData.setDiscrepanciesList(dispoItemArt.getDiscrepanciesList());
      return dispoItemData;
   }

   public static DispoItemData jsonObjToDispoItem(JSONObject jsonObject) {
      DispoItemData dispoItem = new DispoItemData();
      try {
         if (jsonObject.has("name")) {
            dispoItem.setName(jsonObject.getString("name"));
         }
         if (jsonObject.has("guid")) {
            dispoItem.setGuid(jsonObject.getString("guid"));
         }
         if (jsonObject.has("itemStatus")) {
            dispoItem.setStatus(jsonObject.getString("itemStatus"));
         }
         if (jsonObject.has("discrepanciesList")) {
            dispoItem.setDiscrepanciesList(jsonObject.getJSONArray("discrepanciesList"));
         }
         if (jsonObject.has("annotationsList")) {
            dispoItem.setAnnotationsList(jsonObject.getJSONObject("annotationsList"));
         }
      } catch (JSONException ex) {
         throw new OseeCoreException("Error deserializing a Dispositionable Item.", ex);
      }

      return dispoItem;
   }

   public static DispoSetData jsonObjToDispoSet(JSONObject jsonObject) {
      DispoSetData dispoSet = new DispoSetData();
      try {
         if (jsonObject.has("importPath")) {
            dispoSet.setImportPath(jsonObject.getString("importPath"));
         }
         if (jsonObject.has("name")) {
            dispoSet.setName(jsonObject.getString("name"));
         }
         if (jsonObject.has("operation")) {
            dispoSet.setOperation((DispositionOperationsEnum) jsonObject.get("operation"));
         }
         if (jsonObject.has("notesList")) {
            dispoSet.setNotesList(jsonObject.getJSONArray("notesList"));
         }
      } catch (JSONException ex) {
         throw new OseeCoreException("Error deserializing a Dispositionable Item.", ex);
      }
      return dispoSet;
   }

   public static JSONObject dispoItemToJsonObj(DispoItemData dispoItem) {
      JSONObject jsonObject = new JSONObject(dispoItem, true);
      try {
         jsonObject.put("annotationsList", dispoItem.getAnnotationsList());
         jsonObject.put("discrepanciesList", dispoItem.getDiscrepanciesList());
      } catch (JSONException ex) {
         throw new OseeCoreException("Error deserializing a Dispositionable Item.", ex);
      }

      return jsonObject;
   }

   public static JSONObject discrepancyToJsonObj(Discrepancy discrepancy) {
      JSONObject toReturn = new JSONObject(discrepancy);
      try {
         toReturn.put("idsOfCoveringAnnotations", discrepancy.getIdsOfCoveringAnnotations());
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
      return toReturn;
   }

   public static JSONObject dispoSetToJsonObj(DispoSetData dispoSet) {
      JSONObject jsonObject = new JSONObject(dispoSet, true);
      try {
         jsonObject.put("notesList", dispoSet.getNotesList());
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }

      return jsonObject;
   }

   public static JSONObject annotationToJsonObj(DispoAnnotationData annotation) {
      JSONObject toReturn = new JSONObject(annotation);
      try {
         toReturn.put("idsOfCoveredDiscrepancies", annotation.getIdsOfCoveredDiscrepancies());
         toReturn.put("notesList", annotation.getNotesList());
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
      return toReturn;
   }

   public static DispoAnnotationData jsonObjToDispoAnnotationData(JSONObject object) {
      DispoAnnotationData dispoAnnotation = new DispoAnnotationData();
      try {
         if (object.has("id")) {
            dispoAnnotation.setId(object.getString("id"));
         }
         if (object.has("locationRefs")) {
            dispoAnnotation.setLocationRefs(object.getString("locationRefs"));
         }
         if (object.has("idsOfCoveredDiscrepancies")) {
            dispoAnnotation.setIdsOfCoveredDiscrepancies(object.getJSONArray("idsOfCoveredDiscrepancies"));
         }
         if (object.has("isValid")) {
            dispoAnnotation.setIsConnected(object.getBoolean("isValid"));
         }
         if (object.has("notesList")) {
            dispoAnnotation.setNotesList(object.getJSONArray("notesList"));
         }
         if (object.has("resolution")) {
            dispoAnnotation.setResolution(object.getString("resolution"));
         }
         if (object.has("isResolutionValid")) {
            dispoAnnotation.setIsResolutionValid(object.getBoolean("isResolutionValid"));
         }
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
      return dispoAnnotation;
   }

   public static Discrepancy jsonObjToDiscrepancy(JSONObject object) throws JSONException {
      Discrepancy discrepancy = new Discrepancy();
      discrepancy.setLocationRange(jsonObjToLocationRagne(object.getJSONObject("locationRange")));
      discrepancy.setText(object.getString("text"));
      discrepancy.setId(object.getInt("id"));
      discrepancy.setIdsOfCoveringAnnotations(object.getJSONArray("idsOfCoveringAnnotations"));
      return discrepancy;
   }

   public static LocationRange jsonObjToLocationRagne(JSONObject object) throws JSONException {
      LocationRange range = new LocationRange();
      range.setStart(object.getInt("start"));
      range.setEnd(object.getInt("end"));
      return range;
   }
}

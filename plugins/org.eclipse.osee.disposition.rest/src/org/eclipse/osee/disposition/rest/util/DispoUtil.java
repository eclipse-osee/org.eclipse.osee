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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.LocationRange;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.rest.internal.LocationRangesCompressor;
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

   public static JSONObject asJSONObject(String value) {
      try {
         return new JSONObject(value);
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static JSONObject getById(JSONArray list, String id) {
      try {
         for (int i = 0; i < list.length(); i++) {
            JSONObject object;
            object = list.getJSONObject(i);
            if (object.has("id")) {
               if (object.getString("id").equals(id)) {
                  return object;
               }
            }
         }
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }

      return null;
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
         dispoSetData.setDispoConfig(dispoSet.getDispoConfig());
      } else {
         dispoSetData = null;
      }
      return dispoSetData;
   }

   public static DispoItemData itemArtToItemData(DispoItem dispoItemArt, boolean isIncludeDiscrepancies) {
      DispoItemData dispoItemData = new DispoItemData();
      dispoItemData.setName(dispoItemArt.getName());
      dispoItemData.setGuid(dispoItemArt.getGuid());
      dispoItemData.setCreationDate(dispoItemArt.getCreationDate());
      dispoItemData.setAssignee(dispoItemArt.getAssignee());
      dispoItemData.setVersion(dispoItemArt.getVersion());
      dispoItemData.setLastUpdate(dispoItemArt.getLastUpdate());
      dispoItemData.setStatus(dispoItemArt.getStatus());
      dispoItemData.setTotalPoints(dispoItemArt.getTotalPoints());
      dispoItemData.setNeedsRerun(dispoItemArt.getNeedsRerun());
      dispoItemData.setCategory(dispoItemArt.getCategory());
      dispoItemData.setMachine(dispoItemArt.getMachine());
      dispoItemData.setAborted(dispoItemArt.getAborted());
      dispoItemData.setElapsedTime(dispoItemArt.getElapsedTime());
      if (isIncludeDiscrepancies) {
         dispoItemData.setDiscrepanciesList(dispoItemArt.getDiscrepanciesList());
      }
      return dispoItemData;
   }

   @SuppressWarnings("unchecked")
   private static String discrepanciesToString(JSONObject discrepanciesList) {
      List<Integer> discrepanciesPoints = new ArrayList<Integer>();
      Iterator<String> iterator = discrepanciesList.keys();
      while (iterator.hasNext()) {
         try {
            JSONObject jObject = discrepanciesList.getJSONObject(iterator.next());
            int location = jObject.getInt("location");
            discrepanciesPoints.add(location);
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }

      return LocationRangesCompressor.compress(discrepanciesPoints);

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
         if (jsonObject.has("totalPoints")) {
            dispoItem.setTotalPoints(jsonObject.getString("totalPoints"));
         }
         if (jsonObject.has("needsRerun")) {
            dispoItem.setNeedsRerun(jsonObject.getBoolean("needsRerun"));
         }
         if (jsonObject.has("itemVersion")) {
            dispoItem.setVersion(jsonObject.getString("itemVersion"));
         }
         if (jsonObject.has("assignee")) {
            dispoItem.setAssignee(jsonObject.getString("assignee"));
         }
         if (jsonObject.has("discrepanciesList")) {
            dispoItem.setDiscrepanciesList(jsonObject.getJSONObject("discrepanciesList"));
         }
         if (jsonObject.has("annotationsList")) {
            dispoItem.setAnnotationsList(jsonObject.getJSONArray("annotationsList"));
         }
         if (jsonObject.has("category")) {
            dispoItem.setCategory(jsonObject.getString("category"));
         }
         if (jsonObject.has("machine")) {
            dispoItem.setMachine(jsonObject.getString("machine"));
         }
         if (jsonObject.has("elapsedTime")) {
            dispoItem.setElapsedTime(jsonObject.getString("elapsedTime"));
         }
         if (jsonObject.has("aborted")) {
            dispoItem.setAborted(jsonObject.getBoolean("aborted"));
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
            dispoSet.setOperation(jsonObject.getString("operation"));
         }
         if (jsonObject.has("notesList")) {
            dispoSet.setNotesList(jsonObject.getJSONArray("notesList"));
         }
      } catch (JSONException ex) {
         throw new OseeCoreException("Error deserializing a Dispositionable Item.", ex);
      }
      return dispoSet;
   }

   public static JSONObject dispoItemToJsonObj(DispoItem dispoItem) {
      JSONObject jsonObject = new JSONObject();
      try {
         jsonObject.put("discrepanciesAsRanges", discrepanciesToString(dispoItem.getDiscrepanciesList()));
         jsonObject.put("failureCount", dispoItem.getDiscrepanciesList().length());
         jsonObject.put("name", dispoItem.getName());
         jsonObject.put("status", dispoItem.getStatus());
         jsonObject.put("totalPoints", dispoItem.getTotalPoints());
         jsonObject.put("assignee", dispoItem.getAssignee());
         jsonObject.put("needsRerun", dispoItem.getNeedsRerun());
         jsonObject.put("guid", dispoItem.getGuid());
         jsonObject.put("category", dispoItem.getCategory());
         jsonObject.put("machine", dispoItem.getMachine());
         jsonObject.put("elapsedTime", dispoItem.getElapsedTime());
         jsonObject.put("aborted", dispoItem.getAborted());
      } catch (JSONException ex) {
         throw new OseeCoreException("Error deserializing a Dispositionable Item.", ex);
      }

      return jsonObject;
   }

   public static JSONObject discrepancyToJsonObj(Discrepancy discrepancy) {
      JSONObject toReturn = new JSONObject(discrepancy);
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
         if (object.has("index")) {
            dispoAnnotation.setIndex(object.getInt("index"));
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
         if (object.has("developerNotes")) {
            dispoAnnotation.setDeveloperNotes(object.getString("developerNotes"));
         }
         if (object.has("customerNotes")) {
            dispoAnnotation.setCustomerNotes(object.getString("customerNotes"));
         }
         if (object.has("resolution")) {
            dispoAnnotation.setResolution(object.getString("resolution"));
         }
         if (object.has("isConnected")) {
            dispoAnnotation.setIsConnected(object.getBoolean("isConnected"));
         }
         if (object.has("isResolutionValid")) {
            dispoAnnotation.setIsResolutionValid(object.getBoolean("isResolutionValid"));
         }
         if (object.has("resolutionType")) {
            dispoAnnotation.setResolutionType(object.getString("resolutionType"));
         }
      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
      return dispoAnnotation;
   }

   public static Discrepancy jsonObjToDiscrepancy(JSONObject object) throws JSONException {
      Discrepancy discrepancy = new Discrepancy();
      discrepancy.setLocation(object.getInt("location"));
      discrepancy.setText(object.getString("text"));
      discrepancy.setId(object.getString("id"));
      return discrepancy;
   }

   public static LocationRange jsonObjToLocationRagne(JSONObject object) throws JSONException {
      LocationRange range = new LocationRange();
      range.setStart(object.getInt("start"));
      range.setEnd(object.getInt("end"));
      return range;
   }

   public static Note jsonObjToNote(JSONObject object) throws JSONException {
      Note note = new Note();
      note.setContent(object.getString("content"));
      note.setDateString(object.getString("dateString"));
      note.setType(object.getString("type"));
      return note;
   }
}

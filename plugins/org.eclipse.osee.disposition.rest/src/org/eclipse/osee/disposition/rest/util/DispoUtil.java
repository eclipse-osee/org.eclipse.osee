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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoConfigData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.DispoSummarySeverity;
import org.eclipse.osee.disposition.model.Note;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.model.OperationSummaryEntry;
import org.eclipse.osee.disposition.model.ResolutionMethod;
import org.eclipse.osee.disposition.rest.internal.LocationRangesCompressor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public final class DispoUtil {

   private static final Pattern pattern = Pattern.compile("^[,\\d-\\s]+$");
   private static final Pattern removeLastDot = Pattern.compile("[^\\.]([^.]*)$", Pattern.CASE_INSENSITIVE);

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

   public static boolean isDefaultAnotation(DispoAnnotationData annotation) {
      return annotation.getResolutionType().equalsIgnoreCase(
         DispoStrings.Test_Unit_Resolution) || annotation.getResolutionType().equalsIgnoreCase(
            DispoStrings.Exception_Handling_Resolution);
   }

   public static boolean isNumericLocations(String str) {
      Matcher matcher = pattern.matcher(str.trim());
      return matcher.matches();
   }

   public static String operationReportToString(OperationReport report) {
      JSONObject reportAsJson = new JSONObject(report);
      return reportAsJson.toString();
   }

   public static OperationReport cleanOperationReport(OperationReport origReport) {
      OperationReport newRerport = new OperationReport();
      if (origReport.getStatus().isFailed()) {
         List<OperationSummaryEntry> entries = origReport.getEntries();
         for (OperationSummaryEntry entry : entries) {
            if (DispoSummarySeverity.ERROR.equals(entry.getSeverity())) {
               newRerport.addEntry(entry.getName(), entry.getMessage(), entry.getSeverity());
            }
         }
         return newRerport;
      } else {
         return origReport;
      }
   }

   public static DispoAnnotationData getById(List<DispoAnnotationData> list, String id) {
      for (DispoAnnotationData annotation : list) {
         if (annotation.getId().equals(id)) {
            return annotation;
         }
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

   public static DispoItemData itemArtToItemData(DispoItem dispoItemArt, boolean isIncludeDiscrepancies) {
      return itemArtToItemData(dispoItemArt, isIncludeDiscrepancies, false);
   }

   public static DispoItemData itemArtToItemData(DispoItem dispoItemArt, boolean isIncludeDiscrepancies, boolean isIncludeAnnotations) {
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
      dispoItemData.setItemNotes(dispoItemArt.getItemNotes());
      dispoItemData.setFileNumber(dispoItemArt.getFileNumber());
      dispoItemData.setMethodNumber(dispoItemArt.getMethodNumber());
      dispoItemData.setTeam(dispoItemArt.getTeam());
      if (isIncludeDiscrepancies) {
         Map<String, Discrepancy> discrepanciesList = dispoItemArt.getDiscrepanciesList();
         dispoItemData.setDiscrepanciesList(discrepanciesList);
         dispoItemData.setDiscrepanciesAsRanges(discrepanciesToString(discrepanciesList));
         dispoItemData.setFailureCount(discrepanciesList.size());
      }
      if (isIncludeAnnotations) {
         dispoItemData.setAnnotationsList(dispoItemArt.getAnnotationsList());
      }
      return dispoItemData;
   }

   public static String discrepanciesToString(Map<String, Discrepancy> discrepanciesList) {
      String toReturn;
      boolean isAllNumeric = true;
      List<Integer> discrepancyLocationAsInts = new ArrayList<>();
      List<String> discrepancyLocationsAsString = new ArrayList<>();
      for (String key : discrepanciesList.keySet()) {
         Discrepancy disrepancy = discrepanciesList.get(key);
         String location = disrepancy.getLocation();
         if (isAllNumeric && isNumericLocations(location)) {
            discrepancyLocationAsInts.add(Integer.valueOf(location));
         } else {
            isAllNumeric = false;
            discrepancyLocationsAsString.add(location);
         }
      }

      if (isAllNumeric) {
         toReturn = LocationRangesCompressor.compress(discrepancyLocationAsInts);
      } else {
         toReturn = listToString(discrepancyLocationsAsString);
      }
      return toReturn;
   }

   public static String listToString(List<String> locations) {
      StringBuilder sb = new StringBuilder();
      boolean isFirst = true;
      for (String location : locations) {
         if (isFirst) {
            sb.append(location);
            isFirst = false;
         } else {
            sb.append(", ");
            sb.append(location);
         }
      }
      return sb.toString();
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
         if (jsonObject.has("status")) {
            dispoItem.setStatus(jsonObject.getString("status"));
         }
         if (jsonObject.has("totalPoints")) {
            dispoItem.setTotalPoints(jsonObject.getString("totalPoints"));
         }
         if (jsonObject.has("needsRerun")) {
            dispoItem.setNeedsRerun(jsonObject.getBoolean("needsRerun"));
         }
         if (jsonObject.has("version")) {
            dispoItem.setVersion(jsonObject.getString("version"));
         }
         if (jsonObject.has("assignee")) {
            dispoItem.setAssignee(jsonObject.getString("assignee"));
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
         if (jsonObject.has("itemNotes")) {
            dispoItem.setItemNotes(jsonObject.getString("itemNotes"));
         }
         if (jsonObject.has("fileNumber")) {
            dispoItem.setFileNumber(jsonObject.getString("fileNumber"));
         }
         if (jsonObject.has("methodNumber")) {
            dispoItem.setMethodNumber(jsonObject.getString("methodNumber"));
         }
         if (jsonObject.has("team")) {
            dispoItem.setTeam(jsonObject.getString("team"));
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
            JSONArray jArray = jsonObject.getJSONArray("notesList");
            List<Note> notesList = new ArrayList<Note>();
            for (int i = 0; i < jArray.length(); i++) {
               notesList.add(jsonObjToNote(jArray.getJSONObject(i)));
            }
            dispoSet.setNotesList(notesList);
         }
         if (jsonObject.has("ciSet")) {
            dispoSet.setCiSet(jsonObject.getString("ciSet"));
         }
      } catch (JSONException ex) {
         throw new OseeCoreException("Error deserializing a Dispositionable Item.", ex);
      }
      return dispoSet;
   }

   public static JSONObject dispoItemToJsonObj(DispoItem dispoItem, boolean isDetailed) {
      JSONObject jsonObject = new JSONObject();
      try {
         jsonObject.put("discrepanciesAsRanges", discrepanciesToString(dispoItem.getDiscrepanciesList()));
         if (isDetailed) {
            jsonObject.put("discrepancies", dispoItem.getDiscrepanciesList());
         }
         jsonObject.put("failureCount", dispoItem.getDiscrepanciesList().size());
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
         jsonObject.put("lastUpdated", dispoItem.getLastUpdate());
         jsonObject.put("creationDate", dispoItem.getCreationDate());
         jsonObject.put("itemNotes", dispoItem.getItemNotes());
         jsonObject.put("version", dispoItem.getVersion());
         jsonObject.put("fileNumber", dispoItem.getFileNumber());
         jsonObject.put("methodNumber", dispoItem.getMethodNumber());
         jsonObject.put("team", dispoItem.getTeam());
      } catch (JSONException ex) {
         throw new OseeCoreException("Error deserializing a Dispositionable Item.", ex);
      }

      return jsonObject;
   }

   public static JSONArray noteListToJsonObj(List<Note> notes) {
      JSONArray jArray = new JSONArray();
      for (Note note : notes) {
         jArray.put(new JSONObject(note));
      }

      return jArray;
   }

   public static JSONObject discrepancyToJsonObj(Discrepancy discrepancy) {
      JSONObject toReturn = new JSONObject(discrepancy);
      return toReturn;
   }

   public static OperationSummaryEntry jsonObjToOperationSummaryEntry(JSONObject jObj) throws JSONException {
      OperationSummaryEntry entry = new OperationSummaryEntry();
      if (jObj.has("message")) {
         entry.setMessage(jObj.getString("message"));
      }
      if (jObj.has("name")) {
         entry.setName(jObj.getString("name"));
      }
      if (jObj.has("severity")) {
         String severity = jObj.getJSONObject("severity").getString("name").toUpperCase();
         DispoSummarySeverity dispoSummarySeverity = DispoSummarySeverity.valueOf(severity);
         entry.setSeverity(dispoSummarySeverity);
      }
      return entry;
   }

   public static OperationReport jsonObjToOperationSummary(JSONObject jObj) {
      OperationReport summary = new OperationReport();
      List<OperationSummaryEntry> entries = new ArrayList<>();

      try {
         if (jObj.has("entries")) {
            JSONArray entriesJson = jObj.getJSONArray("entries");
            for (int i = 0; i < entriesJson.length(); i++) {
               JSONObject entryAsJson = entriesJson.getJSONObject(i);
               OperationSummaryEntry entry = jsonObjToOperationSummaryEntry(entryAsJson);
               entries.add(entry);
            }
            summary.setEntries(entries);
         }
      } catch (JSONException ex) {
         //
      }
      return summary;
   }

   public static JSONObject dispoSetToJsonObj(DispoSet dispoSet) {
      JSONObject jsonObject = new JSONObject(dispoSet, true);
      JSONObject operationSummaryJObj = new JSONObject();
      JSONArray jArray = new JSONArray();
      try {
         for (OperationSummaryEntry entry : dispoSet.getOperationSummary().getEntries()) {
            JSONObject entryJson = new JSONObject(entry);
            jArray.put(entryJson);
         }
         operationSummaryJObj.put("entries", jArray);
         jsonObject.put("operationSummary", operationSummaryJObj);
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
            List<String> idsOfCoveredDiscrepanciesList = new ArrayList<String>();
            JSONArray jArray = object.getJSONArray("idsOfCoveredDiscrepancies");
            for (int i = 0; i < jArray.length(); i++) {
               idsOfCoveredDiscrepanciesList.add(jArray.getString(i));
            }
            dispoAnnotation.setIdsOfCoveredDiscrepancies(idsOfCoveredDiscrepanciesList);
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
         if (object.has("isDefault")) {
            dispoAnnotation.setIsDefault(object.getBoolean("isDefault"));
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

   public static JSONObject disrepanciesMapToJson(Map<String, Discrepancy> discrepancies) {
      JSONObject jObject = null;
      try {
         jObject = new JSONObject();
         for (String key : discrepancies.keySet()) {
            jObject.put(key, DispoUtil.discrepancyToJsonObj(discrepancies.get(key)));
         }

      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
      return jObject;

   }

   public static JSONArray annotationsListToJson(List<DispoAnnotationData> annotations) {
      JSONArray jArray = null;
      try {
         jArray = new JSONArray();
         for (DispoAnnotationData annotation : annotations) {
            jArray.put(annotation.getIndex(), DispoUtil.annotationToJsonObj(annotation));
         }

      } catch (JSONException ex) {
         throw new OseeCoreException(ex);
      }
      return jArray;
   }

   public static Discrepancy jsonObjToDiscrepancy(JSONObject object) throws JSONException {
      Discrepancy discrepancy = new Discrepancy();
      discrepancy.setLocation(object.getString("location"));
      discrepancy.setText(object.getString("text"));
      discrepancy.setId(object.getString("id"));
      return discrepancy;
   }

   public static Note jsonObjToNote(JSONObject object) throws JSONException {
      Note note = new Note();
      note.setContent(object.getString("content"));
      note.setDateString(object.getString("dateString"));
      note.setType(object.getString("type"));
      return note;
   }

   public static ResolutionMethod jsonObjToResolutionMethod(JSONObject object) throws JSONException {
      ResolutionMethod method = new ResolutionMethod();
      method.setText(object.getString("text"));
      method.setValue(object.getString("value"));
      method.setIsDefault(object.getBoolean("isDefault"));
      return method;
   }

   public static DispoConfigData configArtToConfigData(DispoConfig config) {
      DispoConfigData configData = new DispoConfigData();
      configData.setValidResolutions(config.getValidResolutions());

      return configData;
   }

   public static DispoConfig getDefaultConfig() {
      DispoConfigData configData = new DispoConfigData();
      ResolutionMethod defaultMethod = new ResolutionMethod();
      defaultMethod.setText("METHODS HAVEN'T BEEN SET");
      defaultMethod.setValue("INVALID");
      defaultMethod.setIsDefault(true);
      configData.setValidResolutions(Collections.singletonList(defaultMethod));

      return configData;
   }

   public static List<DispoAnnotationData> asAnnotationsList(JSONArray annotations) {
      List<DispoAnnotationData> toReturn = new ArrayList<>();
      for (int i = 0; i < annotations.length(); i++) {
         try {
            toReturn.add(jsonObjToDispoAnnotationData(annotations.getJSONObject(i)));
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }

      return toReturn;
   }

   public static JSONArray listAsJsonArray(List<DispoAnnotationData> annotations) {
      JSONArray toReturn = new JSONArray();
      for (DispoAnnotationData annotation : annotations) {
         try {
            toReturn.put(annotation.getIndex(), annotationToJsonObj(annotation));
         } catch (JSONException ex) {
            throw new OseeCoreException(ex);
         }
      }

      return toReturn;
   }

   public static Date getTimestampOfFile(String fullPathFileName) {
      Date date = new Date(0);
      File f = new File(fullPathFileName);
      if (f.exists()) {
         long lastModified = f.lastModified();
         date = new Date(lastModified);
      }
      return date;
   }

   public static HashMap<String, String> splitTestScriptNameAndPath(List<DispoAnnotationData> annotations) {
      HashMap<String, String> testScriptNameToPath = new HashMap<>();
      for (DispoAnnotationData data : annotations) {
         String name = "", path = "", comment = "";
         String resolution = data.getResolution();
         if (!resolution.isEmpty()) {
            String[] split = resolution.split("___");
            if (split.length > 1) {
               path = split[0];
               comment = split[1];
            } else {
               path = split.toString();
            }
            path = path.replaceFirst("results", "");
            Matcher matcher = removeLastDot.matcher(path);
            while (matcher.find()) {
               name = matcher.group() + ".java";
            }
            path = path.replaceAll("\\.", "/");
         }
         testScriptNameToPath.put(name, path);
      }
      return testScriptNameToPath;
   }

}

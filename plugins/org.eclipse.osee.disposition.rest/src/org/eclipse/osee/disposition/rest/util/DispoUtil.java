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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.codehaus.jackson.JsonNode;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoConfigData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.DispoSummarySeverity;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.model.OperationSummaryEntry;
import org.eclipse.osee.disposition.model.ResolutionMethod;
import org.eclipse.osee.disposition.rest.internal.LocationRangesCompressor;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public final class DispoUtil {

   private static final Pattern pattern = Pattern.compile("^[,\\d-\\s]+$");
   private static final Pattern removeLastDot = Pattern.compile("[^\\.]([^.]*)$", Pattern.CASE_INSENSITIVE);

   private DispoUtil() {
      //
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
      return JsonUtil.toJson(report);
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
         if (annotation.getGuid().equals(id)) {
            return annotation;
         }
      }

      return null;
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

   public static DispoItemData reconstructDispoItem(DispoItem dispoItem, boolean isDetailed) {
      DispoItemData dispoItemData = new DispoItemData();
      dispoItemData.setDiscrepanciesAsRanges(discrepanciesToString(dispoItem.getDiscrepanciesList()));
      if (isDetailed) {
         dispoItemData.setDiscrepanciesList(dispoItem.getDiscrepanciesList());
      }
      dispoItemData.setFailureCount(dispoItem.getDiscrepanciesList().size());
      dispoItemData.setName(dispoItem.getName());
      dispoItemData.setStatus(dispoItem.getStatus());
      dispoItemData.setTotalPoints(dispoItem.getTotalPoints());
      dispoItemData.setAssignee(dispoItem.getAssignee());
      dispoItemData.setNeedsRerun(dispoItem.getNeedsRerun());
      dispoItemData.setGuid(dispoItem.getGuid());
      dispoItemData.setCategory(dispoItem.getCategory());
      dispoItemData.setMachine(dispoItem.getMachine());
      dispoItemData.setElapsedTime(dispoItem.getElapsedTime());
      dispoItemData.setAborted(dispoItem.getAborted());
      dispoItemData.setLastUpdate(dispoItem.getLastUpdate());
      dispoItemData.setCreationDate(dispoItem.getCreationDate());
      dispoItemData.setItemNotes(dispoItem.getItemNotes());
      dispoItemData.setVersion(dispoItem.getVersion());
      dispoItemData.setFileNumber(dispoItem.getFileNumber());
      dispoItemData.setMethodNumber(dispoItem.getMethodNumber());
      dispoItemData.setTeam(dispoItem.getTeam());

      return dispoItemData;
   }

   public static DispoAnnotationData reconstructDispoAnnotationData(DispoAnnotationData annotationData) {
      DispoAnnotationData annotation = new DispoAnnotationData();
      try {
         annotation.setId(Optional.ofNullable(annotationData.getGuid()).get());
         annotation.setIndex(Optional.ofNullable(annotationData.getIndex()).get());
         annotation.setLocationRefs(Optional.ofNullable(annotationData.getLocationRefs()).get());
         if (!annotationData.getIdsOfCoveredDiscrepancies().isEmpty()) {
            annotation.setIdsOfCoveredDiscrepancies(annotationData.getIdsOfCoveredDiscrepancies());
         }
         annotation.setIsConnected(Optional.ofNullable(annotationData.isValid()).get());
         annotation.setDeveloperNotes(Optional.ofNullable(annotationData.getDeveloperNotes()).get());
         annotation.setCustomerNotes(Optional.ofNullable(annotationData.getCustomerNotes()).get());
         annotation.setResolution(Optional.ofNullable(annotationData.getResolution()).get());
         annotation.setIsConnected(Optional.ofNullable(annotationData.getIsConnected()).get());
         annotation.setIsDefault(Optional.ofNullable(annotationData.getIsDefault()).get());
         annotation.setIsResolutionValid(Optional.ofNullable(annotationData.getIsResolutionValid()).get());
         annotation.setResolutionType(Optional.ofNullable(annotationData.getResolutionType()).get());
      } catch (Exception ex) {
         throw new OseeCoreException("Could not reconstruct Annotation Data", ex);
      }
      return annotation;
   }

   public static ResolutionMethod reconstructResolutionMethod(ResolutionMethod resolutionMethod) {
      ResolutionMethod method = new ResolutionMethod();
      method.setText(resolutionMethod.getText());
      method.setValue(resolutionMethod.getValue());
      method.setIsDefault(resolutionMethod.getIsDefault());
      return method;
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

   @SuppressWarnings("unchecked")
   public static OperationReport jsonObjToOperationSummary(String jObj) {
      OperationReport summary = new OperationReport();
      List<OperationSummaryEntry> entries = new LinkedList<>();
      if (!jObj.contains("entries")) {
         return summary;
      }
      JsonNode entriesNode = JsonUtil.readTree(jObj).get("entries");
      entries = JsonUtil.readValue(entriesNode.toString(), List.class);
      summary.setEntries(entries);
      return summary;
   }

   public static <T> List<T> jsonStringToList(String jObj, Class<T> valueType) {
      List<T> toReturn = new ArrayList<>();
      JsonNode node = JsonUtil.readTree(jObj);
      Iterator<JsonNode> elements = node.getElements();
      while (elements.hasNext()) {
         toReturn.add(JsonUtil.readValue(elements.next().toString(), valueType));
      }
      return toReturn;
   }

   public static Map<String, Discrepancy> jsonStringToDiscrepanciesMap(String jObj) {
      Map<String, Discrepancy> toReturn = new HashMap<>();
      JsonNode node = JsonUtil.readTree(jObj);
      Iterator<JsonNode> elements = node.getElements();
      while (elements.hasNext()) {
         Discrepancy discrepancy = JsonUtil.readValue(elements.next().toString(), Discrepancy.class);
         toReturn.put(discrepancy.getId(), discrepancy);
      }
      return toReturn;
   }

   public static DispoItem findDispoItem(List<DispoItem> items, String itemName) {
      for (DispoItem dItem : items) {
         if (dItem.getName().equals(itemName)) {
            return dItem;
         }
      }
      return null;
   }

   public static List<Integer> splitDiscrepancyLocations(String locations) {
      String[] locationString = locations.split(",");
      List<Integer> range = new ArrayList<>();
      if (locations != null && !locations.isEmpty()) {
         for (String location : locationString) {
            String[] loc = location.split("-");
            if (loc.length > 1) {
               range.addAll(
                  IntStream.range(Integer.valueOf(loc[0].trim()), Integer.valueOf(loc[1].trim()) + 1).boxed().collect(
                     Collectors.toList()));
            } else {
               range.add(Integer.valueOf(loc[0].trim()));
            }
         }
      }
      return range;
   }

   public static List<String> findDiscrepancyLocsToRemove(List<Integer> ranges, DispoItem item) {
      List<String> removeDiscrepancies = new ArrayList<>();
      if (ranges != null && !ranges.isEmpty()) {
         for (Entry<String, Discrepancy> discrepancy : item.getDiscrepanciesList().entrySet()) {
            Discrepancy value = discrepancy.getValue();
            String location = value.getLocation();
            if (Strings.isValid(location)) {
               if (ranges.contains(location)) {
                  removeDiscrepancies.add(discrepancy.getKey());
                  break;
               }
            }
         }
      }
      return removeDiscrepancies;
   }

   public static List<String> removeAllDiscrepancies(List<Integer> ranges, DispoItem item) {
      List<String> removeDiscrepancies = new ArrayList<>();
      if (ranges != null && !ranges.isEmpty()) {
         for (Entry<String, Discrepancy> discrepancy : item.getDiscrepanciesList().entrySet()) {
            Discrepancy value = discrepancy.getValue();
            String location = value.getLocation();
            if (Strings.isValid(location)) {
               if (ranges.contains(location)) {
                  removeDiscrepancies.add(discrepancy.getKey());
                  break;
               }
            }
         }
      }
      return removeDiscrepancies;
   }

   public static List<String> findMissingDiscrepancyLocs(List<Integer> ranges, DispoItem item) {
      List<String> missingDiscrepanciesLoc = new ArrayList<>();
      List<String> currentLocations = new ArrayList<>();
      if (ranges != null && !ranges.isEmpty()) {
         for (Entry<String, Discrepancy> discrepancy : item.getDiscrepanciesList().entrySet()) {
            String location = discrepancy.getValue().getLocation();
            if (Strings.isValid(location)) {
               currentLocations.add(location);
            }
         }
         for (Integer range : ranges) {
            if (!currentLocations.contains(String.valueOf(range))) {
               missingDiscrepanciesLoc.add(String.valueOf(range));
            }
         }
      }
      return missingDiscrepanciesLoc;
   }
}

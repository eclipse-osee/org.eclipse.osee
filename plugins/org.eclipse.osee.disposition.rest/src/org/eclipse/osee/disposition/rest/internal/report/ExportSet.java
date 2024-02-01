/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.disposition.rest.internal.report;

import static org.eclipse.osee.disposition.model.DispoStrings.MODIFY_CODE;
import static org.eclipse.osee.disposition.model.DispoStrings.MODIFY_REQT;
import static org.eclipse.osee.disposition.model.DispoStrings.MODIFY_TEST;
import static org.eclipse.osee.disposition.model.DispoStrings.MODIFY_TOOL;
import static org.eclipse.osee.disposition.model.DispoStrings.MODIFY_WORK_PRODUCT;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.ResolutionMethod;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.LocationRangesCompressor;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;

/**
 * @author Angel Avila
 */
public class ExportSet {
   private final DispoApi dispoApi;
   Map<CoverageLevel, WrapInt> levelToTotalCount = new HashMap<>();
   Map<CoverageLevel, WrapInt> levelToCoveredTotalCount = new HashMap<>();
   Map<String, Integer> defaultCases = new HashMap<>();

   private final String LEVEL_A_LOCATION_PATTERN = "\\s*\\d+\\.\\d+\\s*\\(P[a-z]\\)\\s*$";
   private final String LEVEL_A_SUB_LOCATION_PATTERN = "\\s*\\d+\\.\\d+\\s*\\(P[a-z]\\)\\.\\d+$";
   private final String LEVEL_B_LOCATION_PATTERN = "(.*?RESULT.*|\\s*\\d+\\s*\\.\\s*(T|F).*)";

   private enum CoverageLevel {
      A,
      B,
      C,
   }

   private class WrapInt {
      private int value;

      private WrapInt(int initValue) {
         value = initValue;
      }

      private void inc() {
         value++;
      }

      private void inc(int incAmt) {
         value += incAmt;
      }

      private int getValue() {
         return value;
      }
   }

   public ExportSet(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   public void runDispoReport(BranchId branch, DispoSet setPrimary, String option, OutputStream outputStream,
      String fileName) {
      List<DispoItem> items = dispoApi.getDispoItems(branch, setPrimary.getGuid(), true);

      try {
         ExcelXmlWriter sheetWriter;

         if (fileName != "") {
            sheetWriter = new ExcelXmlWriter(fileName, null);
         } else {
            Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
            sheetWriter = new ExcelXmlWriter(writer);
         }

         String[] headers = getHeadersDetailed();
         int columns = headers.length;
         sheetWriter.startSheet(setPrimary.getName(), headers.length);
         sheetWriter.writeRow((Object[]) headers);

         for (DispoItem item : items) {
            DispoConnector connector = new DispoConnector();
            List<String> allUncoveredDiscprepancies = connector.getAllUncoveredDiscrepancies(item);
            List<Integer> allUncoveredDiscrepanciesAsInts = getDiscrepanciesAsInts(allUncoveredDiscprepancies);
            String[] row = new String[columns];
            int index = 0;

            Map<String, Discrepancy> discrepanciesList = item.getDiscrepanciesList();

            row[index++] = String.valueOf(item.getName());
            row[index++] = String.valueOf(item.getCategory());
            row[index++] = String.valueOf(item.getStatus());
            row[index++] = String.valueOf(item.getTotalPoints());
            row[index++] = String.valueOf(item.getDiscrepanciesList().size());
            row[index++] = String.valueOf(DispoUtil.discrepanciesToString(discrepanciesList));
            row[index++] = String.valueOf(allUncoveredDiscprepancies.size());
            String uncoveredDiscrepancies;
            if (allUncoveredDiscrepanciesAsInts.isEmpty()) {
               uncoveredDiscrepancies = DispoUtil.listToString(allUncoveredDiscprepancies);
            } else {
               uncoveredDiscrepancies = LocationRangesCompressor.compress(allUncoveredDiscrepanciesAsInts);
            }

            List<DispoAnnotationData> annotations = item.getAnnotationsList();
            Collections.sort(annotations, new Comparator<DispoAnnotationData>() {
               @Override
               public int compare(DispoAnnotationData o1, DispoAnnotationData o2) {
                  return o1.getLocationRefs().compareTo(o2.getLocationRefs());
               }
            });

            row[index++] = String.valueOf(uncoveredDiscrepancies);
            row[index++] = String.valueOf(item.getAssignee());
            row[index++] = String.valueOf(item.getTeam());
            row[index++] = String.valueOf(item.getItemNotes());
            row[index++] = String.valueOf(item.getNeedsRerun());
            row[index++] = String.valueOf(item.getAborted());
            row[index++] = String.valueOf(item.getMachine());
            row[index++] = String.valueOf(item.getElapsedTime());
            row[index++] = String.valueOf(item.getCreationDate());
            row[index++] = String.valueOf(item.getLastUpdate());
            row[index++] = String.valueOf(item.getVersion());
            row[index++] = String.valueOf(prettifyAnnotations(annotations));

            sheetWriter.writeRow((Object[]) row);
         }

         sheetWriter.endSheet();
         sheetWriter.endWorkbook();

      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

   }

   public ByteArrayOutputStream runCoverageReports(BranchId branch, DispoSet setPrimary, String option,
      String fileName) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try (FileOutputStream fos = new FileOutputStream(fileName)) {
         baos.writeTo(fos);
         runCoverageReport(branch, setPrimary, option, baos, "");
         baos.close();
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }

      return baos;
   }

   public void runCoverageReport(BranchId branch, DispoSet setPrimary, String option, OutputStream outputStream,
      String fileName) {
      Map<String, String> resolutionsValueToText = new HashMap<>();
      Set<CoverageLevel> levelsInSet = new HashSet<>();
      List<CoverageLevel> levelsInList = new ArrayList<>();
      Map<CoverageLevel, Map<String, Pair<WrapInt, WrapInt>>> leveltoUnitToCovered = new HashMap<>();
      for (CoverageLevel level : CoverageLevel.values()) {
         leveltoUnitToCovered.put(level, new HashMap<>());
         levelToTotalCount.put(level, new WrapInt(0));
         levelToCoveredTotalCount.put(level, new WrapInt(0));
      }

      List<DispoItem> items = dispoApi.getDispoItems(branch, setPrimary.getGuid(), true);

      Map<CoverageLevel, Map<String, WrapInt>> levelToResolutionTypesToCount = new HashMap<>();

      // Init map needed for Cover Sheet aka Resolution Types to Coverage %s
      DispoConfig config = dispoApi.getDispoConfig(branch);
      config.getValidResolutions();
      for (CoverageLevel level : CoverageLevel.values()) {
         Map<String, WrapInt> innerMap = new HashMap<>();
         for (ResolutionMethod resolutionType : config.getValidResolutions()) {
            innerMap.put(resolutionType.getText(), new WrapInt(0));
            resolutionsValueToText.put(resolutionType.getValue(), resolutionType.getText());
            if (level.equals(
               CoverageLevel.A) && !resolutionType.getValue().isEmpty() && !resolutionType.getValue().equals(
                  "Test_Script")) {
               String levelAResolutionType = String.format("%s/Test_Script", resolutionType.getValue());
               innerMap.put(levelAResolutionType, new WrapInt(0));
               resolutionsValueToText.put(levelAResolutionType, levelAResolutionType);
            }
         }
         // Needed for Level A, each part of a pair can be satisfied by differing resolutions.
         if (level.equals(CoverageLevel.A)) {
            innerMap.put("MIXED", new WrapInt(0));
         }
         levelToResolutionTypesToCount.put(level, innerMap);
      }

      try {
         ExcelXmlWriter sheetWriter;
         if (fileName != "") {
            sheetWriter = new ExcelXmlWriter(fileName, null);
         } else {
            Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
            sheetWriter = new ExcelXmlWriter(writer);
         }

         String[] headers = getHeadersCoverage();
         int columns = headers.length;
         sheetWriter.startSheet(setPrimary.getName(), headers.length);
         sheetWriter.writeRow((Object[]) headers);

         for (DispoItem item : items) {
            List<DispoAnnotationData> annotations = item.getAnnotationsList();
            Collections.sort(annotations, new Comparator<DispoAnnotationData>() {
               @Override
               public int compare(DispoAnnotationData o1, DispoAnnotationData o2) {
                  return o1.getLocationRefs().compareTo(o2.getLocationRefs());
               }
            });

            Map<String, List<Integer>> itemWithPairs = new HashMap<>();
            Map<String, Map<Boolean, String>> hitForLevelC = new HashMap<>();

            for (DispoAnnotationData annotation : annotations) {
               String annotationLocRef = annotation.getLocationRefs();

               //The following increments level C coverage when hit on A or B
               if (annotationLocRef.contains(".")) {
                  Map<Boolean, String> coverageAndResolutionLevelC = new HashMap<>();
                  int dotIndex = annotationLocRef.indexOf(".");
                  String baseLocRef = annotationLocRef.substring(0, dotIndex);
                  if (annotation.isValid()) {
                     coverageAndResolutionLevelC.put(true, annotation.getResolutionType());
                     hitForLevelC.put(baseLocRef, coverageAndResolutionLevelC);
                  } else if (!annotation.isValid() && !hitForLevelC.containsKey(baseLocRef)) {
                     coverageAndResolutionLevelC.put(false, annotation.getResolutionType());
                     hitForLevelC.put(baseLocRef, coverageAndResolutionLevelC);
                  }

               }

               //The following if statements are exclusively for MCDC pairs.
               try {

                  //You need to do T/F for the level B if its level A

                  if (annotationLocRef.contains("(P") && !annotation.getIsPairAnnotation()) {
                     Pattern pairsPattern = Pattern.compile(".*\\[\\s*(\\d+)\\s*/\\s*(\\d+)\\s*.*\\]");
                     Matcher matcher = pairsPattern.matcher(annotation.getSatisfiedPairs());
                     List<Integer> pairRows = new ArrayList<>();
                     if (matcher.find()) {
                        pairRows.add(Integer.parseInt(matcher.group(1)));
                        pairRows.add(Integer.parseInt(matcher.group(2)));
                     } else {
                        matcher = pairsPattern.matcher(annotation.getPossiblePairs());
                        if (matcher.find()) {
                           pairRows.add(Integer.parseInt(matcher.group(1)));
                           pairRows.add(Integer.parseInt(matcher.group(2)));
                        }
                     }
                     itemWithPairs.put(annotationLocRef, pairRows);

                  } else if (annotation.getIsPairAnnotation()) {
                     int dotIndex = annotationLocRef.lastIndexOf(".");
                     if (dotIndex != -1) {
                        String parentLocRef = annotationLocRef.substring(0, dotIndex);
                        if (!itemWithPairs.get(parentLocRef).contains(annotation.getRow())) {
                           continue;
                        }
                     }
                  }
               } catch (Exception ex) {
                  //DO NOTHING
               }

               writeRowAnnotation(sheetWriter, columns, item, annotation, setPrimary.getName(),
                  levelToResolutionTypesToCount, leveltoUnitToCovered, levelsInSet);
            }

            for (Map.Entry<String, Map<Boolean, String>> LevelCCoverageData : hitForLevelC.entrySet()) {
               for (Map.Entry<Boolean, String> LevelCAdditionalCoverage : LevelCCoverageData.getValue().entrySet()) {
                  levelToTotalCount.get(CoverageLevel.C).inc();
                  if (LevelCAdditionalCoverage.getKey()) {
                     uptickCoverage(levelToResolutionTypesToCount.get(CoverageLevel.C),
                        leveltoUnitToCovered.get(CoverageLevel.C), levelToCoveredTotalCount.get(CoverageLevel.C),
                        getNormalizedName(item.getName()), LevelCAdditionalCoverage.getValue());
                  }
               }
            }
         }

         levelsInList.addAll(levelsInSet);
         Collections.sort(levelsInList);

         if (levelsInList.contains(CoverageLevel.B)) {
            for (DispoItem item : items) {
               levelToTotalCount.get(CoverageLevel.B).inc();
               uptickCoverage(levelToResolutionTypesToCount.get(CoverageLevel.B),
                  leveltoUnitToCovered.get(CoverageLevel.B), levelToCoveredTotalCount.get(CoverageLevel.B),
                  getNormalizedName(item.getName()), "Test_Script");
            }
         }

         sheetWriter.endSheet();

         // START COVER SHEET
         sheetWriter.startSheet("Cover Sheet", headers.length);

         List<String> coverSheetHeadersList = new ArrayList<>();
         coverSheetHeadersList.add(" ");
         if (levelsInList.contains(CoverageLevel.A)) {
            coverSheetHeadersList.add("MCDC");
         }
         if (levelsInList.contains(CoverageLevel.B)) {
            coverSheetHeadersList.add("Branch");
         }
         if (levelsInList.contains(CoverageLevel.C)) {
            coverSheetHeadersList.add("Statement");
         }

         Object[] coverSheetHeaders = coverSheetHeadersList.toArray();
         sheetWriter.writeRow(coverSheetHeaders);
         String[] row = new String[CoverageLevel.values().length + 1];
         String[] uncoveredRow = new String[CoverageLevel.values().length + 1];

         row[0] = "All Coverage Methods";
         uncoveredRow[0] = "Uncovered";

         int index = 1;
         // send correct numbers according to level for second param
         Iterator<CoverageLevel> iterator = levelsInList.iterator();
         while (iterator.hasNext()) {
            CoverageLevel lvl = iterator.next();
            row[index] =
               getPercent(levelToCoveredTotalCount.get(lvl).getValue(), levelToTotalCount.get(lvl).getValue(), false);
            Integer uncovered = levelToTotalCount.get(lvl).getValue() - levelToCoveredTotalCount.get(lvl).getValue();
            int emptyCount = 0;
            if (levelToResolutionTypesToCount.get(lvl).containsKey(" ")) {
               emptyCount = levelToResolutionTypesToCount.get(lvl).get(" ").getValue(); //It is possible for there to be a space as resolution for empty rather than nothing.
            }
            uncoveredRow[index++] = getPercent(uncovered + emptyCount, levelToTotalCount.get(lvl).getValue(), false);
         }
         sheetWriter.writeRow(row);

         // Try to get Resolution from Level A if available, otherwise get from C
         Set<String> resolutionTypes;
         if (levelsInList.contains(CoverageLevel.A)) {
            resolutionTypes = levelToResolutionTypesToCount.get(CoverageLevel.A).keySet();
         } else if (levelsInList.contains(CoverageLevel.B)) {
            resolutionTypes = levelToResolutionTypesToCount.get(CoverageLevel.B).keySet();
         } else {
            resolutionTypes = levelToResolutionTypesToCount.get(CoverageLevel.C).keySet();
         }

         List<String> orderedResolutionTypes = organizeResolutions(resolutionTypes);
         for (String resolution : orderedResolutionTypes) {
            int index1 = 0;

            row[index1++] =
               resolutionsValueToText.containsKey(resolution) ? resolutionsValueToText.get(resolution) : resolution;

            if (!row[0].equals(" ")) {
               Iterator<CoverageLevel> it = levelsInList.iterator();
               while (it.hasNext()) {
                  CoverageLevel lvl = it.next();
                  if (resolution.contains("/") && index1 > 1) {
                     row[index1++] = "";
                     continue;
                  }
                  if (levelToResolutionTypesToCount.get(lvl).get(resolution) == null) {
                     row[index1++] = "ERROR";
                     continue;
                  }
                  row[index1++] = getPercent(levelToResolutionTypesToCount.get(lvl).get(resolution).getValue(),
                     levelToTotalCount.get(lvl).getValue(), false);
               }

               sheetWriter.writeRow(row);
            }
         }

         if (!defaultCases.isEmpty()) {
            Map<CoverageLevel, Integer> levelToMixed = new HashMap<>();
            int index1 = 0;
            row[index1++] = "MIXED - Expanded Below";
            Iterator<CoverageLevel> it = levelsInList.iterator();
            while (it.hasNext()) {
               CoverageLevel lvl = it.next();

               if (levelToResolutionTypesToCount.get(lvl).get("MIXED") == null) {
                  row[index1++] = "ERROR";
                  continue;
               }
               row[index1++] = getPercent(levelToResolutionTypesToCount.get(lvl).get("MIXED").getValue(),
                  levelToTotalCount.get(lvl).getValue(), false) + " - Expanded Below";
               levelToMixed.put(lvl, levelToResolutionTypesToCount.get(lvl).get("MIXED").getValue());
            }
            sheetWriter.writeRow(row);

            for (Entry<String, Integer> entry : defaultCases.entrySet()) {
               int index2 = 0;
               row[index2++] = entry.getKey();

               Iterator<CoverageLevel> it2 = levelsInList.iterator();
               while (it.hasNext()) {
                  CoverageLevel lvl = it2.next();
                  row[index2++] = getPercent(entry.getValue(), levelToMixed.get(lvl), false);
               }
               sheetWriter.writeRow(row);
            }
         }

         sheetWriter.writeRow(uncoveredRow);
         sheetWriter.endSheet();
         // END COVER SHEET

         // Write Summary Sheet
         Object[] summarySheetHeaders = getHeadersSummarySheet();
         columns = summarySheetHeaders.length;
         sheetWriter.startSheet("Summary Sheet", columns);
         sheetWriter.writeRow(summarySheetHeaders);

         Object[] row2 = new String[columns];
         // Try to get Resolution from Level A if available, otherwise get from C
         Set<String> units = leveltoUnitToCovered.get(CoverageLevel.A).keySet();
         if (units.isEmpty()) {
            units = leveltoUnitToCovered.get(CoverageLevel.C).keySet();
         }

         index = 0;
         for (String unit : units) {
            index = 0;
            row2[index++] = unit;
            for (CoverageLevel level : CoverageLevel.values()) {
               Map<String, Pair<WrapInt, WrapInt>> unitToCovered = leveltoUnitToCovered.get(level);
               // If this check is false then Unit has no Annotations of this level
               if (!unitToCovered.isEmpty() && unitToCovered.containsKey(unit)) {
                  Pair<WrapInt, WrapInt> coveredOverTotal = unitToCovered.get(unit);
                  int covered = coveredOverTotal.getFirst().getValue();
                  int total = coveredOverTotal.getSecond().getValue();
                  row2[index++] = String.valueOf(covered);
                  row2[index++] = String.valueOf(total);
                  Double percent = (((double) covered / total) * 100);
                  row2[index++] = String.format("%2.2f%%", percent);
               } else {
                  row2[index++] = " ";
                  row2[index++] = " ";
                  row2[index++] = " ";
               }
            }
            sheetWriter.writeRow(row2);
         }

         sheetWriter.endSheet();

         // Write Test_Script Sheet
         Object[] testScriptSheetHeaders =
            {"Unit", "Code Line", "Resolution Type", "Script Name", "Script Path", "Script Notes"};
         sheetWriter.startSheet("Test Script Sheet", testScriptSheetHeaders.length);
         sheetWriter.writeRow(testScriptSheetHeaders);
         for (DispoItem item : items) {
            List<DispoAnnotationData> annotations = item.getAnnotationsList();
            Collections.sort(annotations, new Comparator<DispoAnnotationData>() {
               @Override
               public int compare(DispoAnnotationData o1, DispoAnnotationData o2) {
                  return o1.getLocationRefs().compareTo(o2.getLocationRefs());
               }
            });
            for (DispoAnnotationData annotation : annotations) {
               if (annotation.getResolutionType().equals(DispoStrings.Test_Unit_Resolution)) {
                  HashMap<String, String> testNameToPath =
                     DispoUtil.splitTestScriptNameAndPath(Collections.singletonList(annotation));
                  sheetWriter.writeRow(item.getName(), annotation.getLocationRefs(), annotation.getResolutionType(),
                     testNameToPath.keySet(), testNameToPath.values(), annotation.getResolution());
               }
            }
         }
         sheetWriter.endSheet();

         sheetWriter.endWorkbook();
      } catch (Exception ex)

      {
         throw new OseeCoreException(ex);
      }

   }

   public void runCoverageSummaryReport(BranchId branch, DispoSet setPrimary, HashMap<String, Object> jsonObject) {
      Map<String, String> resolutionsValueToText = new HashMap<>();
      Set<CoverageLevel> levelsInSet = new HashSet<>();
      List<CoverageLevel> levelsInList = new ArrayList<>();
      Map<CoverageLevel, Map<String, Pair<WrapInt, WrapInt>>> leveltoUnitToCovered = new HashMap<>();
      for (CoverageLevel level : CoverageLevel.values()) {
         leveltoUnitToCovered.put(level, new HashMap<>());
         levelToTotalCount.put(level, new WrapInt(0));
         levelToCoveredTotalCount.put(level, new WrapInt(0));
      }

      List<DispoItem> items = dispoApi.getDispoItems(branch, setPrimary.getGuid(), true);

      Map<CoverageLevel, Map<String, WrapInt>> levelToResolutionTypesToCount = new HashMap<>();
      DispoConfig config = dispoApi.getDispoConfig(branch);
      config.getValidResolutions();
      for (CoverageLevel level : CoverageLevel.values()) {
         Map<String, WrapInt> innerMap = new HashMap<>();
         for (ResolutionMethod resolutionType : config.getValidResolutions()) {
            innerMap.put(resolutionType.getText(), new WrapInt(0));
            resolutionsValueToText.put(resolutionType.getValue(), resolutionType.getText());
            if (level.equals(
               CoverageLevel.A) && !resolutionType.getValue().isEmpty() && !resolutionType.getValue().equals(
                  "Test_Script")) {
               String levelAResolutionType = String.format("%s/Test_Script", resolutionType.getValue());
               innerMap.put(levelAResolutionType, new WrapInt(0));
               resolutionsValueToText.put(levelAResolutionType, levelAResolutionType);
            }
         }
         // Needed for Level A, each part of a pair can be satisfied by differing resolutions.
         if (level.equals(CoverageLevel.A)) {
            innerMap.put("MIXED", new WrapInt(0));
         }

         levelToResolutionTypesToCount.put(level, innerMap);
      }

      try {
         for (DispoItem item : items) {
            List<DispoAnnotationData> annotations = item.getAnnotationsList();
            Collections.sort(annotations, new Comparator<DispoAnnotationData>() {
               @Override
               public int compare(DispoAnnotationData o1, DispoAnnotationData o2) {
                  return o1.getLocationRefs().compareTo(o2.getLocationRefs());
               }
            });
            for (DispoAnnotationData annotation : annotations) {
               writeRowAnnotationSummary(item, annotation, setPrimary.getName(), levelToResolutionTypesToCount,
                  leveltoUnitToCovered, levelsInSet);
            }
         }

         levelsInList.addAll(levelsInSet);
         Collections.sort(levelsInList);
         String[] row = new String[CoverageLevel.values().length + 1];
         String[] uncoveredRow = new String[CoverageLevel.values().length + 1];

         row[0] = "Total_Count";
         int index = 1;
         // send correct numbers according to level for second param
         Iterator<CoverageLevel> iterator = levelsInList.iterator();
         while (iterator.hasNext()) {
            CoverageLevel lvl = iterator.next();
            Integer totalCount = levelToTotalCount.get(lvl).getValue();
            row[index++] = totalCount.toString();
         }
         addToJsonObject(levelsInList, row, jsonObject);

         row[0] = "All_Coverage_Methods";
         uncoveredRow[0] = "Uncovered";

         index = 1;
         // send correct numbers according to level for second param
         iterator = levelsInList.iterator();
         while (iterator.hasNext()) {
            CoverageLevel lvl = iterator.next();
            Integer coveredCount = levelToCoveredTotalCount.get(lvl).getValue();
            row[index] = coveredCount.toString();
            Integer uncoveredCount =
               levelToTotalCount.get(lvl).getValue() - levelToCoveredTotalCount.get(lvl).getValue();
            uncoveredRow[index++] = uncoveredCount.toString();
         }
         addToJsonObject(levelsInList, row, jsonObject);
         addToJsonObject(levelsInList, uncoveredRow, jsonObject);

         // Try to get Resolution from Level A if available, otherwise get from C
         Set<String> resolutionTypes;
         if (levelsInList.contains(CoverageLevel.A)) {
            resolutionTypes = levelToResolutionTypesToCount.get(CoverageLevel.A).keySet();
         } else if (levelsInList.contains(CoverageLevel.B)) {
            resolutionTypes = levelToResolutionTypesToCount.get(CoverageLevel.B).keySet();
         } else {
            resolutionTypes = levelToResolutionTypesToCount.get(CoverageLevel.C).keySet();
         }

         List<String> orderedResolutionTypes = organizeResolutions(resolutionTypes);
         for (String resolution : orderedResolutionTypes) {
            int index1 = 0;

            row[index1++] =
               resolutionsValueToText.containsKey(resolution) ? resolutionsValueToText.get(resolution) : resolution;

            Iterator<CoverageLevel> it = levelsInList.iterator();
            while (it.hasNext()) {
               CoverageLevel lvl = it.next();
               if (levelToResolutionTypesToCount.get(lvl).get(resolution) == null) {
                  row[index1++] = "ERROR";
                  continue;
               }
               Integer resolutionCount = levelToResolutionTypesToCount.get(lvl).get(resolution).getValue();
               row[index1++] = resolutionCount.toString();
            }
            addToJsonObject(levelsInList, row, jsonObject);
         }

         if (!defaultCases.isEmpty()) {
            Map<CoverageLevel, Integer> levelToMixed = new HashMap<>();
            int index1 = 0;
            row[index1++] = "MIXED - Expanded Below";
            Iterator<CoverageLevel> it = levelsInList.iterator();
            while (it.hasNext()) {
               CoverageLevel lvl = it.next();

               if (levelToResolutionTypesToCount.get(lvl).get("MIXED") == null) {
                  row[index1++] = "ERROR";
                  continue;
               }
               Integer resolutionCount = levelToResolutionTypesToCount.get(lvl).get("MIXED").getValue();
               row[index1++] = resolutionCount.toString();
               levelToMixed.put(lvl, levelToResolutionTypesToCount.get(lvl).get("MIXED").getValue());
            }
            addToJsonObject(levelsInList, row, jsonObject);

            for (Entry<String, Integer> entry : defaultCases.entrySet()) {
               int index2 = 0;
               row[index2++] = entry.getKey();

               while (it.hasNext()) {
                  row[index2++] = entry.getValue().toString();
               }
               addToJsonObject(levelsInList, row, jsonObject);
            }
         }
         mergeEmptyAndUncovered(levelsInList, jsonObject);
      } catch (Exception ex)

      {
         throw new OseeCoreException(ex);
      }

   }

   private void mergeEmptyAndUncovered(List levelsInList, HashMap<String, Object> jsonObject) {
      HashMap<String, String> emptyEntry = (HashMap<String, String>) jsonObject.get(" ");
      HashMap<String, String> uncoveredEntry = (HashMap<String, String>) jsonObject.get("Uncovered");
      Iterator<CoverageLevel> iterator = levelsInList.iterator();
      Integer value;
      while (iterator.hasNext()) {
         CoverageLevel lvl = iterator.next();
         if (lvl.name().equals("A")) {
            value = Integer.parseInt(emptyEntry.get("MCDC")) + Integer.parseInt(uncoveredEntry.get("MCDC"));
            uncoveredEntry.put("MCDC", value.toString());
         } else if (lvl.name().equals("B")) {
            value = Integer.parseInt(emptyEntry.get("Branch")) + Integer.parseInt(uncoveredEntry.get("Branch"));
            uncoveredEntry.put("Branch", value.toString());
         } else if (lvl.name().equals("C")) {
            value = Integer.parseInt(emptyEntry.get("Statement")) + Integer.parseInt(uncoveredEntry.get("Statement"));
            uncoveredEntry.put("Statement", value.toString());
         }
      }
      jsonObject.remove(" ");
   }

   private void addToJsonObject(List<CoverageLevel> levelsInList, String[] row, HashMap<String, Object> jsonObject) {
      HashMap<String, String> coverageEntry = new HashMap<String, String>();
      coverageEntry = createJsonObject(levelsInList, row);
      jsonObject.put(row[0], coverageEntry);
   }

   private HashMap createJsonObject(List levelsInList, String[] value) {
      HashMap<String, String> jsonObject = new HashMap<String, String>();
      Iterator<CoverageLevel> iterator = levelsInList.iterator();
      int index = 1;
      while (iterator.hasNext()) {
         CoverageLevel lvl = iterator.next();
         if (lvl.name().equals("A")) {
            jsonObject.put("MCDC", value[index++]);
         } else if (lvl.name().equals("B")) {
            jsonObject.put("Branch", value[index++]);
         } else if (lvl.name().equals("C")) {
            jsonObject.put("Statement", value[index++]);
         }
      }

      return jsonObject;
   }

   private List<Integer> getDiscrepanciesAsInts(List<String> discrepancyLocations) {
      List<Integer> toReturn = new ArrayList<>();
      for (String location : discrepancyLocations) {
         if (DispoUtil.isNumericLocations(location)) {
            toReturn.add(Integer.valueOf(location));
         } else {
            toReturn = Collections.emptyList();
            break;
         }
      }

      return toReturn;
   }

   private String getPercent(int complete, int total, boolean showZero) {
      if (total == 0 || complete == 0) {
         return getPercentString(0, complete, total, showZero);
      }
      Double percent = Double.valueOf(complete);
      percent = percent / total;
      percent = percent * 100;
      return getPercentString(percent, complete, total, showZero);
   }

   private String getPercentString(double percent, int complete, int total, boolean showZero) {
      if (!showZero && percent == 0.0 && complete == 0) {
         return "0%";
      }
      if (percent == 100.0) {
         return String.format("100%% - %d / %d", complete, total);
      }
      if (percent == 0.0) {
         return String.format("0%% - %d / %d", complete, total);
      }
      return String.format("%2.2f%% - %d / %d", percent, complete, total);
   }

   private void writeRowAnnotation(ExcelXmlWriter sheetWriter, int columns, DispoItem item,
      DispoAnnotationData annotation, String setName, Map<CoverageLevel, Map<String, WrapInt>> levelToResolutionToCount,
      Map<CoverageLevel, Map<String, Pair<WrapInt, WrapInt>>> levelToUnitsToCovered, Set<CoverageLevel> levelsInSet)
      throws IOException {
      String[] row = new String[columns];
      int index = 0;
      row[index++] = getNameSpace(item, setName);
      String unit = getNormalizedName(item.getName());
      row[index++] = unit;
      row[index++] = item.getName().replaceAll(".*\\.", "");
      row[index++] = String.valueOf(item.getMethodNumber());
      row[index++] = String.valueOf(annotation.getLocationRefs());
      String resolutionType = annotation.getResolutionType();
      if (Strings.isValid(resolutionType)) {
         row[index++] = resolutionType;
         String rationale = annotation.getResolution();
         if (resolutionType.equalsIgnoreCase("Test_Script") || !Strings.isValid(rationale)) {
            row[index++] = "N/A";
         } else {
            row[index++] = rationale;
         }
      } else {
         row[index++] = "Uncovered";
         row[index++] = "N/A";
      }

      sheetWriter.writeRow((Object[]) row);

      // location ex. 24, 24.T, 24.A.RESULT
      calculateTotals(levelToResolutionToCount, levelToUnitsToCovered, unit, resolutionType,
         annotation.getLocationRefs(), levelsInSet);
   }

   private void writeRowAnnotationSummary(DispoItem item, DispoAnnotationData annotation, String setName,
      Map<CoverageLevel, Map<String, WrapInt>> levelToResolutionToCount,
      Map<CoverageLevel, Map<String, Pair<WrapInt, WrapInt>>> levelToUnitsToCovered, Set<CoverageLevel> levelsInSet) {
      String unit = getNormalizedName(item.getName());
      String resolutionType = annotation.getResolutionType();
      calculateTotals(levelToResolutionToCount, levelToUnitsToCovered, unit, resolutionType,
         annotation.getLocationRefs(), levelsInSet);
   }

   private void calculateTotals(Map<CoverageLevel, Map<String, WrapInt>> levelToResolutionToCount,
      Map<CoverageLevel, Map<String, Pair<WrapInt, WrapInt>>> levelToUnitsToCovered, String unit, String resolutionType,
      String location, Set<CoverageLevel> levelsInSet) {
      // Determine what level count to increment by location simple number = C, number.T or number.number.RESULT = B, number.number.T = A
      CoverageLevel thisAnnotationsLevel = getLevel(location);

      switch (thisAnnotationsLevel) {
         case A:
            levelsInSet.add(CoverageLevel.A);
            levelsInSet.add(CoverageLevel.B);
            levelsInSet.add(CoverageLevel.C);
            levelToTotalCount.get(thisAnnotationsLevel).inc();
            // Update total covered counts
            uptickCoverage(levelToResolutionToCount.get(thisAnnotationsLevel),
               levelToUnitsToCovered.get(thisAnnotationsLevel), levelToCoveredTotalCount.get(thisAnnotationsLevel),
               unit, resolutionType);
            break;
         case B:
            levelsInSet.add(CoverageLevel.B);
            levelsInSet.add(CoverageLevel.C);
            levelToTotalCount.get(thisAnnotationsLevel).inc();
            uptickCoverage(levelToResolutionToCount.get(thisAnnotationsLevel),
               levelToUnitsToCovered.get(thisAnnotationsLevel), levelToCoveredTotalCount.get(thisAnnotationsLevel),
               unit, resolutionType);
            break;
         case C:
            levelsInSet.add(CoverageLevel.C);
            levelToTotalCount.get(thisAnnotationsLevel).inc();
            uptickCoverage(levelToResolutionToCount.get(thisAnnotationsLevel),
               levelToUnitsToCovered.get(thisAnnotationsLevel), levelToCoveredTotalCount.get(thisAnnotationsLevel),
               unit, resolutionType);
            break;
         default:
            // do nothing
            break;
      }
   }

   private void uptickCoverage(Map<String, WrapInt> resolutionTypeToCount,
      Map<String, Pair<WrapInt, WrapInt>> unitToCovered, WrapInt currentCoveredTotalCount, String unit,
      String resolutionType) {
      WrapInt count = resolutionTypeToCount.get(resolutionType);
      if (Strings.isValid(resolutionType)) {
         if (count == null) {
            resolutionTypeToCount.put(resolutionType, new WrapInt(1));
         } else {
            count.inc();
         }
      }

      Pair<WrapInt, WrapInt> coveredOverTotal = unitToCovered.get(unit);

      int thisUnitsCoveredCount = 0;
      if (Strings.isValid(resolutionType) && !isTypeAnalyze(resolutionType) && !resolutionType.contains("Modify")) {
         thisUnitsCoveredCount = 1;
         currentCoveredTotalCount.inc();
      }
      if (coveredOverTotal == null) {
         Pair<WrapInt, WrapInt> newCount = new Pair<>(new WrapInt(thisUnitsCoveredCount), new WrapInt(1));
         unitToCovered.put(unit, newCount);
      } else {
         coveredOverTotal.getFirst().inc(thisUnitsCoveredCount);
         coveredOverTotal.getSecond().inc();
      }
   }

   private boolean isTypeAnalyze(String resolutionType) {
      if (resolutionType.equals(MODIFY_CODE) || resolutionType.equals(MODIFY_TEST) || resolutionType.equals(
         MODIFY_REQT) || resolutionType.equals(MODIFY_TOOL) || resolutionType.equals(MODIFY_WORK_PRODUCT)) {
         return true;
      }
      return false;
   }

   private CoverageLevel getLevel(String location) {
      if (location.matches(LEVEL_A_LOCATION_PATTERN)) {
         return CoverageLevel.A;
      } else if (location.matches(LEVEL_B_LOCATION_PATTERN) || location.matches(LEVEL_A_SUB_LOCATION_PATTERN)) {
         return CoverageLevel.B;
      } else {
         return CoverageLevel.C;
      }
   }

   private String getNormalizedName(String fullName) {
      if (fullName.contains(".2.ada")) {
         return fullName.replaceAll("\\.2\\.ada.*", ".2.ada");
      } else {
         return fullName.replaceAll("\\.c.*", ".c");
      }
   }

   private String getNameSpace(DispoItem item, String setName) {
      Pattern pattern = Pattern.compile(".*?(\\..*?){1,}\\.2\\.ada");
      Matcher matcher = pattern.matcher(item.getName());
      StringBuilder nameSpace = new StringBuilder(setName);
      if (matcher.find()) {
         String str = matcher.group();
         Pattern pattern2 = Pattern.compile(".*\\.");
         Matcher matcher2 = pattern2.matcher(str.replaceAll("\\.2.*", ""));
         if (matcher2.find()) {
            nameSpace.append(".");
            String toAdd = matcher2.group();
            nameSpace.append(toAdd.substring(0, toAdd.length() - 1));
         }
      }
      return nameSpace.toString();
   }

   private String prettifyAnnotations(List<DispoAnnotationData> annotations) {
      StringBuilder sb = new StringBuilder();

      for (DispoAnnotationData annotation : annotations) {
         sb.append(annotation.getLocationRefs());
         sb.append(":");
         sb.append(annotation.getResolution());
         sb.append("\n");
      }

      return sb.toString();
   }

   private List<String> organizeResolutions(Set<String> resolutionTypes) {
      String[] toRemove = {"Test Script", "MIXED", ""};
      List<String> tempResolutionTypes = new ArrayList<>(resolutionTypes);
      for (String coverageMethod : toRemove) {
         if (tempResolutionTypes.contains(coverageMethod)) {
            tempResolutionTypes.remove(coverageMethod);
         }
      }

      List<String> orderedResolutionTypes = new ArrayList<>();
      for (String coverageMethod : coverageMethodList()) {
         if (tempResolutionTypes.contains(coverageMethod) || coverageMethod.isEmpty()) {
            orderedResolutionTypes.add(coverageMethod);
            tempResolutionTypes.remove(coverageMethod);
         }
      }

      for (String coverageMethod : tempResolutionTypes) {
         orderedResolutionTypes.add(coverageMethod);
      }

      return orderedResolutionTypes;
   }

   private String[] getHeadersDetailed() {
      String[] toReturn = {//
         "Script Name", //
         "Category", //
         "Status", //
         "Total Test Points", //
         "Failures", //
         "Failed Points", //
         "Remaining Count", //
         "Remaining Points", //
         "Assignee", //
         "Team", //
         "Item Notes", //
         "Needs Rerun", //
         "Aborted", //
         "Station", //
         "Elapsed Time", //
         "Creation Date", //
         "Last Updated", //
         "Version", //
         "Dispositions"//
      };
      return toReturn;
   }

   private String[] getHeadersCoverage() {
      String[] toReturn = {//
         "Namespace", //
         "Parent Coverage Unit", //
         "Unit", //
         "Method Number", //
         "Execution Line Number", //
         "Coverage Method", //
         "Coverage Rationale"}; //
      return toReturn;
   }

   private String[] getHeadersSummarySheet() {
      String[] toReturn = {//
         "Unit", //
         "MCDC Pairs Covered", //
         "MCDC Pairs Total",
         "MCDC Pairs % Coverage",
         "Branches Covered", //
         "Branches Total",
         "Branches % Coverage",
         "Statement Lines Covered", //
         "Statement Lines Total", //
         "Statement Lines % Coverage"}; //
      return toReturn;
   }

   //Any changes must also be made in DispoUtil.java
   private String[] coverageMethodList() {
      String[] toReturn = {
         "Test_Script",
         "Defensive_Programming",
         "Exception_Handling",
         "Analysis",
         "Deactivated_Ada_console_command",
         "Deactivated_Code",
         "Deactivated_Compile_Time",
         "Deactivated_Engineering_test_page",
         "Deactivated_EXT_ATE_PRESENT",
         "Deactivated_Ground_testing_only",
         "Deactivated_IN_AIR_OR_ENG_ON",
         "Deactivated_INTEGRITY_serial_console_command",
         "Deactivated_J4_Connector",
         "Deactivated_Remote_SW_test_page",

         "Defensive_Programming/Test_Script",
         "Exception_Handling/Test_Script",
         "Analysis/Test_Script",
         "Deactivated_Ada_console_command/Test_Script",
         "Deactivated_Code/Test_Script",
         "Deactivated_Compile_Time/Test_Script",
         "Deactivated_Engineering_test_page/Test_Script",
         "Deactivated_EXT_ATE_PRESENT/Test_Script",
         "Deactivated_Ground_testing_only/Test_Script",
         "Deactivated_IN_AIR_OR_ENG_ON/Test_Script",
         "Deactivated_INTEGRITY_serial_console_command/Test_Script",
         "Deactivated_J4_Connector/Test_Script",
         "Deactivated_Remote_SW_test_page/Test_Script",

         "Modify_Reqt",
         "Modify_Code",
         "Modify_Test",
         "Modify_Tooling",
         "Modify_Work_Product",

         "Modify_Reqt/Test_Script",
         "Modify_Code/Test_Script",
         "Modify_Test/Test_Script",
         "Modify_Tooling/Test_Script",
         "Modify_Work_Product/Test_Script"};
      return toReturn;
   }
}
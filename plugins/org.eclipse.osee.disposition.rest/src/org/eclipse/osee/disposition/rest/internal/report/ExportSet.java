/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.report;

import static org.eclipse.osee.disposition.model.DispoStrings.ANALYZE_CODE;
import static org.eclipse.osee.disposition.model.DispoStrings.ANALYZE_REQT;
import static org.eclipse.osee.disposition.model.DispoStrings.ANALYZE_TEST;
import static org.eclipse.osee.disposition.model.DispoStrings.ANALYZE_TOOL;
import static org.eclipse.osee.disposition.model.DispoStrings.ANALYZE_WORK_PRODUCT;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

   private final String LEVEL_A_LOCATION_PATTERN = "\\s*\\d+\\s*\\.\\s*\\d+.*?\\.\\s*(T|F)\\s*";
   private final String LEVEL_B_LOCATION_PATTERN = "(.*?RESULT.*|\\s*\\d+\\s*\\.\\s*(T|F).*)";

   //@formatter:off
   private final int FALSE_PRESENT = 1;            // xx01
   private final int TRUE_PRESENT = 2;             // xx10
   private final int BOTH_PRESENT = 3;             // xx11
   private final int FALSE_COVERED = 4;            // 01xx
   private final int TRUE_COVERED = 8;             // 10xx
   private final int BOTH_COVERED = 12;            // 11xx
   //@formatter:on

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

   private class MCDCCoverageData {
      // If we don't like bitFlags we could add 4 boolean fields to represent the bits, T\F path present and T\F path Covered
      private int bitFlag;
      private final Set<String> resolutionTypes;

      MCDCCoverageData(int initFlagValue) {
         bitFlag = initFlagValue;
         resolutionTypes = new HashSet<>();
      }

      private void updateBitFlag(int value) {
         bitFlag += value;
      }

      private void addResolutionType(String resolutionType) {
         resolutionTypes.add(resolutionType);
      }

      private boolean isPairPresent() {
         return (bitFlag & BOTH_PRESENT) == BOTH_PRESENT;
      }

      private boolean isPairCovered() {
         return (bitFlag & BOTH_COVERED) == BOTH_COVERED;
      }

      private String getCoveringResolutionType() {
         if (resolutionTypes.size() == 1) {
            return resolutionTypes.iterator().next();
         } else if (resolutionTypes.size() == 2) {
            String toReturn;
            List<String> typesCopy = new ArrayList<>(resolutionTypes);
            if (typesCopy.remove("Test_Script")) {
               switch (typesCopy.get(0)) {
                  case "Defensive_Programming":
                     toReturn = "Test_Def_Prog";
                     break;
                  case "Exception_Handling":
                     toReturn = "Test_Ex_Handling";
                     break;
                  case "Analysis":
                     toReturn = "Test_Analysis";
                     break;
                  case "Deactivated_EXT_ATE_PRESENT":
                     toReturn = "Test_Deactivated_EXT_ATE_Present";
                     break;
                  case "Deactivated_IN_AIR_OR_ENG_ON":
                     toReturn = "Test_Deactivated_AIR_OR_ENG_ON";
                     break;
                  case "Deactivated_J4_Connector":
                     toReturn = "Test_Deactivated_J4";
                     break;
                  case "Deactivated_Compile_Time":
                     toReturn = "Test_Deactivated_Compile";
                     break;
                  default:
                     toReturn = "MIXED";
               }
            } else {
               toReturn = "MIXED - No Test_Script";
            }
            return toReturn;
         } else {
            return "SHOULD NOT HAVE LANDED HERE";
         }
      }
   }

   public ExportSet(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   public void runDispoReport(BranchId branch, DispoSet setPrimary, String option, OutputStream outputStream) {
      List<DispoItem> items = dispoApi.getDispoItems(branch, setPrimary.getGuid(), true);

      try {
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         String[] headers = getHeadersDetailed();
         int columns = headers.length;
         sheetWriter.startSheet(setPrimary.getName(), headers.length);
         sheetWriter.writeRow((Object[]) headers);

         for (DispoItem item : items) {
            DispoConnector connector = new DispoConnector();
            List<String> allUncoveredDiscprepancies = connector.getAllUncoveredDiscprepancies(item);
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
            row[index++] = String.valueOf(prettifyAnnotations(item.getAnnotationsList()));

            sheetWriter.writeRow((Object[]) row);
         }

         sheetWriter.endSheet();
         sheetWriter.endWorkbook();

      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

   }

   public void runCoverageReport(BranchId branch, DispoSet setPrimary, String option, OutputStream outputStream) {
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
         }
         // Needed for Level A, pairs can but should not have different coverage methods
         innerMap.put("MIXED", new WrapInt(0));
         innerMap.put("Test_Def_Prog", new WrapInt(0));
         innerMap.put("Test_Ex_Handling", new WrapInt(0));
         innerMap.put("Test_Analysis", new WrapInt(0));
         innerMap.put("Test_Deactivated_EXT_ATE_Present", new WrapInt(0));
         innerMap.put("Test_Deactivated _AIR_ENG_ON", new WrapInt(0));
         innerMap.put("Test_Deactivated _J4", new WrapInt(0));
         innerMap.put("Test_Deactivated _Compile", new WrapInt(0));
         levelToResolutionTypesToCount.put(level, innerMap);
      }

      try {
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         String[] headers = getHeadersCoverage();
         int columns = headers.length;
         sheetWriter.startSheet(setPrimary.getName(), headers.length);
         sheetWriter.writeRow((Object[]) headers);

         for (DispoItem item : items) {
            Map<String, MCDCCoverageData> mcdcToCoverageData = new HashMap<>();
            List<DispoAnnotationData> annotations = item.getAnnotationsList();
            for (DispoAnnotationData annotation : annotations) {
               writeRowAnnotation(sheetWriter, columns, item, annotation, setPrimary.getName(),
                  levelToResolutionTypesToCount, leveltoUnitToCovered, mcdcToCoverageData, levelsInSet);
            }
         }

         levelsInList.addAll(levelsInSet);
         Collections.sort(levelsInList);
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
         Object[] row = new String[CoverageLevel.values().length + 1];
         row[0] = "All Coverage Methods";

         int index = 1;
         // send correct numbers according to level for second param
         Iterator<CoverageLevel> iterator = levelsInList.iterator();
         while (iterator.hasNext()) {
            CoverageLevel lvl = iterator.next();
            row[index++] =
               getPercent(levelToCoveredTotalCount.get(lvl).getValue(), levelToTotalCount.get(lvl).getValue(), false);
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

         for (String resolution : resolutionTypes) {
            int index1 = 0;
            row[index1++] =
               resolutionsValueToText.containsKey(resolution) ? resolutionsValueToText.get(resolution) : resolution;

            Iterator<CoverageLevel> it = levelsInList.iterator();
            while (it.hasNext()) {
               CoverageLevel lvl = it.next();
               row[index1++] = getPercent(levelToResolutionTypesToCount.get(lvl).get(resolution).getValue(),
                  levelToTotalCount.get(lvl).getValue(), false);
            }
            sheetWriter.writeRow(row);
         }

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
      Double percent = new Double(complete);
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

   private void writeRowAnnotation(ExcelXmlWriter sheetWriter, int columns, DispoItem item, DispoAnnotationData annotation, String setName, Map<CoverageLevel, Map<String, WrapInt>> levelToResolutionToCount, Map<CoverageLevel, Map<String, Pair<WrapInt, WrapInt>>> levelToUnitsToCovered, Map<String, MCDCCoverageData> mcdcToCoverageData, Set<CoverageLevel> levelsInSet) throws IOException {
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
      calculateTotals(levelToResolutionToCount, levelToUnitsToCovered, unit, resolutionType, mcdcToCoverageData,
         annotation.getLocationRefs(), levelsInSet);
   }

   private void calculateTotals(Map<CoverageLevel, Map<String, WrapInt>> levelToResolutionToCount, Map<CoverageLevel, Map<String, Pair<WrapInt, WrapInt>>> levelToUnitsToCovered, String unit, String resolutionType, Map<String, MCDCCoverageData> mcdcToCoverageData, String location, Set<CoverageLevel> levelsInSet) {
      // Determine what level count to increment by location simple number = C, number.T or number.number.RESULT = B, number.number.T = A
      CoverageLevel thisAnnotationsLevel = getLevel(location);

      switch (thisAnnotationsLevel) {
         case A: {
            levelsInSet.add(CoverageLevel.A);
            // Update total pairs count
            // MCDC pairs have different rules, T and F combo make up one pair so using bitflags
            String mcdcName = getNameFromLocation(location);
            boolean isTruePath = getPathFromLocation(location);
            int mcdcValue = isTruePath ? TRUE_PRESENT : FALSE_PRESENT;

            MCDCCoverageData coverageData = mcdcToCoverageData.get(mcdcName);
            if (coverageData == null) {
               coverageData = new MCDCCoverageData(mcdcValue);
               mcdcToCoverageData.put(mcdcName, coverageData);
            } else {
               coverageData.updateBitFlag(mcdcValue);
            }
            if (coverageData.isPairPresent()) {
               levelToTotalCount.get(thisAnnotationsLevel).inc();
            }

            // Update total covered counts
            uptickA(levelToResolutionToCount.get(thisAnnotationsLevel), levelToUnitsToCovered.get(thisAnnotationsLevel),
               levelToCoveredTotalCount.get(thisAnnotationsLevel), unit, resolutionType, coverageData, mcdcName,
               isTruePath);
         }
            break;
         case B:
            levelsInSet.add(CoverageLevel.B);
         case C: {
            levelsInSet.add(CoverageLevel.C);
            levelToTotalCount.get(thisAnnotationsLevel).inc();

            uptickBorC(levelToResolutionToCount.get(thisAnnotationsLevel),
               levelToUnitsToCovered.get(thisAnnotationsLevel), levelToCoveredTotalCount.get(thisAnnotationsLevel),
               unit, resolutionType);
         }
            break;
         default: {
            // do nothing
         }
      }
   }

   private String getNameFromLocation(String location) {
      return location.replaceAll("\\(.*", "").trim();
   }

   private boolean getPathFromLocation(String location) {
      String[] parts = location.split("\\.");
      String partWithValue = parts[2];
      return partWithValue.trim().equals("T");
   }

   private void uptickA(Map<String, WrapInt> resolutionTypeToCount, Map<String, Pair<WrapInt, WrapInt>> unitToCovered, WrapInt currentCoveredTotalCount, String unit, String resolutionType, MCDCCoverageData coverageData, String mcdcName, boolean isTruePath) {
      int mcdcValue = 0;
      if (Strings.isValid(resolutionType)) {
         coverageData.addResolutionType(resolutionType);
         mcdcValue = isTruePath ? TRUE_COVERED : FALSE_COVERED;
      }
      // safe to add mcdcValue to bitflag since if it's 0 (resolutionType wasn't valid) nothing will change
      coverageData.updateBitFlag(mcdcValue);

      // Uptick Resolution type count
      if (coverageData.isPairCovered()) {
         if (!isTypeAnalyze(resolutionType)) {
            currentCoveredTotalCount.inc();
         }

         WrapInt count = resolutionTypeToCount.get(coverageData.getCoveringResolutionType());
         if (count == null) {
            resolutionTypeToCount.put(resolutionType, new WrapInt(1));
         } else {
            count.inc();
         }
      }

      // uptick unit count
      int amtToIncrementTotal = coverageData.isPairPresent() ? 1 : 0;
      int amtToIncrementCoveredTotal = coverageData.isPairCovered() ? 1 : 0;

      Pair<WrapInt, WrapInt> coveredOverTotal = unitToCovered.get(unit);
      if (coveredOverTotal == null) {
         Pair<WrapInt, WrapInt> newCount =
            new Pair<>(new WrapInt(amtToIncrementCoveredTotal), new WrapInt(amtToIncrementCoveredTotal));
         unitToCovered.put(unit, newCount);
      } else {
         coveredOverTotal.getFirst().inc(amtToIncrementCoveredTotal);
         coveredOverTotal.getSecond().inc(amtToIncrementTotal);
      }
   }

   private void uptickBorC(Map<String, WrapInt> resolutionTypeToCount, Map<String, Pair<WrapInt, WrapInt>> unitToCovered, WrapInt currentCoveredTotalCount, String unit, String resolutionType) {
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
      if (Strings.isValid(resolutionType) && !isTypeAnalyze(resolutionType)) {
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
      if (resolutionType.equals(ANALYZE_CODE) || resolutionType.equals(ANALYZE_TEST) || resolutionType.equals(
         ANALYZE_REQT) || resolutionType.equals(ANALYZE_TOOL) || resolutionType.equals(ANALYZE_WORK_PRODUCT)) {
         return true;
      }
      return false;
   }

   private CoverageLevel getLevel(String location) {
      if (location.matches(LEVEL_A_LOCATION_PATTERN)) {
         return CoverageLevel.A;
      } else if (location.matches(LEVEL_B_LOCATION_PATTERN)) {
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
}

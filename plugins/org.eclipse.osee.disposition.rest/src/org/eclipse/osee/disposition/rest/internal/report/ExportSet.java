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
package org.eclipse.osee.disposition.rest.internal.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
   private int totalStatementCount;
   private int totalCoveredCount;

   public ExportSet(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   public void runReport(BranchId branch, DispoSet setPrimary, String option, OutputStream outputStream) {
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
      totalStatementCount = 0;
      totalCoveredCount = 0;
      List<DispoItem> items = dispoApi.getDispoItems(branch, setPrimary.getGuid(), true);

      Map<String, Integer> resolutionToCount = new HashMap<>();
      Map<String, Pair<Integer, Integer>> unitToCovered = new HashMap<>();
      DispoConfig config = dispoApi.getDispoConfig(branch);
      config.getValidResolutions();
      for (ResolutionMethod resolution : config.getValidResolutions()) {
         resolutionToCount.put(resolution.getText(), 0);
      }

      try {
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         String[] headers = getHeadersCoverage();
         int columns = headers.length;
         sheetWriter.startSheet(setPrimary.getName(), headers.length);
         sheetWriter.writeRow((Object[]) headers);

         for (DispoItem item : items) {
            List<DispoAnnotationData> annotations = item.getAnnotationsList();
            for (DispoAnnotationData annotation : annotations) {
               writeRowAnnotation(sheetWriter, columns, item, annotation, setPrimary.getName(), resolutionToCount,
                  unitToCovered, totalStatementCount);
            }
         }

         sheetWriter.endSheet();

         // Write Cover Sheet
         sheetWriter.startSheet("Cover Sheet", headers.length);
         Object[] coverSheetHeaders = {" ", setPrimary.getName()};
         sheetWriter.writeRow(coverSheetHeaders);
         Object[] row = new String[2];
         row[0] = "All Coverage Methods";
         row[1] = getPercent(totalCoveredCount, totalStatementCount, false);
         sheetWriter.writeRow(row);
         for (String resolution : resolutionToCount.keySet()) {
            row[0] = resolution;
            row[1] = getPercent(resolutionToCount.get(resolution), totalStatementCount, false);
            sheetWriter.writeRow(row);
         }
         sheetWriter.endSheet();

         // Write Summary Sheet
         Object[] summarySheetHeaders = {"Unit", "Lines Covered", "Total Lines", "Percent Coverage"};
         sheetWriter.startSheet("Summary Sheet", summarySheetHeaders.length);
         sheetWriter.writeRow(summarySheetHeaders);
         Object[] row2 = new String[4];
         for (String unit : unitToCovered.keySet()) {
            row2[0] = unit;
            Pair<Integer, Integer> coveredOverTotal = unitToCovered.get(unit);
            int covered = coveredOverTotal.getFirst();
            int total = coveredOverTotal.getSecond();
            row2[1] = String.valueOf(covered);
            row2[2] = String.valueOf(total);
            Double percent = (double) covered / total * 100;
            row2[3] = String.format("%2.2f%%", percent);
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

   private void writeRowAnnotation(ExcelXmlWriter sheetWriter, int columns, DispoItem item, DispoAnnotationData annotation, String setName, Map<String, Integer> resolutionToCount, Map<String, Pair<Integer, Integer>> unitToCovered, Integer totalNumber) throws IOException {
      totalStatementCount++;

      String[] row = new String[columns];
      int index = 0;
      row[index++] = getNameSpace(item, setName);
      String unit = getNormalizedName(item.getName());
      row[index++] = unit;
      row[index++] = item.getName().replaceAll(".*\\.", "");
      row[index++] = String.valueOf(item.getMethodNumber());
      row[index++] = String.valueOf(annotation.getLocationRefs());
      String coverageMethod = annotation.getResolutionType();
      if (Strings.isValid(coverageMethod)) {
         row[index++] = coverageMethod;
         String rationale = annotation.getResolution();
         if (coverageMethod.equalsIgnoreCase("Test_Script") || !Strings.isValid(rationale)) {
            row[index++] = "N/A";
         } else {
            row[index++] = rationale;
         }
      } else {
         row[index++] = "Uncovered";
         row[index++] = "N/A";
      }

      sheetWriter.writeRow((Object[]) row);

      // Update Coverage Resolution Count
      Integer count = resolutionToCount.get(coverageMethod);
      if (Strings.isValid(coverageMethod)) {
         if (count == null) {
            resolutionToCount.put(coverageMethod, 1);
         } else {
            resolutionToCount.put(coverageMethod, ++count);
         }
      }

      // Update
      Pair<Integer, Integer> coveredOverTotal = unitToCovered.get(unit);
      int coveredCount;
      if (Strings.isValid(coverageMethod)) {
         coveredCount = 1;
         totalCoveredCount++;
      } else {
         coveredCount = 0;
      }
      if (coveredOverTotal == null) {
         Pair<Integer, Integer> newCount = new Pair<>(coveredCount, 1);
         unitToCovered.put(unit, newCount);
      } else {
         Integer currentCovered = coveredOverTotal.getFirst();
         Integer currentTotal = coveredOverTotal.getSecond();
         Pair<Integer, Integer> newCount = new Pair<>(currentCovered + coveredCount, ++currentTotal);
         unitToCovered.put(unit, newCount);
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

   private static String prettifyAnnotations(List<DispoAnnotationData> annotations) {
      StringBuilder sb = new StringBuilder();

      for (DispoAnnotationData annotation : annotations) {
         sb.append(annotation.getLocationRefs());
         sb.append(":");
         sb.append(annotation.getResolution());
         sb.append("\n");
      }

      return sb.toString();
   }

   private static String[] getHeadersDetailed() {
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

   private static String[] getHeadersCoverage() {
      String[] toReturn = {//
         "Namespace", //
         "Parent Coverage Unit", //
         "Unit", //
         "Method Number", //
         "Execution Line Number", //
         "Coverage Method", //
         "Coverage Rationale"}; //
      //         "Text"};
      return toReturn;
   }
}

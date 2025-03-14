/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.disposition.rest.internal.importer.coverage;

import static org.eclipse.osee.disposition.model.DispoStrings.Exception_Handling_Resolution;
import static org.eclipse.osee.disposition.model.DispoStrings.Test_Unit_Resolution;
import static org.eclipse.osee.disposition.model.DispoSummarySeverity.ERROR;
import static org.eclipse.osee.disposition.model.DispoSummarySeverity.WARNING;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.DispoSummarySeverity;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.DispoApiConfiguration;
import org.eclipse.osee.disposition.rest.DispoImporterApi;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.DispoDataFactory;
import org.eclipse.osee.disposition.rest.internal.importer.DispoSetCopier;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.vcast.VCastClient;
import org.eclipse.osee.vcast.VCastDataStore;
import org.eclipse.osee.vcast.VCastLisFileParser;
import org.eclipse.osee.vcast.VCastValidateDatFileSyntax;
import org.eclipse.osee.vcast.model.VCastFunction;
import org.eclipse.osee.vcast.model.VCastInstrumentedFile;
import org.eclipse.osee.vcast.model.VCastMcdcCoveragePairRow;
import org.eclipse.osee.vcast.model.VCastResult;
import org.eclipse.osee.vcast.model.VCastSourceFileJoin;
import org.eclipse.osee.vcast.model.VCastStatementCoverage;

/**
 * @author Angel Avila
 */
public class LisFileParser implements DispoImporterApi {
   private static final String RESULTS = "results";
   private static final String IMPORTED_RESULTS = "IMPORTED_RESULTS";
   private static final String LOG = "\\s*(log).*";
   private static final String EXIT_WHEN = "^(?i)(.*\\bEXIT WHEN\\b\\s*[^:]*$)";
   private static final String MCDC_BRANCH_TF_CONDITIONS = "^(?i)(.*\\b(IF|ELSIF|WHILE|EXIT.*WHEN)\\b\\s*[^:]*$)";
   private static final String BRANCH_TF_CONDITIONS = "^(?i)(.*\\bFOR\\b.*)";
   private static final String BRANCH_T_CONDITION = "^(?i)(?!.*\\bEXIT\\b).*\\b(WHEN|BEGIN)\\b.*";
   private static final String WHILE_ONE = "(.*\\b(WHILE|while)\\b\\s*\\(1\\).*)";
   private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

   private final DispoDataFactory dataFactory;

   private static final Pattern fileMethod5LineNumberPattern =
      Pattern.compile("\\s*([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)");
   private static final Pattern fileMethod3LineNumberPattern = Pattern.compile("\\s*([0-9]+)\\s+([0-9]+)\\s+([0-9]+)");
   private final static Pattern fileMethod4LineNumberPlusTokenPattern =
      Pattern.compile("\\s*([0-9]+)\\s+([0-9]+)\\s+([0-9]+)\\s+(T|F)");
   private final Map<String, DispoItemData> datIdToItem = new HashMap<>();
   private final Set<String> datIdsCoveredByException = new HashSet<>();
   private final Set<String> alreadyUsedDatIds = new HashSet<>();
   private final Set<String> alreadyUsedFileNames = new HashSet<>();

   private final DispoConnector dispoConnector;
   private final DispoApiConfiguration config;

   private String vCastDir;

   public LisFileParser(Log logger, DispoDataFactory dataFactory, DispoApiConfiguration config, DispoConnector connector) {
      this.dataFactory = dataFactory;
      this.config = config;
      this.dispoConnector = connector;
   }

   @Override
   public List<DispoItem> importDirectory(Map<String, DispoItem> exisitingItems, File filesDir, OperationReport report,
      Log logger, ArtifactReadable programConfig) {

      //Get information from cover.db file in vcast output
      vCastDir = filesDir.getAbsolutePath();
      File f = new File(vCastDir + File.separator + "cover.db");
      if (!f.exists()) {
         vCastDir = filesDir.getAbsolutePath() + File.separator + "vcast";
         f = new File(vCastDir + File.separator + "cover.db");
      }
      VCastDataStore dataStore = VCastClient.newDataStore(f.getAbsolutePath(), logger);

      dataStore.setIsMCDC();
      dataStore.setIsBranch();

      Collection<VCastInstrumentedFile> instrumentedFiles = getInstrumentedFiles(dataStore, report);

      HashMap<String, File> nameToFileMap = createNameToFileMap(report);

      //Query cover.db database based on instrumented LIS files
      for (VCastInstrumentedFile instrumentedFile : instrumentedFiles) {
         processInstrumented(dataStore, instrumentedFile, nameToFileMap, report);
      }

      Collection<VCastResult> results = getResultFiles(dataStore);
      for (VCastResult result : results) {
         try {
            processResults(result, exisitingItems, report);
         } catch (Exception ex) {
            report.addEntry("FAILURE", "VCast Error", ERROR);
         }
      }

      processExceptionHandled(exisitingItems, report);

      return createItems(exisitingItems, report, programConfig);
   }

   private HashMap<String, File> createNameToFileMap(OperationReport report) {
      HashMap<String, File> fileNameToFileMap = new HashMap<>();
      File vcastDir = new File(vCastDir);
      FilenameFilter filter = new FilenameFilter() {

         @Override
         public boolean accept(File dir, String name) {
            return name.endsWith(".lis") || name.endsWith(".LIS");
         }
      };

      File[] lisFiles = vcastDir.listFiles(filter);
      if (lisFiles != null) {
         for (File file : lisFiles) {
            String fileNameLowerCase = normalizeLisFileName(file.getName());
            if (fileNameToFileMap.containsKey(fileNameLowerCase)) {
               report.addEntry("DIRECTORY", String.format("Collision with file name: %s", fileNameLowerCase), ERROR);
            } else {
               fileNameToFileMap.put(fileNameLowerCase, file);
            }
         }
      }
      return fileNameToFileMap;
   }

   private List<DispoItem> createItems(Map<String, DispoItem> exisitingItems, OperationReport report,
      ArtifactReadable programConfig) {
      List<DispoItem> toReturn;
      Collection<DispoItemData> items = datIdToItem.values();

      for (DispoItemData item : items) {
         dataFactory.initDispoItem(item);

         int validAnnotationCount = 0;

         for (DispoAnnotationData annotationData : item.getAnnotationsList()) {
            if (annotationData.getIsDefault() && !annotationData.getIsPairAnnotation()) {
               validAnnotationCount++;
            }
         }
         item.setTotalPoints(String.valueOf(validAnnotationCount + item.getDiscrepanciesList().size()));
      }

      if (!exisitingItems.isEmpty()) {
         // This is a reimport so we'll need to copy all the annotations
         toReturn = runCopier(exisitingItems, report, items);
      } else {
         toReturn = new ArrayList<>();
         toReturn.addAll(items);
      }

      boolean clearCoverage = false;
      if (programConfig.isValid()) {
         clearCoverage = programConfig.getSoleAttributeValue(CoreAttributeTypes.ClearCoverage, false);
      }

      for (DispoItem item : toReturn) {
         createPlaceHolderAnnotations((DispoItemData) item, exisitingItems, report, clearCoverage);
      }
      return toReturn;
   }

   private List<DispoItem> runCopier(Map<String, DispoItem> exisitingItems, OperationReport report,
      Collection<DispoItemData> items) {
      List<DispoItem> toReturn;
      DispoSetCopier copier = new DispoSetCopier(dispoConnector);
      List<DispoItemData> itemsFromImport = new ArrayList<>();
      itemsFromImport.addAll(items);

      Map<String, Set<DispoItemData>> namesToDestItems = new HashMap<>();
      for (DispoItemData item : itemsFromImport) {
         String name = item.getName();
         Set<DispoItemData> itemsWithSameName = namesToDestItems.get(name);
         if (itemsWithSameName == null) {
            Set<DispoItemData> set = new HashSet<>();
            set.add(item);
            namesToDestItems.put(name, set);
         } else {
            itemsWithSameName.add(item);
            namesToDestItems.put(name, itemsWithSameName);
         }
      }

      toReturn = copier.copyAllDispositionsSameSet(namesToDestItems, exisitingItems.values(), false, null, report);
      return toReturn;
   }

   private void createPlaceHolderAnnotations(DispoItemData item, Map<String, DispoItem> exisitingItems,
      OperationReport report, boolean clearCoverage) {

      List<DispoItem> prevItems = new ArrayList<DispoItem>(exisitingItems.values());
      List<String> uncovered = dispoConnector.getAllUncoveredDiscrepancies(item);
      List<String> discrepanciesToRemove = new ArrayList<>();

      DispoItem prevItem = getOldItem(prevItems, item);

      if (!uncovered.isEmpty()) {
         Map<String, Discrepancy> discrepanciesList = item.getDiscrepanciesList();
         for (String id : discrepanciesList.keySet()) {
            Discrepancy discrepancy = discrepanciesList.get(id);
            if (discrepancy.getLocation().endsWith(").T") || discrepancy.getLocation().endsWith(").F")) {
               discrepanciesToRemove.add(id);
               continue;
            }
            if (uncovered.contains(discrepancy.getLocation())) {
               DispoAnnotationData uncoveredAnnotation = getOldAnnotByDisc(prevItem, discrepancy);
               if (uncoveredAnnotation.isValid()) {
                  if (clearCoverage && (uncoveredAnnotation.getResolutionType().equalsIgnoreCase(
                     DispoStrings.Test_Unit_Resolution) || uncoveredAnnotation.getResolutionType().equalsIgnoreCase(
                        Exception_Handling_Resolution))) {
                     if (!uncoveredAnnotation.getLastManualResolutionType().isEmpty()) {
                        addAnnotationForPreviouslyCoveredLine(item, discrepancy.getLocation(), discrepancy.getText(),
                           uncoveredAnnotation);
                     } else {
                        addBlankAnnotationForUncoveredLine(item, discrepancy.getLocation(), discrepancy.getText(),
                           uncoveredAnnotation);
                     }
                  } else {
                     keepExistingAnnotation(item, uncoveredAnnotation);
                  }
               } else {
                  addBlankAnnotationForUncoveredLine(item, discrepancy.getLocation(), discrepancy.getText(),
                     uncoveredAnnotation);
               }
            }
         }
         for (String id : discrepanciesToRemove) {
            discrepanciesList.remove(id);
            item.setDiscrepanciesList(discrepanciesList);
         }
      }
   }

   private void processExceptionHandled(Map<String, DispoItem> exisitingItems, OperationReport report) {
      for (String datId : datIdsCoveredByException) {
         Matcher matcher = Pattern.compile("\\d*:\\d*:").matcher(datId);
         matcher.find();
         String itemDatId = matcher.group();
         DispoItemData item = datIdToItem.get(itemDatId);
         String line = datId.replaceAll("\\d*:\\d*:", "");
         line = line.replaceAll(":", "");
         String text = "";

         String lastManualResolutionType = "";
         String lastManualResolution = "";
         DispoItem prevItem = getOldItem(new ArrayList<DispoItem>(exisitingItems.values()), item);

         if (line.endsWith("RESULT")) {
            String lineF = line + ".F";
            Discrepancy matchingDiscrepancyF = matchDiscrepancy(lineF, item.getDiscrepanciesList());
            if (matchingDiscrepancyF != null) {
               text = matchingDiscrepancyF.getText();
               Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();
               discrepancies.remove(matchingDiscrepancyF.getId());

               DispoAnnotationData prevAnnotation = getOldAnnotByDisc(prevItem, matchingDiscrepancyF);
               if (prevAnnotation.isValid() && !prevAnnotation.getResolutionType().equalsIgnoreCase(
                  DispoStrings.Test_Unit_Resolution) && !prevAnnotation.getResolutionType().equalsIgnoreCase(
                     DispoStrings.Exception_Handling_Resolution)) {
                  lastManualResolutionType = prevAnnotation.getResolutionType();
                  lastManualResolution = prevAnnotation.getResolution();
               }

               addAnnotationForCoveredLine(item, lineF, Exception_Handling_Resolution, "", text,
                  lastManualResolutionType, lastManualResolution);

            }
            line = line + ".T";
         }
         Discrepancy matchingDiscrepancy = matchDiscrepancy(line, item.getDiscrepanciesList());
         if (matchingDiscrepancy != null) {

            DispoAnnotationData prevAnnotation = getOldAnnotByDisc(prevItem, matchingDiscrepancy);
            if (prevAnnotation != DispoAnnotationData.SENTINEL && !prevAnnotation.getResolutionType().equalsIgnoreCase(
               DispoStrings.Test_Unit_Resolution) && !prevAnnotation.getResolutionType().equalsIgnoreCase(
                  DispoStrings.Exception_Handling_Resolution)) {
               lastManualResolutionType = prevAnnotation.getResolutionType();
               lastManualResolution = prevAnnotation.getResolution();
            }

            text = matchingDiscrepancy.getText();
            Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();
            if (matchingDiscrepancy.getPairAnnotations() != null && !matchingDiscrepancy.getPairAnnotations().isEmpty() && !matchingDiscrepancy.getIsOverloadedCondition()) {
               boolean isMcdcCovered = addAnnotationForCoveredMcdcLine(item, line, Exception_Handling_Resolution, "",
                  text, matchingDiscrepancy.getPairAnnotations(), lastManualResolutionType, lastManualResolution,
                  prevItem);
               if (isMcdcCovered) {
                  discrepancies.remove(matchingDiscrepancy.getId());
               }
            } else if (matchingDiscrepancy.getIsOverloadedCondition()) {
               addAnnotationForOverloadedMcdcLine(item, line, text, matchingDiscrepancy.getDevNotes(),
                  Exception_Handling_Resolution, "", lastManualResolutionType, lastManualResolution);
            } else {
               addAnnotationForCoveredLine(item, line, Exception_Handling_Resolution, "", text,
                  lastManualResolutionType, lastManualResolution);
               discrepancies.remove(matchingDiscrepancy.getId());
            }
         }
      }
   }

   private Collection<VCastInstrumentedFile> getInstrumentedFiles(VCastDataStore dataStore, OperationReport report) {
      Collection<VCastInstrumentedFile> instrumentedFiles = new ArrayList<>();
      try {
         /**
          * Note: the LIS_file field of the instrumentedFiles may have a fictitious absolute path - but the path is
          * ignored and only the file name is used.
          */
         instrumentedFiles = dataStore.getAllInstrumentedFiles();
      } catch (OseeCoreException ex) {
         report.addEntry("SQL", String.format("SQL error while reading functions for directory: [%s]", vCastDir),
            ERROR);
      }
      return instrumentedFiles;
   }

   private void processInstrumented(VCastDataStore dataStore, VCastInstrumentedFile instrumentedFile,
      HashMap<String, File> nameToFileMap, OperationReport report) {
      VCastSourceFileJoin sourceFileJoin = null;

      try {
         sourceFileJoin = dataStore.getSourceFileJoin(instrumentedFile);
      } catch (OseeCoreException ex) {
         report.addEntry("SQL",
            String.format("SQL error while reading source_files for instrumented_file id: [%s]. Error Message: [%s]",
               instrumentedFile.getId(), ex.getMessage()),
            ERROR);
      }

      if (sourceFileJoin != null) {
         String lisFileNameFullPath = instrumentedFile.getLISFile();
         if (!Strings.isValid(lisFileNameFullPath)) {
            report.addEntry("SQL",
               String.format(
                  "Error: instrumented_file has invalid LIS_file value.  ID:(" + instrumentedFile.getId() + ")"),
               ERROR);
         }
         String normalizedName = normalizeLisFileName(lisFileNameFullPath);
         File file = nameToFileMap.get(normalizedName);
         if (file != null) {
            VCastLisFileParser lisFileParser = new VCastLisFileParser(file);
            try {
               lisFileParser.loadFileText();
            } catch (IOException ex1) {
               report.addEntry("VCast", String.format("Could not load file: %s", normalizedName), ERROR);
            }

            Collection<VCastFunction> functions = Collections.emptyList();
            try {
               functions = dataStore.getFunctions(instrumentedFile);
            } catch (OseeCoreException ex) {
               report.addEntry("SQL",
                  String.format("SQL error while reading functions for instrumented_file id: [%s]. Error Message: [%s]",
                     instrumentedFile.getId(), ex.getMessage()),
                  ERROR);
            }

            for (VCastFunction function : functions) {
               processFunction(instrumentedFile, sourceFileJoin, lisFileParser, dataStore, function,
                  dataStore.getIsMCDC(), report);
            }
         } else {
            report.addEntry("VCast", String.format("Could not find file: %s", normalizedName), ERROR);
         }
      }
   }

   private void processFunction(VCastInstrumentedFile instrumentedFile, VCastSourceFileJoin sourceFileJoin,
      VCastLisFileParser lisFileParser, VCastDataStore dataStore, VCastFunction function, boolean isMCDCFile,
      OperationReport report) {
      int functionNum = function.getFindex();
      String itemName = "";
      DispoItemData newItem = new DispoItemData();
      newItem.setAnnotationsList(new ArrayList<DispoAnnotationData>());
      int sourceFileNum = sourceFileJoin.getUnitIndex();
      String sourceFilePath = sourceFileJoin.getPath();

      Objects.requireNonNull(sourceFileJoin, "sourceFileJoin can not be null");
      itemName = sourceFileJoin.getDisplayName() + "." + function.getName();
      newItem.setName(itemName);
      newItem.setFileNumber(Integer.toString(sourceFileNum));
      newItem.setSourceFilePath(sourceFilePath);
      newItem.setMethodNumber(Integer.toString(functionNum));

      try {
         String fullPathToFile = vCastDir + File.separator + instrumentedFile.getLISFile();
         Date lastModified = DispoUtil.getTimestampOfFile(fullPathToFile);
         newItem.setLastUpdate(lastModified);
      } catch (Throwable ex) {
         report.addEntry("Get Timestamp of File",
            String.format("Error retrieving the timestamp for [%s]. Error Message: [%s]", instrumentedFile.getId(),
               ex.getMessage()),
            ERROR);
      }

      String datId = generateDatId(sourceFileNum, functionNum);

      datIdToItem.put(datId, newItem);

      checkForMultiEnvRename(sourceFileNum, instrumentedFile, newItem);

      Collection<VCastStatementCoverage> statementCoverageItems = Collections.emptyList();
      try {
         statementCoverageItems = dataStore.getStatementCoverageLines(function);
      } catch (OseeCoreException ex) {
         report.addEntry("SQL", String.format(
            "SQL error while reading statement_coverages for instrumented_file id: [%s] and function id: [%s]. Error Message: [%s]",
            instrumentedFile.getId(), function.getId(), ex.getMessage()), ERROR);
      }
      Map<String, Discrepancy> discrepancies = new HashMap<>();

      for (VCastStatementCoverage statementCoverageItem : statementCoverageItems) {
         processStatement(instrumentedFile, lisFileParser, sourceFileNum, functionNum, function, statementCoverageItem,
            isMCDCFile, discrepancies, report);
      }

      // add discrepancies to item
      newItem.setDiscrepanciesList(discrepancies);

      //for mcdc, add discrepancies for pairs.
   }

   private void checkForMultiEnvRename(int fileNum, VCastInstrumentedFile instrumentedFile, DispoItemData newItem) {
      String regexWithSeparator = String.format(".*?%svcast%s.*?\\d+\\.2\\.lis", File.separator, File.separator);
      if (instrumentedFile.getLISFile().matches(regexWithSeparator)) {
         // Making assumption here that the only time we wanna collect these duplicate "twin" files is when vcast tags them with the name
         // syntax filename.id.2.lis
         String nameWithoutId = newItem.getName();
         String regex = "\\.2\\.";
         String nameWithId = nameWithoutId.replaceAll(regex, String.format(".%s.2.", fileNum));
         newItem.setName(nameWithId);
      }
   }

   private void processStatement(VCastInstrumentedFile lisFile, VCastLisFileParser lisFileParser, int fileNum,
      int functionNum, VCastFunction function, VCastStatementCoverage statementCoverageItem, boolean isMCDCFile,
      Map<String, Discrepancy> discrepancies, OperationReport report) {
      // Create discrepancy for every line, annotate with test unit or exception handled
      Integer functionNumber = function.getFindex();
      Integer lineNumber = statementCoverageItem.getLine();
      Pair<String, Boolean> lineData = null;

      try {
         lineData = lisFileParser.getSourceCodeForLine(functionNumber, lineNumber);
      } catch (Exception ex) {
         report.addEntry("SQL",
            String.format("Issue getting source code line [%s], [%s]", lisFile.getLISFile(), ex.getMessage()), ERROR);
      }
      String location = "";
      if (lineData != null) {
         boolean isMCDCPair = statementCoverageItem.getIsMCDCPair();
         String text;
         if (isMCDCPair) {
            location = String.format("%s.%s", lineNumber, statementCoverageItem.getAbbrevCondition());
            if (lineData.getFirst().matches(MCDC_BRANCH_TF_CONDITIONS)) {

               text = String.format("%s %s", lineData.getFirst().trim(), statementCoverageItem.getFullCondition());
               int numberOfConditions = statementCoverageItem.getNumConditions();
               int conditionIndex = statementCoverageItem.getCondIndex();
               if (conditionIndex > 0) {
                  ArrayList<VCastMcdcCoveragePairRow> coverageRows = statementCoverageItem.getCoverageRows();
                  Map<Integer, DispoAnnotationData> pairAnnotations = new HashMap<>();
                  if (numberOfConditions <= 8) {

                     //Table of Row, Condition, Truth Value
                     Table<Integer, Integer, Boolean> truthTable = mcdcTruthTable(numberOfConditions);

                     //Map of Row, Successful Pair for the current condition.
                     Multimap<Integer, Integer> allPairs = determineMcdcPairs(truthTable, coverageRows, conditionIndex);

                     for (VCastMcdcCoveragePairRow coverageRow : coverageRows) {
                        int rowValue = coverageRow.getRowValue();
                        if (!allPairs.containsKey(rowValue)) {
                           continue;
                        }
                        boolean isRowCovered = coverageRow.getMaxHitCount() > 0;
                        String truthRowText = "";
                        for (Map.Entry<Integer, Boolean> truthRow : truthTable.row(rowValue).entrySet()) {
                           truthRowText = String.format("%s%s=%b  ", truthRowText,
                              LETTERS.charAt(truthRow.getKey() - 1), truthRow.getValue());
                        }
                        if (coverageRow.getRowResult() == 1) {
                           truthRowText = String.format("%sResult=%b", truthRowText, true);
                        } else {
                           truthRowText = String.format("%sResult=%b", truthRowText, false);
                        }
                        pairAnnotations.put(rowValue, createPairAnnotation(location + "." + rowValue, truthRowText,
                           isRowCovered, rowValue, allPairs.get(rowValue)));
                     }
                     addMcdcDiscrepancy(discrepancies, location, text, pairAnnotations, "", false);
                  } else {
                     String overloadedText = String.format("", numberOfConditions);
                     addMcdcDiscrepancy(discrepancies, location, text, pairAnnotations, overloadedText, true);
                  }
               }
            } else if (lineData.getFirst().matches(BRANCH_TF_CONDITIONS)) {
               text = String.format("%s %s", lineData.getFirst().trim(), statementCoverageItem.getFullCondition());
               addDiscrepancy(discrepancies, location + ".T", text);
               addDiscrepancy(discrepancies, location + ".F", text);
            } else {
               text = String.format("%s %s", lineData.getFirst().trim(), statementCoverageItem.getFullCondition());
               addDiscrepancy(discrepancies, location + ".T", text);
            }
         } else {
            text = lineData.getFirst().trim();
            if (statementCoverageItem.getNumConditions() == 2 && (lineData.getFirst().matches(
               MCDC_BRANCH_TF_CONDITIONS) || lineData.getFirst().matches(BRANCH_TF_CONDITIONS))) {
               location = String.format("%s.%s", lineNumber, "T");
               String locationF = String.format("%s.%s", lineNumber, "F");
               addDiscrepancy(discrepancies, location, text);
               addDiscrepancy(discrepancies, locationF, text);
            } else if (statementCoverageItem.getNumConditions() == 1 && (lineData.getFirst().matches(
               BRANCH_T_CONDITION))) {
               location = String.format("%s.%s", lineNumber, "T");
               addDiscrepancy(discrepancies, location, text);
            } else {
               location = String.valueOf(lineNumber);
               addDiscrepancy(discrepancies, location, text);
            }
         }
         if (lineData.getSecond()) {
            String datId = generateDatId(fileNum, functionNum, location);
            datIdsCoveredByException.add(datId);
         }
      }
   }

   private Table<Integer, Integer, Boolean> mcdcTruthTable(int numberOfConditions) {
      Table<Integer, Integer, Boolean> truthTable = TreeBasedTable.create();
      ArrayList<Integer> current = new ArrayList<>();
      ArrayList<Integer> holding = new ArrayList<>();
      current.add(numberOfConditions);
      int rowNumber = 1;
      for (int i = 1; i <= numberOfConditions; i++) {
         truthTable.put(rowNumber, i, true);
      }
      rowNumber++;
      return mcdcTruthTableRec(truthTable, current, holding, numberOfConditions, rowNumber);
   }

   private Table<Integer, Integer, Boolean> mcdcTruthTableRec(Table<Integer, Integer, Boolean> truthTable,
      List<Integer> current, List<Integer> holding, int numberOfConditions, int rowNumber) {

      try {
         if (current.contains(0) || current.isEmpty()) {
            return truthTable;
         }

         for (int i = 1; i <= numberOfConditions; i++) {
            truthTable.put(rowNumber, i, true);
         }

         if (holding.isEmpty()) {
            for (int i : current) {
               truthTable.put(rowNumber, i, false);
            }
            rowNumber++;
            int prevCondition = current.get(0) - 1;
            holding.addAll(current);
            current.clear();
            current.add(prevCondition);
            return mcdcTruthTableRec(truthTable, current, holding, numberOfConditions, rowNumber);
         }
         Collections.sort(current);
         Collections.sort(holding);
         int currMax = current.get(current.size() - 1);
         int holdMax = holding.get(holding.size() - 1);

         if (currMax < holdMax) {
            for (int i : current) {
               truthTable.put(rowNumber, i, false);
            }
            rowNumber++;
            current.add(holdMax);
            holding.removeAll(Arrays.asList(holdMax));
            return mcdcTruthTableRec(truthTable, current, holding, numberOfConditions, rowNumber);
         }
         ArrayList<Integer> toHold = new ArrayList<>();
         if (currMax > holdMax) {
            for (int i : current) {
               truthTable.put(rowNumber, i, false);
            }
            for (int i : current) {
               if (i > holdMax) {
                  holding.removeAll(Arrays.asList(holdMax));
                  toHold.add(i);
               }
            }
            rowNumber++;
            holding.addAll(toHold);
            current.removeAll(toHold);
            current.add(holdMax);
            return mcdcTruthTableRec(truthTable, current, holding, numberOfConditions, rowNumber);
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      return truthTable;
   }

   private Multimap<Integer, Integer> determineMcdcPairs(Table<Integer, Integer, Boolean> truthTable,
      ArrayList<VCastMcdcCoveragePairRow> pairRows, int conditionIndex) {

      Multimap<Integer, Integer> pairs = ArrayListMultimap.create();

      for (VCastMcdcCoveragePairRow pairRowSideA : pairRows) {
         int sideARow = pairRowSideA.getRowValue();
         int sideARowResult = pairRowSideA.getRowResult();
         Map<Integer, Boolean> sideAConditionBools = truthTable.row(sideARow);
         for (VCastMcdcCoveragePairRow pairRowSideB : pairRows) {
            int sideBRow = pairRowSideB.getRowValue();
            int sideBRowResult = pairRowSideB.getRowResult();
            Map<Integer, Boolean> sideBConditionBools = truthTable.row(sideBRow);
            if (sideARowResult == sideBRowResult || sideAConditionBools.get(conditionIndex) == sideBConditionBools.get(
               conditionIndex)) {
               continue;
            }
            boolean singleConditionFlip = true;
            for (int key : sideAConditionBools.keySet()) {
               if (key == conditionIndex) {
                  continue;
               }
               boolean sideAConditionValue = sideAConditionBools.get(key);
               boolean sideBConditionValue = sideBConditionBools.get(key);

               if (sideAConditionValue != sideBConditionValue) {
                  singleConditionFlip = false;
                  break;
               }
            }
            if (singleConditionFlip) {
               pairs.put(sideARow, sideBRow);
            }
         }
      }
      return pairs;
   }

   private void addDiscrepancy(Map<String, Discrepancy> discrepancies, String location, String text) {
      Discrepancy newDiscrepancy = new Discrepancy();
      newDiscrepancy.setLocation(location);
      newDiscrepancy.setText(text);
      String id = String.valueOf(Lib.generateUuid());
      newDiscrepancy.setId(id);
      discrepancies.put(id, newDiscrepancy);
   }

   private void addMcdcDiscrepancy(Map<String, Discrepancy> discrepancies, String location, String text,
      Map<Integer, DispoAnnotationData> pairAnnotations, String devNotes, boolean isOverloadedCondition) {
      String id = String.valueOf(Lib.generateUuid());
      Discrepancy newDiscrepancy = new Discrepancy();
      newDiscrepancy.setId(id);
      newDiscrepancy.setLocation(location);
      newDiscrepancy.setText(text);
      newDiscrepancy.setPairAnnotations(pairAnnotations);
      newDiscrepancy.setDevNotes(devNotes);
      newDiscrepancy.setIsOverloadedCondition(isOverloadedCondition);
      discrepancies.put(id, newDiscrepancy);

   }

   private DispoAnnotationData createPairAnnotation(String location, String text, boolean isRowCovered, int row,
      Collection<Integer> collection) {
      String id = String.valueOf(Lib.generateUuid());
      DispoAnnotationData pairAnnotation = new DispoAnnotationData();
      pairAnnotation.setId(id);
      pairAnnotation.setLocationRefs(location);
      pairAnnotation.setRow(row);
      pairAnnotation.setCustomerNotes(text);
      pairAnnotation.setIsRowCovered(isRowCovered);
      pairAnnotation.setPairedWith(collection);
      pairAnnotation.setLocationRefs(location);
      pairAnnotation.setIsPairAnnotation(true);
      pairAnnotation.setResolutionType("");
      pairAnnotation.setResolution("");
      pairAnnotation.setIsResolutionValid(false);
      pairAnnotation.setIsDefault(false);
      return pairAnnotation;
   }

   private String generateDatId(Object... ids) {
      StringBuilder sb = new StringBuilder();
      for (Object id : ids) {
         sb.append(id);
         sb.append(":");
      }

      return sb.toString();
   }

   private void processResults(VCastResult result, Map<String, DispoItem> exisitingItems, OperationReport report)
      throws Exception {
      String resultPath = result.getPath();
      String resultPathAbs = vCastDir + File.separator + resultPath;
      File resultsFile = new File(resultPathAbs);
      if (!resultsFile.exists()) {
         boolean fileExists = findAndProcessResultFile(resultsFile, resultPath, exisitingItems, report);

         if (!fileExists) {
            report.addEntry("SQL", String.format("Could not find DAT file [%s]", resultPathAbs), WARNING);
         }
      } else {
         process(resultPath, resultsFile, exisitingItems, report);
      }
   }

   private boolean findAndProcessResultFile(File resultsFile, String resultPath, Map<String, DispoItem> exisitingItems,
      OperationReport report) {
      List<File> resultsDirs = new ArrayList<>();
      resultsDirs.add(new File(vCastDir + File.separator + RESULTS));
      resultsDirs.add(new File(vCastDir + File.separator + RESULTS + File.separator + IMPORTED_RESULTS));

      for (File resultsDir : resultsDirs) {
         if (!resultsDir.exists()) {
            continue;
         }
         File[] files = resultsDir.listFiles();
         if (files != null) {
            for (File file : files) {
               String inputF = file.toString();
               String outputF = inputF.replaceAll(config.getResultsFileExtRegex(), "");
               if (outputF.toString().equalsIgnoreCase(resultsFile.toString())) {
                  process(resultPath, file, exisitingItems, report);
                  return true; // File exists
               }
            }
         }
      }
      return false; // File does not exist
   }

   private void process(String resultPath, File resultsFile, Map<String, DispoItem> exisitingItems,
      OperationReport report) {
      if (!isDuplicateFile(resultsFile, report)) {
         //Start reading line by line
         BufferedReader br = null;
         try {
            br = new BufferedReader(new FileReader(resultsFile));
            String resultsLine;
            while ((resultsLine = br.readLine()) != null) {
               // Loop through results file and log coverageItem as Test_Unit for each entry
               if (Strings.isValid(resultsLine)) {
                  Result datFileSyntaxResult = VCastValidateDatFileSyntax.validateDatFileSyntax(resultsLine);
                  if (!datFileSyntaxResult.isTrue()) {
                     report.addEntry("SQL", String.format("This line [%s] is not in proper format. In DAT file [%s]",
                        resultsLine, resultsFile.getName()), WARNING);
                  } else {
                     if (!alreadyUsedDatIds.contains(resultsLine)) {
                        processDatFileLine(report, resultPath, resultsFile, resultsLine, exisitingItems);
                     }
                  }
               }
            }
         } catch (Exception ex) {
            report.addEntry("EXCEPTION", ex.getMessage(), ERROR);
            BufferedReader br2 = null;
            try {
               br2 = new BufferedReader(new FileReader(resultsFile));
               String resultsLine;

               while ((resultsLine = br2.readLine()) != null) {
                  // Loop through results file and log coverageItem as Test_Unit for each entry
                  if (Strings.isValid(resultsLine)) {
                     Result datFileSyntaxResult = VCastValidateDatFileSyntax.validateDatFileSyntax(resultsLine);
                     if (!datFileSyntaxResult.isTrue()) {
                        report.addEntry("SQL", String.format("This line [%s] is not in proper format. In DAT file [%s]",
                           resultsLine, resultsFile.getName()), WARNING);
                     } else {
                        if (!alreadyUsedDatIds.contains(resultsLine)) {
                           processDatFileLine(report, resultPath, resultsFile, resultsLine, exisitingItems);
                        }
                     }
                  }
               }
            } catch (Exception ex2) {
               report.addEntry("EXCEPTION", ex2.getMessage(), ERROR);
            } finally {
               Lib.close(br2);
               Lib.close(br);
            }
         } finally {
            Lib.close(br);
         }
      }
   }

   private void processDatFileLine(OperationReport report, String resultPath, File resultsFile, String resultsLine,
      Map<String, DispoItem> exisitingItems) {
      alreadyUsedDatIds.add(resultsLine);
      StringTokenizer st = new StringTokenizer(resultsLine);
      int count = st.countTokens();
      if (count == 3) {
         Matcher m = fileMethod3LineNumberPattern.matcher(resultsLine);
         if (m.find()) {
            processSingleResult(resultPath, m, exisitingItems);
         }
      } else if (count == 4) {
         Matcher m = fileMethod4LineNumberPlusTokenPattern.matcher(resultsLine);
         if (m.find()) {
            processSingleResultBranch(resultPath, m, exisitingItems);
         }
      } else if (count == 5) {
         Matcher m = fileMethod5LineNumberPattern.matcher(resultsLine);
         if (m.find()) {
            processMultiResultMCDC(resultPath, m, exisitingItems);
         }
      } else {
         report.addEntry("RESULTS FILE PARSE",
            String.format("This line [%s] could not be parsed. In DAT file [%s]", resultsLine, resultsFile.getName()),
            WARNING);
      }
   }

   private boolean isDuplicateFile(File file, OperationReport report) {
      String normalizedFileName = file.getName().replace("//....", "");
      if (alreadyUsedFileNames.contains(normalizedFileName)) {
         report.addEntry(file.getName(), "Duplicate File skipped", DispoSummarySeverity.WARNING);
         return true;
      } else {
         alreadyUsedFileNames.add(normalizedFileName);
         return false;
      }
   }

   private void processSingleResult(String resultPath, Matcher m, Map<String, DispoItem> exisitingItems) {
      DispoItemData item = datIdToItem.get(generateDatId(m.group(1), m.group(2)));
      String lastManualResolutionType = "";
      String lastManualResolution = "";
      DispoItem prevItem = getOldItem(new ArrayList<DispoItem>(exisitingItems.values()), item);

      if (item != null) {
         String location = m.group(3);
         String discrepancyText = "";
         Discrepancy matchingDiscrepancy = matchDiscrepancy(location, item.getDiscrepanciesList());
         if (matchingDiscrepancy != null) {
            discrepancyText = matchingDiscrepancy.getText();
            Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();

            discrepancies.remove(matchingDiscrepancy.getId());
            datIdsCoveredByException.remove(generateDatId(m.group(1), m.group(2), m.group(3)));
            item.setDiscrepanciesList(discrepancies);

            DispoAnnotationData prevAnnotation = getOldAnnotByDisc(prevItem, matchingDiscrepancy);
            if (prevAnnotation.isValid() && !prevAnnotation.getResolutionType().equalsIgnoreCase(
               DispoStrings.Test_Unit_Resolution) && !prevAnnotation.getResolutionType().equalsIgnoreCase(
                  DispoStrings.Exception_Handling_Resolution)) {
               lastManualResolutionType = prevAnnotation.getResolutionType();
               lastManualResolution = prevAnnotation.getResolution();
            }

            addAnnotationForCoveredLine(item, location, Test_Unit_Resolution, resultPath, discrepancyText,
               lastManualResolutionType, lastManualResolution);
         }
      }
   }

   private void processSingleResultBranch(String resultPath, Matcher m, Map<String, DispoItem> exisitingItems) {
      DispoItemData item = datIdToItem.get(generateDatId(m.group(1), m.group(2)));
      String lastManualResolutionType = "";
      String lastManualResolution = "";
      DispoItem prevItem = getOldItem(new ArrayList<DispoItem>(exisitingItems.values()), item);

      if (item != null) {
         String location = m.group(3) + "." + m.group(4);
         Discrepancy matchingDiscrepancy = matchDiscrepancy(location, item.getDiscrepanciesList());
         if (matchingDiscrepancy != null) {

            String text = matchingDiscrepancy.getText();
            Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();
            discrepancies.remove(matchingDiscrepancy.getId());
            datIdsCoveredByException.remove(generateDatId(m.group(1), m.group(2), m.group(3), m.group(4)));
            item.setDiscrepanciesList(discrepancies);

            DispoAnnotationData prevAnnotation = getOldAnnotByDisc(prevItem, matchingDiscrepancy);
            if (prevAnnotation.isValid() && !prevAnnotation.getResolutionType().equalsIgnoreCase(
               DispoStrings.Test_Unit_Resolution) && !prevAnnotation.getResolutionType().equalsIgnoreCase(
                  DispoStrings.Exception_Handling_Resolution)) {
               lastManualResolutionType = prevAnnotation.getResolutionType();
               lastManualResolution = prevAnnotation.getResolution();
            }

            addAnnotationForCoveredLine(item, location, Test_Unit_Resolution, resultPath, text,
               lastManualResolutionType, lastManualResolution);
         }
      }
   }

   private void processMultiResultMCDC(String resultPath, Matcher m, Map<String, DispoItem> exisitingItems) {
      DispoItemData item = datIdToItem.get(generateDatId(m.group(1), m.group(2)));
      DispoItem prevItem = getOldItem(new ArrayList<DispoItem>(exisitingItems.values()), item);

      if (item != null) {
         Integer lineNumber = Integer.valueOf(m.group(3));
         Integer bitsValue = Integer.valueOf(m.group(4));
         Integer bitsMask = Integer.valueOf(m.group(5));
         Map<String, Boolean> bitsTrueMap =
            getBitToBoolean(Integer.toString(bitsValue, 2), Integer.toString(bitsMask, 2));
         for (String abbrevCond : bitsTrueMap.keySet()) {
            String lastManualResolutionType = "";
            String lastManualResolution = "";
            String location = "";
            if (bitsTrueMap.get(abbrevCond) == null) {
               location = formatLocation(lineNumber, abbrevCond, " ");
            } else {
               if (bitsTrueMap.get(abbrevCond)) {
                  location = formatLocation(lineNumber, abbrevCond, "T");
               } else {
                  location = formatLocation(lineNumber, abbrevCond, "F");
               }
            }
            Discrepancy matchingDiscrepancy = matchDiscrepancy(location, item.getDiscrepanciesList());
            if (matchingDiscrepancy != null) {

               DispoAnnotationData prevAnnotation = getOldAnnotByDisc(prevItem, matchingDiscrepancy);
               if (prevAnnotation.isValid() && !prevAnnotation.getResolutionType().equalsIgnoreCase(
                  DispoStrings.Test_Unit_Resolution) && !prevAnnotation.getResolutionType().equalsIgnoreCase(
                     DispoStrings.Exception_Handling_Resolution)) {
                  lastManualResolutionType = prevAnnotation.getResolutionType();
                  lastManualResolution = prevAnnotation.getResolution();
               }

               String text = matchingDiscrepancy.getText();
               Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();

               if (matchingDiscrepancy.getPairAnnotations() != null && !matchingDiscrepancy.getPairAnnotations().isEmpty() && !matchingDiscrepancy.getIsOverloadedCondition()) {
                  boolean isMcdcCovered = addAnnotationForCoveredMcdcLine(item, location, Test_Unit_Resolution,
                     resultPath, text, matchingDiscrepancy.getPairAnnotations(), lastManualResolutionType,
                     lastManualResolution, prevItem);
                  if (isMcdcCovered) {
                     discrepancies.remove(matchingDiscrepancy.getId());
                  }
               } else if (matchingDiscrepancy.getIsOverloadedCondition()) {
                  addAnnotationForOverloadedMcdcLine(item, location, text, matchingDiscrepancy.getDevNotes(),
                     Test_Unit_Resolution, resultPath, lastManualResolutionType, lastManualResolution);
               } else {
                  addAnnotationForCoveredLine(item, location, Test_Unit_Resolution, resultPath, text,
                     lastManualResolutionType, lastManualResolution);
                  discrepancies.remove(matchingDiscrepancy.getId());
               }
               datIdsCoveredByException.remove(
                  generateDatId(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5)));
               item.setDiscrepanciesList(discrepancies);
            }
         }
      }
   }

   private String formatLocation(int lineNumber, String abbrevCond, String TorF) {
      if (abbrevCond.length() != 1 || abbrevCond.charAt(0) < 65 || abbrevCond.charAt(0) > 90) {
         return String.format("%s.%s.%s", lineNumber, abbrevCond, TorF);
      }
      return String.format("%s.%s", lineNumber, abbrevCond.charAt(0) - 64 + " (P" + abbrevCond.toLowerCase() + ")");
   }

   private Map<String, Boolean> getBitToBoolean(String bitsValue, String bitsMask) {
      Map<String, Boolean> toReturn = new HashMap<>();
      char[] bitsMaskedArray = bitsMask.toCharArray();
      char[] bitsValueArray = bitsValue.toCharArray();
      int totalResultIndex = bitsValueArray.length - 1;

      int sizeDelta = bitsMaskedArray.length - bitsValueArray.length;
      int highestChar = 63 + bitsMaskedArray.length;
      for (int i = 0; i < sizeDelta; i++) {
         char valueMaskBit = bitsMaskedArray[i];
         char c = (char) highestChar--;
         String key = Character.toString(c);
         if (valueMaskBit == '1') {
            toReturn.put(key, false);
         } else {
            toReturn.put(key, null);
         }
      }

      for (int i = 0; i < bitsValueArray.length; i++) {
         char valueC = bitsValueArray[i];
         char valueMaskBit = bitsMaskedArray[i + sizeDelta];
         String key = "";
         if (i != totalResultIndex) {
            char c = (char) highestChar--;
            key = Character.toString(c);
         } else {
            key = "RESULT";
         }
         if (valueMaskBit == '0' && valueC == '0') {
            toReturn.put(key, null);
         } else {
            toReturn.put(key, valueC == '1');
         }
      }
      return toReturn;
   }

   private Discrepancy matchDiscrepancy(String location, Map<String, Discrepancy> discrepancies) {
      for (String key : discrepancies.keySet()) {
         Discrepancy discrepancy = discrepancies.get(key);
         if (String.valueOf(discrepancy.getLocation()).equals(location)) {
            return discrepancy;
         }
      }
      return null;
   }

   private DispoAnnotationData getOldAnnotByDisc(DispoItem oldItem, Discrepancy discrepancy) {
      return getOldAnnot(oldItem, discrepancy.getLocation());
   }

   private DispoAnnotationData getOldAnnotByAnnot(DispoItem oldItem, DispoAnnotationData annotationToMatch) {
      return getOldAnnot(oldItem, annotationToMatch.getLocationRefs());
   }

   private DispoAnnotationData getOldAnnot(DispoItem oldItem, String locationRef) {
      if (oldItem.isValid()) {
         for (DispoAnnotationData value : oldItem.getAnnotationsList()) {
            if (String.valueOf(value.getLocationRefs()).equals(locationRef)) {
               return value;
            }
         }
      }
      return DispoAnnotationData.SENTINEL;
   }

   private DispoItem getOldItem(List<DispoItem> oldItems, DispoItemData itemToMatch) {
      if (!oldItems.isEmpty()) {
         for (DispoItem oldItem : oldItems) {
            if (String.valueOf(oldItem.getName()).equals(itemToMatch.getName())) {
               return oldItem;
            }
         }
      }
      return DispoItemData.SENTINEL;
   }

   private void keepExistingAnnotation(DispoItemData item, DispoAnnotationData existingAnnotation) {
      String location = existingAnnotation.getLocationRefs();
      if (location.contains("(P")) {
         Discrepancy matchingDiscrepancy = matchDiscrepancy(location, item.getDiscrepanciesList());
         if (matchingDiscrepancy.getPairAnnotations() != null && !matchingDiscrepancy.getPairAnnotations().isEmpty() && !matchingDiscrepancy.getIsOverloadedCondition()) {
            //Already handled if some part of the pair was hit by test (default).
            for (DispoAnnotationData pairData : matchingDiscrepancy.getPairAnnotations().values()) {
               if (pairData.getIsDefault()) {
                  return;
               }
            }
         }
      }

      DispoUtil.addAnnotation(item.getAnnotationsList(), existingAnnotation);

   }

   private void addBlankAnnotationForUncoveredLine(DispoItemData item, String location, String text,
      DispoAnnotationData uncoveredAnnotation) {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();

      dataFactory.initAnnotation(newAnnotation);
      String idOfNewAnnotation = dataFactory.getNewId();
      newAnnotation.setId(idOfNewAnnotation);

      //For blank MCDC annotations
      if (location.contains("(P")) {
         Discrepancy matchingDiscrepancy = matchDiscrepancy(location, item.getDiscrepanciesList());
         if (matchingDiscrepancy.getPairAnnotations() != null && !matchingDiscrepancy.getPairAnnotations().isEmpty() && !matchingDiscrepancy.getIsOverloadedCondition()) {
            Map<Integer, DispoAnnotationData> pairAnnotations = new HashMap<>();
            pairAnnotations.putAll(matchingDiscrepancy.getPairAnnotations());
            newAnnotation.setPossiblePairs(calcPossiblePairs(pairAnnotations, item));
            newAnnotation.setSatisfiedPairs("");
         }
      }

      newAnnotation.setIsDefault(false);
      newAnnotation.setLocationRefs(location);
      newAnnotation.setResolutionType("");
      newAnnotation.setResolution("");
      newAnnotation.setLastResolutionType(uncoveredAnnotation.getResolutionType());
      newAnnotation.setLastResolution(uncoveredAnnotation.getResolution());
      newAnnotation.setLastManualResolutionType(uncoveredAnnotation.getLastManualResolutionType());
      newAnnotation.setLastManualResolution(uncoveredAnnotation.getLastManualResolution());
      newAnnotation.setIsResolutionValid(false);
      newAnnotation.setCustomerNotes(text);

      boolean annotAdded = DispoUtil.addAnnotation(item.getAnnotationsList(), newAnnotation);

      if (annotAdded) {
         dispoConnector.connectAnnotation(newAnnotation, item.getDiscrepanciesList());
      }

   }

   private void addAnnotationForPreviouslyCoveredLine(DispoItemData item, String location, String text,
      DispoAnnotationData uncoveredAnnotation) {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      dataFactory.initAnnotation(newAnnotation);
      String idOfNewAnnotation = dataFactory.getNewId();
      newAnnotation.setId(idOfNewAnnotation);

      newAnnotation.setIsDefault(false);
      newAnnotation.setLocationRefs(location);
      newAnnotation.setResolutionType(uncoveredAnnotation.getLastManualResolutionType());
      newAnnotation.setResolution(uncoveredAnnotation.getLastManualResolution());
      newAnnotation.setLastResolutionType(uncoveredAnnotation.getResolutionType());
      newAnnotation.setLastResolution(uncoveredAnnotation.getResolution());
      newAnnotation.setLastManualResolutionType(uncoveredAnnotation.getLastManualResolutionType());
      newAnnotation.setLastManualResolution(uncoveredAnnotation.getLastManualResolution());
      newAnnotation.setIsResolutionValid(false);
      newAnnotation.setAndGetNeedsModify();

      newAnnotation.setCustomerNotes(text);

      if (location.contains("(P")) {
         Discrepancy matchingDiscrepancy = matchDiscrepancy(location, item.getDiscrepanciesList());
         if (matchingDiscrepancy.getPairAnnotations() != null && !matchingDiscrepancy.getPairAnnotations().isEmpty() && !matchingDiscrepancy.getIsOverloadedCondition()) {
            Map<Integer, DispoAnnotationData> pairAnnotations = new HashMap<>();
            pairAnnotations.putAll(matchingDiscrepancy.getPairAnnotations());
            newAnnotation.setPossiblePairs(calcPossiblePairs(pairAnnotations, item));
            newAnnotation.setSatisfiedPairs("");
         }
      }

      dispoConnector.connectAnnotation(newAnnotation, item.getDiscrepanciesList());

      List<DispoAnnotationData> annotationsList = item.getAnnotationsList();
      int newIndex = annotationsList.size();
      newAnnotation.setIndex(newIndex);
      annotationsList.add(newIndex, newAnnotation);
   }

   private void addAnnotationForCoveredLine(DispoItemData item, String location, String resolutionType,
      String coveringFile, String text, String lastManualResolutionType, String lastManualResolution) {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      dataFactory.initAnnotation(newAnnotation);
      String idOfNewAnnotation = dataFactory.getNewId();
      newAnnotation.setId(idOfNewAnnotation);

      newAnnotation.setIsDefault(true);
      newAnnotation.setLocationRefs(location);
      newAnnotation.setResolutionType(resolutionType);
      newAnnotation.setResolution(coveringFile);
      newAnnotation.setLastResolutionType(lastManualResolutionType);
      newAnnotation.setLastResolution(lastManualResolution);
      newAnnotation.setLastManualResolutionType(lastManualResolutionType);
      newAnnotation.setLastManualResolution(lastManualResolution);

      newAnnotation.setIsResolutionValid(true);
      newAnnotation.setCustomerNotes(text);

      DispoUtil.addAnnotation(item.getAnnotationsList(), newAnnotation);
   }

   private void addAnnotationForOverloadedMcdcLine(DispoItemData item, String location, String text,
      String developerNotes, String lastResolutionType, String lastResolution, String lastManualResolutionType,
      String lastManualResolution) {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      dataFactory.initAnnotation(newAnnotation);
      String idOfNewAnnotation = dataFactory.getNewId();
      newAnnotation.setId(idOfNewAnnotation);

      newAnnotation.setIsDefault(false);
      newAnnotation.setLocationRefs(location);
      newAnnotation.setResolutionType("");
      newAnnotation.setResolution("");

      newAnnotation.setLastResolutionType(Test_Unit_Resolution);
      newAnnotation.setLastResolution(lastResolution);
      newAnnotation.setLastManualResolutionType(lastManualResolutionType);
      newAnnotation.setLastManualResolution(lastManualResolution);

      newAnnotation.setIsResolutionValid(false);
      newAnnotation.setCustomerNotes(text);
      newAnnotation.setDeveloperNotes(developerNotes);

      boolean annotAdded = DispoUtil.addAnnotation(item.getAnnotationsList(), newAnnotation);

      if (annotAdded && !newAnnotation.getIsResolutionValid()) {
         dispoConnector.connectAnnotation(newAnnotation, item.getDiscrepanciesList());
      }
   }

   private boolean addAnnotationForCoveredMcdcLine(DispoItemData item, String location, String resolutionType,
      String coveringFile, String text, Map<Integer, DispoAnnotationData> pairAnnotations,
      String lastManualResolutionType, String lastManualResolution, DispoItem prevItem) {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      dataFactory.initAnnotation(newAnnotation);
      String idOfNewAnnotation = dataFactory.getNewId();

      newAnnotation.setId(idOfNewAnnotation);
      newAnnotation.setLocationRefs(location);

      newAnnotation.setCustomerNotes(text);

      boolean isPairSatisfied =
         calcSatisfiedPairs(pairAnnotations, resolutionType, coveringFile, prevItem, newAnnotation);
      newAnnotation.setPossiblePairs(calcPossiblePairs(pairAnnotations, item));

      DispoAnnotationData prevAnnotation = getOldAnnotByAnnot(prevItem, newAnnotation);

      if (prevAnnotation.isValid()) {
         String prevResolutionType = prevAnnotation.getResolutionType();
         String prevResolution = prevAnnotation.getResolution();

         if (!prevAnnotation.getIsDefault() && newAnnotation.getIsDefault()) {
            //If prev is a manual disposition and new is default, set 'last manual res' field to prev.
            newAnnotation.setLastManualResolutionType(prevResolutionType);
            newAnnotation.setLastManualResolution(prevResolution);
         } else if (prevAnnotation.getIsDefault() && newAnnotation.getIsDefault() && (!prevResolutionType.equals(
            newAnnotation.getResolutionType()) || !prevResolution.equals(newAnnotation.getResolution()))) {
            //If both are test but they differ, keep new and put prev into 'last resolution' field.
            newAnnotation.setLastResolutionType(prevResolutionType);
            newAnnotation.setLastResolution(prevAnnotation.getResolution());
         }
      }

      boolean annotAdded = DispoUtil.addAnnotation(item.getAnnotationsList(), newAnnotation);

      if (annotAdded && !newAnnotation.getIsResolutionValid()) {
         dispoConnector.connectAnnotation(newAnnotation, item.getDiscrepanciesList());
      }

      return isPairSatisfied;
   }

   private boolean calcSatisfiedPairs(Map<Integer, DispoAnnotationData> pairAnnotations, String resolutionType,
      String resolution, DispoItem prevItem, DispoAnnotationData parentAnnotation) {
      List<String> satisfiedPairs = new ArrayList<>();
      boolean fullPairCoverage = false;
      for (DispoAnnotationData pairAnnotation : pairAnnotations.values()) {

         if (pairAnnotation.isValid()) {
            if (pairAnnotation.getIsRowCovered()) {
               pairAnnotation.setResolutionType(resolutionType);
               pairAnnotation.setResolution(resolution);
               pairAnnotation.setIsResolutionValid(true);
               pairAnnotation.setIsDefault(true);
            }

            DispoAnnotationData prevAnnotation = getOldAnnotByAnnot(prevItem, pairAnnotation);

            if (prevAnnotation.isValid()) {
               String prevResolutionType = prevAnnotation.getResolutionType();
               String prevResolution = prevAnnotation.getResolution();

               String newResolutionType = pairAnnotation.getResolutionType();
               String newResolution = pairAnnotation.getResolution();

               //If new annotation is empty, set it to the old one.
               if (newResolutionType.isEmpty() && newResolution.isEmpty()) {
                  pairAnnotation.setResolutionType(prevResolutionType);
                  pairAnnotation.setResolution(prevResolution);
                  pairAnnotation.setIsResolutionValid(prevAnnotation.getIsResolutionValid());
               } else if (!prevAnnotation.getIsDefault() && pairAnnotation.getIsDefault()) {
                  //If prev is a manual disposition and new is default, set 'last manual res' field to prev.
                  pairAnnotation.setLastManualResolutionType(prevResolutionType);
                  pairAnnotation.setLastManualResolution(prevResolution);
               } else if (prevAnnotation.getIsDefault() && pairAnnotation.getIsDefault() && (!prevResolutionType.equals(
                  newResolutionType) || !prevResolution.equals(newResolution))) {
                  //If both are test but they differ, keep new and put prev into 'last resolution' field.
                  pairAnnotation.setLastResolutionType(prevResolutionType);
                  pairAnnotation.setLastResolution(prevAnnotation.getResolution());
               }
            }
         } else {
            satisfiedPairs.add("SENTINEL ITEM");
            continue;
         }

         if (pairAnnotation.getIsResolutionValid() || pairAnnotation.setAndGetNeedsModify()) {
            for (int pair : pairAnnotation.getPairedWith()) {
               int sideARow = pairAnnotation.getRow();
               DispoAnnotationData associatedPair = pairAnnotations.get(pair);
               if (sideARow < pair) {
                  continue;
               }
               if (pairAnnotation.getIsResolutionValid() && associatedPair.getIsResolutionValid()) {
                  fullPairCoverage = true;
                  satisfiedPairs.add(String.format("%s/%s", pair, sideARow));
               }

               if (!pairAnnotation.getResolutionType().isEmpty() && !associatedPair.getResolutionType().isEmpty()) {
                  String pairAResolutionType = pairAnnotation.getResolutionType();
                  String pairAResolution = pairAnnotation.getResolution();
                  String pairBResolutionType = associatedPair.getResolutionType();
                  String pairBResolution = associatedPair.getResolution();

                  if (pairAResolutionType.equals(pairBResolutionType)) {
                     parentAnnotation.setResolutionType(pairAResolutionType);
                  } else if (associatedPair.getIsDefault()) {
                     parentAnnotation.setResolutionType(
                        String.format("%s/%s", pairBResolutionType, pairAResolutionType));
                  } else {
                     parentAnnotation.setResolutionType(
                        String.format("%s/%s", pairAResolutionType, pairBResolutionType));
                  }

                  if (pairAResolution.equals(pairBResolution)) {
                     parentAnnotation.setResolution(pairAResolution);
                  } else if (associatedPair.getIsDefault()) {
                     parentAnnotation.setResolution(String.format("%s/%s", pairBResolution, pairAResolution));
                  } else {
                     parentAnnotation.setResolution(String.format("%s/%s", pairAResolution, pairBResolution));
                  }
               } else {
                  parentAnnotation.setIsDefault(false);
                  parentAnnotation.setResolutionType("");
                  parentAnnotation.setResolution("");
               }

            }
         }
      }
      parentAnnotation.setIsResolutionValid(fullPairCoverage);
      if (fullPairCoverage) {
         parentAnnotation.setSatisfiedPairs(String.format("Pairs Satisfied By: %s", satisfiedPairs.toString()));
      } else {
         parentAnnotation.setSatisfiedPairs("");
      }
      return fullPairCoverage;
   }

   private String calcPossiblePairs(Map<Integer, DispoAnnotationData> pairAnnotations, DispoItemData item) {
      List<String> possiblePairs = new ArrayList<>();

      for (DispoAnnotationData pairAnnotation : pairAnnotations.values()) {
         DispoUtil.addAnnotation(item.getAnnotationsList(), pairAnnotation);

         for (int pair : pairAnnotation.getPairedWith()) {
            int sideARow = pairAnnotation.getRow();
            if (sideARow > pair) {
               possiblePairs.add(String.format("%s/%s", pair, sideARow));
            }
         }
      }
      return String.format("Possible Pairs: %s", possiblePairs.toString());
   }

   private Collection<VCastResult> getResultFiles(VCastDataStore dataStore) {
      Collection<VCastResult> results = null;
      results = dataStore.getAllResults();
      return results;
   }

   private String normalizeLisFileName(String fileName) {
      return fileName.replaceAll("^.*(\\\\|\\/)", "").toLowerCase();
   }
}

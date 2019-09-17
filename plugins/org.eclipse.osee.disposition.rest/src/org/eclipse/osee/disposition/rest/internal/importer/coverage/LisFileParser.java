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
package org.eclipse.osee.disposition.rest.internal.importer.coverage;

import static org.eclipse.osee.disposition.model.DispoStrings.Exception_Handling_Resolution;
import static org.eclipse.osee.disposition.model.DispoStrings.Test_Unit_Resolution;
import static org.eclipse.osee.disposition.model.DispoSummarySeverity.ERROR;
import static org.eclipse.osee.disposition.model.DispoSummarySeverity.WARNING;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
   private static final String EXIT_WHEN = "\\s*\\( \\)\\s*\\( \\)\\s*(EXIT WHEN).*";
   private static final String WHEN_FOR = "\\s*\\( \\)\\s*(WHEN|FOR).*";
   private static final String WHEN_CASE = "(.*\\bWHEN\\b\\s*[^:]*$)";
   private static final String CASE_STATEMENT = "(.*(\\bCASE|case|default|\\s+.+[:].*))";
   private static final String WHILE_ONE = "(.*\\bWHILE|while\\s*\\(1\\).*)";

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
   public List<DispoItem> importDirectory(Map<String, DispoItem> exisitingItems, File filesDir, OperationReport report) {
      vCastDir = filesDir.getAbsolutePath() + File.separator + "vcast";
      File f = new File(vCastDir + File.separator + "cover.db");

      VCastDataStore dataStore = VCastClient.newDataStore(f.getAbsolutePath());

      dataStore.setIsMCDC();
      dataStore.setIsBranch();

      Collection<VCastInstrumentedFile> instrumentedFiles = getInstrumentedFiles(dataStore, report);

      HashMap<String, File> nameToFileMap = createNameToFileMap(report);

      for (VCastInstrumentedFile instrumentedFile : instrumentedFiles) {
         processInstrumented(dataStore, instrumentedFile, nameToFileMap, report);
      }

      Collection<VCastResult> results = getResultFiles(dataStore);
      for (VCastResult result : results) {
         try {
            processResults(result, report);
         } catch (Exception ex) {
            report.addEntry("FAILURE", "VCast Error", ERROR);
         }
      }

      processExceptionHandled(report);

      return createItems(exisitingItems, report);
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

   private List<DispoItem> createItems(Map<String, DispoItem> exisitingItems, OperationReport report) {
      List<DispoItem> toReturn;
      Collection<DispoItemData> items = datIdToItem.values();

      for (DispoItemData item : items) {
         dataFactory.initDispoItem(item);
         item.setTotalPoints(String.valueOf(item.getAnnotationsList().size() + item.getDiscrepanciesList().size()));
      }

      if (!exisitingItems.isEmpty()) {
         // This is a reimport so we'll need to copy all the annotations
         toReturn = runCopier(exisitingItems, report, items);
      } else {
         toReturn = new ArrayList<>();
         toReturn.addAll(items);
      }

      for (DispoItem item : toReturn) {
         if (item.getStatus().equalsIgnoreCase("incomplete")) {
            createPlaceHolderAnnotations((DispoItemData) item, exisitingItems, report);
         }
      }
      return toReturn;
   }

   private List<DispoItem> runCopier(Map<String, DispoItem> exisitingItems, OperationReport report, Collection<DispoItemData> items) {
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

      toReturn = copier.copyAllDispositions(namesToDestItems, exisitingItems.values(), false, null, report);
      return toReturn;
   }

   private void createPlaceHolderAnnotations(DispoItemData item, Map<String, DispoItem> exisitingItems, OperationReport report) {
      List<DispoItem> prevItems = new ArrayList<DispoItem>(exisitingItems.values());
      List<String> uncovered = dispoConnector.getAllUncoveredDiscprepancies(item);
      if (!uncovered.isEmpty()) {
         Map<String, Discrepancy> discrepanciesList = item.getDiscrepanciesList();
         for (String id : discrepanciesList.keySet()) {
            Discrepancy discrepancy = discrepanciesList.get(id);
            if (uncovered.contains(discrepancy.getLocation())) {
               DispoAnnotationData uncoveredAnnotation = matchOldAnnotation(discrepancy, prevItems, item);
               if (uncoveredAnnotation != null) {
                  if (uncoveredAnnotation.getResolutionType().equalsIgnoreCase(DispoStrings.Test_Unit_Resolution)) {
                     uncoveredAnnotation.setLastResolution(uncoveredAnnotation.getResolution());
                  } else if (uncoveredAnnotation.getResolutionType().equalsIgnoreCase(
                     DispoStrings.Exception_Handling_Resolution)) {
                     uncoveredAnnotation.setLastResolution("Exception_Handling");
                  }
                  addBlankAnnotationForUncoveredLine(item, discrepancy.getLocation(), discrepancy.getText(),
                     uncoveredAnnotation.getLastResolution());
               } else {
                  addBlankAnnotationForUncoveredLine(item, discrepancy.getLocation(), discrepancy.getText(), "N/A");
               }
            }
         }
      }
   }

   private void processExceptionHandled(OperationReport report) {
      for (String datId : datIdsCoveredByException) {
         Matcher matcher = Pattern.compile("\\d*:\\d*:").matcher(datId);
         matcher.find();
         String itemDatId = matcher.group();
         DispoItemData item = datIdToItem.get(itemDatId);
         String line = datId.replaceAll("\\d*:\\d*:", "");
         line = line.replaceAll(":", "");
         String text = "";
         Discrepancy matchingDiscrepancy = matchDiscrepancy(line, item.getDiscrepanciesList());
         if (matchingDiscrepancy != null) {
            text = matchingDiscrepancy.getText();
            Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();
            discrepancies.remove(matchingDiscrepancy.getId());
            addAnnotationForCoveredLine(item, line, Exception_Handling_Resolution, "", text);
         }
      }
   }

   private Collection<VCastInstrumentedFile> getInstrumentedFiles(VCastDataStore dataStore, OperationReport report) {
      Collection<VCastInstrumentedFile> instrumentedFiles = new ArrayList<>();
      try {
         /**
          * Note: the LIS_file field of the instrumentedFiles may have a fictious absolute path - but the path is
          * ignored and only the file name is used.
          */
         instrumentedFiles = dataStore.getAllInstrumentedFiles();
      } catch (OseeCoreException ex) {
         report.addEntry("SQL", String.format("SQL error while reading functions for directory: [%s]", vCastDir),
            ERROR);
      }
      return instrumentedFiles;
   }

   private void processInstrumented(VCastDataStore dataStore, VCastInstrumentedFile instrumentedFile, HashMap<String, File> nameToFileMap, OperationReport report) {
      VCastSourceFileJoin sourceFile = null;
      try {
         sourceFile = dataStore.getSourceFileJoin(instrumentedFile);
      } catch (OseeCoreException ex) {
         report.addEntry("SQL",
            String.format("SQL error while reading source_files for instrumented_file id: [%s]. Error Message: [%s]",
               instrumentedFile.getId(), ex.getMessage()),
            ERROR);
      }

      if (sourceFile != null) {
         int fileNum = sourceFile.getUnitIndex();

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
               processFunction(instrumentedFile, lisFileParser, fileNum, dataStore, instrumentedFile, function,
                  dataStore.getIsMCDC(), report);
            }
         } else {
            report.addEntry("VCast", String.format("Could not find file: %s", normalizedName), ERROR);
         }
      }
   }

   private void processFunction(VCastInstrumentedFile lisFile, VCastLisFileParser lisFileParser, int fileNum, VCastDataStore dataStore, VCastInstrumentedFile instrumentedFile, VCastFunction function, boolean isMCDCFile, OperationReport report) {
      int functionNum = function.getFindex();
      String itemName = "";
      DispoItemData newItem = new DispoItemData();
      newItem.setAnnotationsList(new ArrayList<DispoAnnotationData>());
      VCastSourceFileJoin sourceFileJoin = dataStore.getSourceFileJoin(lisFile);
      itemName = sourceFileJoin.getDisplayName() + "." + function.getName();
      newItem.setName(itemName);
      newItem.setFileNumber(Integer.toString(fileNum));
      newItem.setMethodNumber(Integer.toString(functionNum));

      try {
         String fileName = sourceFileJoin.getDisplayName();
         String fullPathToFile = vCastDir + File.separator + fileName.replaceAll(config.getFileExtRegex(), ".LIS");
         Date lastModified = DispoUtil.getTimestampOfFile(fullPathToFile);
         newItem.setLastUpdate(lastModified);
      } catch (Throwable ex) {
         report.addEntry("Get Timestamp of File",
            String.format("Error retrieving the timestamp for [%s]. Error Message: [%s]", instrumentedFile.getId(),
               ex.getMessage()),
            ERROR);
      }

      String datId = generateDatId(fileNum, functionNum);
      datIdToItem.put(datId, newItem);

      checkForMultiEnvRename(fileNum, instrumentedFile, newItem);

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
         processStatement(lisFile, lisFileParser, fileNum, functionNum, function, statementCoverageItem, isMCDCFile,
            discrepancies, report);
      }

      // add discrepancies to item
      newItem.setDiscrepanciesList(discrepancies);
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

   private void processStatement(VCastInstrumentedFile lisFile, VCastLisFileParser lisFileParser, int fileNum, int functionNum, VCastFunction function, VCastStatementCoverage statementCoverageItem, boolean isMCDCFile, Map<String, Discrepancy> discrepancies, OperationReport report) {
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
            location = String.format("%s.%s.%s", lineNumber, statementCoverageItem.getAbbrevCondition(), "T");
            String location2 = String.format("%s.%s.%s", lineNumber, statementCoverageItem.getAbbrevCondition(), "F");

            if (!lineData.getFirst().matches(WHEN_FOR) && !lineData.getFirst().matches(
               CASE_STATEMENT) && !lineData.getFirst().matches(WHILE_ONE)) {
               // Only add corresponding 'F' discrepancy if it's not a WHEN or WHILE (1) condition statement
               text = statementCoverageItem.getFullCondition();
               addDiscrepancy(discrepancies, location, text);
               addDiscrepancy(discrepancies, location2, text);
            } else { // May be a case or WHILE (1) statement, which should only test for True
               text = lineData.getFirst().trim();
               addDiscrepancy(discrepancies, location, text);
            }

         } else {
            text = lineData.getFirst().trim();
            if (statementCoverageItem.getNumConditions() == 2) {
               location = String.format("%s.%s", lineNumber, "T");
               String locationF = String.format("%s.%s", lineNumber, "F");

               if (!lineData.getFirst().matches(WHEN_FOR) //
                  && !lineData.getFirst().matches(EXIT_WHEN) //
                  && !lineData.getFirst().matches(LOG) //
               ) {
                  addDiscrepancy(discrepancies, location, text);
                  addDiscrepancy(discrepancies, locationF, text);
               } else {
                  addDiscrepancy(discrepancies, location, text);
               }
            } else if (statementCoverageItem.getNumConditions() == 1 //
               && lineData.getFirst().matches(WHEN_CASE)) {
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

   private void addDiscrepancy(Map<String, Discrepancy> discrepancies, String location, String text) {
      Discrepancy newDiscrepancy = new Discrepancy();
      newDiscrepancy.setLocation(location);
      newDiscrepancy.setText(text);
      String id = String.valueOf(Lib.generateUuid());
      newDiscrepancy.setId(id);
      discrepancies.put(id, newDiscrepancy);
   }

   private String generateDatId(Object... ids) {
      StringBuilder sb = new StringBuilder();
      for (Object id : ids) {
         sb.append(id);
         sb.append(":");
      }

      return sb.toString();
   }

   private void processResults(VCastResult result, OperationReport report) throws Exception {
      String resultPath = result.getPath();
      String resultPathAbs = vCastDir + File.separator + resultPath;
      File resultsFile = new File(resultPathAbs);
      if (!resultsFile.exists()) {
         boolean fileExists = findAndProcessResultFile(resultsFile, resultPath, report);

         if (!fileExists) {
            report.addEntry("SQL", String.format("Could not find DAT file [%s]", resultPathAbs), WARNING);
         }
      } else {
         process(report, resultPath, resultsFile);
      }
   }

   private boolean findAndProcessResultFile(File resultsFile, String resultPath, OperationReport report) {
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
                  process(report, resultPath, file);
                  return true; // File exists
               }
            }
         }
      }
      return false; // File does not exist
   }

   private void process(OperationReport report, String resultPath, File resultsFile) {
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
                        processDatFileLine(report, resultPath, resultsFile, resultsLine);
                     }
                  }
               }
            }
         } catch (Exception ex) {
            report.addEntry("EXCEPTION", ex.getMessage(), ERROR);
         } finally {
            Lib.close(br);
         }
      }
   }

   private void processDatFileLine(OperationReport report, String resultPath, File resultsFile, String resultsLine) {
      alreadyUsedDatIds.add(resultsLine);
      StringTokenizer st = new StringTokenizer(resultsLine);
      int count = st.countTokens();
      if (count == 3) {
         Matcher m = fileMethod3LineNumberPattern.matcher(resultsLine);
         if (m.find()) {
            processSingleResult(resultPath, m);
         }
      } else if (count == 4) {
         Matcher m = fileMethod4LineNumberPlusTokenPattern.matcher(resultsLine);
         if (m.find()) {
            processSingleResultBranch(resultPath, m);
         }
      } else if (count == 5) {
         Matcher m = fileMethod5LineNumberPattern.matcher(resultsLine);
         if (m.find()) {
            processMultiResultMCDC(resultPath, m);
         }
      } else {
         report.addEntry("RESULTS FILE PARSE",
            String.format("This line [%s] could not be parsed. In DAT file [%s]", resultsLine, resultsFile.getName()),
            WARNING);
      }
   }

   private boolean isDuplicateFile(File file, OperationReport report) {
      String normalizedFileName = file.getName().replaceAll("//....", "");
      if (alreadyUsedFileNames.contains(normalizedFileName)) {
         report.addEntry(file.getName(), "Duplicate File skipped", DispoSummarySeverity.WARNING);
         return true;
      } else {
         alreadyUsedFileNames.add(normalizedFileName);
         return false;
      }
   }

   private void processSingleResult(String resultPath, Matcher m) {
      DispoItemData item = datIdToItem.get(generateDatId(m.group(1), m.group(2)));

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
            addAnnotationForCoveredLine(item, location, Test_Unit_Resolution, resultPath, discrepancyText);
         }
      }

   }

   private void processSingleResultBranch(String resultPath, Matcher m) {
      DispoItemData item = datIdToItem.get(generateDatId(m.group(1), m.group(2)));

      if (item != null) {
         String location = m.group(3) + "." + m.group(4);
         Discrepancy matchingDiscrepancy = matchDiscrepancy(location, item.getDiscrepanciesList());
         if (matchingDiscrepancy != null) {
            String text = matchingDiscrepancy.getText();
            Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();
            discrepancies.remove(matchingDiscrepancy.getId());
            datIdsCoveredByException.remove(generateDatId(m.group(1), m.group(2), m.group(3), m.group(4)));
            item.setDiscrepanciesList(discrepancies);
            addAnnotationForCoveredLine(item, location, Test_Unit_Resolution, resultPath, text);
         }
      }
   }

   private void processMultiResultMCDC(String resultPath, Matcher m) {
      DispoItemData item = datIdToItem.get(generateDatId(m.group(1), m.group(2)));

      if (item != null) {
         Integer lineNumber = Integer.valueOf(m.group(3));
         Integer bitsTrue = Integer.valueOf(m.group(4));
         Integer bitsUsed = Integer.valueOf(m.group(5));

         Map<String, Boolean> bitsTrueMap =
            getBitToBoolean(Integer.toString(bitsTrue, 2), Integer.toString(bitsUsed, 2));

         for (String abbrevCond : bitsTrueMap.keySet()) {
            String TorF = bitsTrueMap.get(abbrevCond) ? "T" : "F";
            String location = formatLocation(lineNumber, abbrevCond, TorF);
            Discrepancy matchingDiscrepancy = matchDiscrepancy(location, item.getDiscrepanciesList());
            if (matchingDiscrepancy != null) {
               String text = matchingDiscrepancy.getText();
               Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();
               discrepancies.remove(matchingDiscrepancy.getId());
               datIdsCoveredByException.remove(
                  generateDatId(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5)));
               item.setDiscrepanciesList(discrepancies);
               addAnnotationForCoveredLine(item, location, Test_Unit_Resolution, resultPath, text);
            }
         }
      }
   }

   private String formatLocation(int lineNumber, String abbrevCond, String TorF) {
      if (abbrevCond.length() != 1 || abbrevCond.charAt(0) < 65 || abbrevCond.charAt(0) > 90) {
         return String.format("%s.%s.%s", lineNumber, abbrevCond, TorF);
      }
      return String.format("%s.%s.%s", lineNumber, abbrevCond.charAt(0) - 64 + " (" + abbrevCond + ")", TorF);
   }

   private Map<String, Boolean> getBitToBoolean(String bitsTrue, String bitsUsed) {
      Map<String, Boolean> toReturn = new HashMap<>();
      char[] bitsUsedArray = bitsUsed.toCharArray();
      char[] bitsTrueArray = bitsTrue.toCharArray();
      int totalResultIndex = bitsTrueArray.length - 1;

      int sizeDelta = bitsUsedArray.length - bitsTrueArray.length;
      int highestChar = 63 + bitsUsedArray.length;
      for (int i = 1; i <= sizeDelta; i++) {
         char c = (char) highestChar--;
         String key = Character.toString(c);
         toReturn.put(key, false);
      }

      for (int i = 0; i < bitsTrueArray.length; i++) {
         char valueC = bitsTrueArray[i];
         if (i != totalResultIndex) {
            char c = (char) highestChar--;
            String key = Character.toString(c);
            toReturn.put(key, valueC == '1');
         } else {
            toReturn.put("RESULT", valueC == '1');
         }
      }
      return toReturn;
   }

   private Discrepancy matchDiscrepancy(String location, Map<String, Discrepancy> discrepancies) {
      Discrepancy toReturn = null;
      for (String key : discrepancies.keySet()) {
         Discrepancy discrepancy = discrepancies.get(key);
         if (String.valueOf(discrepancy.getLocation()).equals(location)) {
            toReturn = discrepancy;
            break;
         }
      }
      return toReturn;
   }

   private DispoAnnotationData matchOldAnnotation(Discrepancy discrepancy, List<DispoItem> oldItems, DispoItemData item) {
      DispoAnnotationData toReturn = null;
      DispoItem dispoItem = null;
      if (!oldItems.isEmpty()) {
         for (DispoItem oldItem : oldItems) {
            if (String.valueOf(oldItem.getName()).equals(item.getName())) {
               dispoItem = oldItem;
               break;
            }
         }
         for (DispoAnnotationData value : dispoItem.getAnnotationsList()) {
            if (String.valueOf(value.getLocationRefs()).equals(discrepancy.getLocation())) {
               toReturn = value;
               return toReturn;
            }
         }
      }
      return toReturn;
   }

   private void addBlankAnnotationForUncoveredLine(DispoItemData item, String location, String text, String lastResolution) {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      dataFactory.initAnnotation(newAnnotation);
      String idOfNewAnnotation = dataFactory.getNewId();
      newAnnotation.setId(idOfNewAnnotation);

      newAnnotation.setIsDefault(false);
      newAnnotation.setLocationRefs(location);
      newAnnotation.setResolutionType("");
      newAnnotation.setResolution("");
      newAnnotation.setLastResolution(lastResolution);
      newAnnotation.setIsResolutionValid(false);
      newAnnotation.setCustomerNotes(text);
      dispoConnector.connectAnnotation(newAnnotation, item.getDiscrepanciesList());

      List<DispoAnnotationData> annotationsList = item.getAnnotationsList();
      int newIndex = annotationsList.size();
      newAnnotation.setIndex(newIndex);
      annotationsList.add(newIndex, newAnnotation);
   }

   private void addAnnotationForCoveredLine(DispoItemData item, String location, String resolutionType, String coveringFile, String text) {
      DispoAnnotationData newAnnotation = new DispoAnnotationData();
      dataFactory.initAnnotation(newAnnotation);
      String idOfNewAnnotation = dataFactory.getNewId();
      newAnnotation.setId(idOfNewAnnotation);

      newAnnotation.setIsDefault(true);
      newAnnotation.setLocationRefs(location);
      newAnnotation.setResolutionType(resolutionType);
      newAnnotation.setResolution(coveringFile);
      newAnnotation.setLastResolution("N/A");
      newAnnotation.setIsResolutionValid(true);
      newAnnotation.setCustomerNotes(text);

      List<DispoAnnotationData> annotationsList = item.getAnnotationsList();
      int newIndex = annotationsList.size();
      newAnnotation.setIndex(newIndex);
      annotationsList.add(newIndex, newAnnotation);
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

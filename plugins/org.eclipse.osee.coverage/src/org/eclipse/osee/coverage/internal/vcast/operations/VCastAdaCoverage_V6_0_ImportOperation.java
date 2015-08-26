/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.internal.vcast.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.SimpleCoverageUnitFileContentsProvider;
import org.eclipse.osee.coverage.vcast.VCast60Params;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
 * @author Shawn F. Cook
 */
public class VCastAdaCoverage_V6_0_ImportOperation extends AbstractOperation {

   private static final Pattern fileMethodLineNumberPattern = Pattern.compile("\\s*([0-9]+)\\s+([0-9]+)\\s+([0-9]+)");

   private final CoverageImport coverageImport;
   private final VCast60Params input;

   /**
    * Examples of String input parameters
    * 
    * @param: vCastDir = "C:\directory\nav.nav.ftb1.wrk\vcast\"
    * @param: namespace = "nav"
    */
   public VCastAdaCoverage_V6_0_ImportOperation(VCast60Params input, CoverageImport coverageImport) {
      super("VectorCast Import (v6.0 SQLite)", Activator.PLUGIN_ID);
      this.input = input;
      this.coverageImport = coverageImport;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      File file = new File(input.getVCastDirectory() + input.getVCastDbPath());

      if (file.exists() && file.canRead()) {
         VCastDataStore dataStore = VCastClient.newDataStore(file.getAbsolutePath());

         Map<String, CoverageUnit> fileNumToCoverageUnit = new HashMap<String, CoverageUnit>();
         coverageImport.setCoverageUnitFileContentsProvider(new SimpleCoverageUnitFileContentsProvider());

         Collection<VCastInstrumentedFile> instrumentedFiles = getInstrumentedFiles(dataStore);
         for (VCastInstrumentedFile instrumentedFile : instrumentedFiles) {
            checkForCancelledStatus(monitor);
            processInstrumented(monitor, dataStore, fileNumToCoverageUnit, instrumentedFile);
         }

         Collection<VCastResult> results = getResultFiles(dataStore);
         for (VCastResult result : results) {
            checkForCancelledStatus(monitor);
            processResult(monitor, fileNumToCoverageUnit, result);
         }
         monitor.worked(1);
      } else {
         throw new OseeCoreException("The db file [%s] does not exist or is not readable", file.getAbsolutePath());
      }
   }

   private void processInstrumented(IProgressMonitor monitor, VCastDataStore dataStore, Map<String, CoverageUnit> fileNumToCoverageUnit, VCastInstrumentedFile instrumentedFile) throws Exception {
      VCastSourceFileJoin sourceFile = null;
      try {
         sourceFile = dataStore.getSourceFileJoin(instrumentedFile);
      } catch (OseeDataStoreException ex) {
         coverageImport.getLog().error(
            "SQL error while reading source_files for instrumented_file id:" + instrumentedFile.getId() + " " + ex.getMessage());
      }

      if (sourceFile != null) {
         monitor.setTaskName(String.format("File:%s ", sourceFile.getDisplayName()));
         CoverageUnit sourceFileCoverageUnit = coverageImport.createCoverageUnit(null, sourceFile.getDisplayName(), "");

         fileNumToCoverageUnit.put(String.valueOf(sourceFile.getUnitIndex()), sourceFileCoverageUnit);

         String lisFileName_badPath = instrumentedFile.getLISFile();
         if (!Strings.isValid(lisFileName_badPath)) {
            coverageImport.getLog().error(
               "Error: instrumented_file has invalid LIS_file value.  ID:(" + instrumentedFile.getId() + ")");
         }

         String normalizedPath = lisFileName_badPath.replaceAll("\\\\", "/");
         File f = new File(normalizedPath);
         String lisFileName = f.getName();

         VCastLisFileParser lisFileParser = new VCastLisFileParser(lisFileName, input.getVCastDirectory());
         sourceFileCoverageUnit.setFileContentsLoader(lisFileParser);
         String sourceFileNamespace = generateNamespace(input.getNamespace(), sourceFile.getDisplayName());
         CoverageUnit sourceFileParent = coverageImport.getOrCreateParent(sourceFileNamespace);
         if (sourceFileParent != null) {
            sourceFileParent.addCoverageUnit(sourceFileCoverageUnit);
         } else {
            coverageImport.addCoverageUnit(sourceFileCoverageUnit);
         }

         Collection<VCastFunction> functions = Collections.emptyList();
         try {
            functions = dataStore.getFunctions(instrumentedFile);
         } catch (OseeDataStoreException ex) {
            coverageImport.getLog().error(
               "SQL error while reading functions for instrumented_file id:" + instrumentedFile.getId() + " " + ex.getMessage());
         }
         for (VCastFunction function : functions) {
            checkForCancelledStatus(monitor);
            processFunction(monitor, dataStore, lisFileParser, instrumentedFile, sourceFileCoverageUnit, function);
         }
         monitor.worked(1);
      }
   }

   private void processFunction(IProgressMonitor monitor, VCastDataStore dataStore, VCastLisFileParser lisFileParser, VCastInstrumentedFile instrumentedFile, CoverageUnit sourceFileCoverageUnit, VCastFunction function) {
      CoverageUnit functionCoverageUnit =
         coverageImport.createCoverageUnit(sourceFileCoverageUnit, function.getName(), "");
      sourceFileCoverageUnit.addCoverageUnit(functionCoverageUnit);

      functionCoverageUnit.setOrderNumber(String.valueOf(function.getFindex()));
      Collection<VCastStatementCoverage> statementCoverageItems = Collections.emptyList();
      try {
         statementCoverageItems = dataStore.getStatementCoverageLines(function);
      } catch (OseeCoreException ex) {
         coverageImport.getLog().error(
            "SQL error while reading statement_coverages for instrumented_file id:" + instrumentedFile.getId() + " and function id: " + function.getId() + " " + ex.getMessage());
      }
      for (VCastStatementCoverage statementCoverageItem : statementCoverageItems) {
         checkForCancelledStatus(monitor);
         processStatement(monitor, lisFileParser, function, functionCoverageUnit, statementCoverageItem);
      }
   }

   private void processStatement(IProgressMonitor monitor, VCastLisFileParser lisFileParser, VCastFunction function, CoverageUnit functionCoverageUnit, VCastStatementCoverage statementCoverageItem) {
      checkForCancelledStatus(monitor);
      Integer functionNumber = function.getFindex();
      Integer lineNumber = statementCoverageItem.getLine();
      Pair<String, Boolean> lineData = null;
      try {
         lineData = lisFileParser.getSourceCodeForLine(functionNumber, lineNumber);
      } catch (OseeArgumentException ex) {
         coverageImport.getLog().error(
            String.format("Error(OseeArgumentException) parsing *.LIS file: [%s]. %s", function.getName(),
               ex.getMessage()));
      } catch (IOException ex) {
         coverageImport.getLog().error(
            String.format("Error(IOException) parsing *.LIS file: [%s]. %s", function.getName(), ex.getMessage()));
      }

      if (lineData != null) {
         CoverageItem lineCoverageItem =
            new CoverageItem(functionCoverageUnit, CoverageOptionManager.Not_Covered, lineNumber.toString());
         try {
            lineCoverageItem.setName(lineData.getFirst());
         } catch (OseeCoreException ex) {
            coverageImport.getLog().error(
               String.format("Error(OseeCoreException) when trying to set the line of code for %s", function.getName(),
                  ex.getMessage()));
         }

         if (input.isResolveExceptionHandling() && lineData.getSecond()) {
            lineCoverageItem.setCoverageMethod(CoverageOptionManager.Exception_Handling);
         }
         functionCoverageUnit.addCoverageItem(lineCoverageItem);
      }
   }

   private void processResult(IProgressMonitor monitor, Map<String, CoverageUnit> fileNumToCoverageUnit, VCastResult result) throws Exception {
      String resultPath = result.getPath();
      monitor.subTask(resultPath);
      String resultPathAbs = input.getVCastDirectory() + File.separator + resultPath;
      File resultsFile = new File(resultPathAbs);
      if (!resultsFile.exists()) {
         coverageImport.getLog().error(String.format("Error: Missing result *.DAT file: %s", resultPathAbs));
      } else {
         coverageImport.getImportRecordFiles().add(resultsFile);

         BufferedReader br = null;
         try {
            br = new BufferedReader(new FileReader(resultsFile));
            String resultsLine;
            while ((resultsLine = br.readLine()) != null) {
               checkForCancelledStatus(monitor);

               // Loop through results file and log coverageItem as Test_Unit for each entry
               if (Strings.isValid(resultsLine)) {
                  Result datFileSyntaxResult = VCastValidateDatFileSyntax.validateDatFileSyntax(resultsLine);
                  if (!datFileSyntaxResult.isTrue()) {
                     coverageImport.getLog().error(
                        String.format("Invalid VCast DAT file syntax - %s -  [%s] ", datFileSyntaxResult.getText(),
                           resultPath));
                  } else {
                     Matcher m = fileMethodLineNumberPattern.matcher(resultsLine);
                     if (m.find()) {
                        String fileNum = m.group(1);
                        String methodNum = m.group(2);
                        String executeNum = m.group(3);

                        CoverageUnit coverageUnit = fileNumToCoverageUnit.get(fileNum);
                        if (coverageUnit == null) {
                           coverageImport.getLog().error(
                              String.format("coverageUnit doesn't exist for unit_number [%s]", fileNum));
                        } else {
                           // Find or create new coverage item for method num /execution line
                           CoverageItem coverageItem = coverageUnit.getCoverageItem(methodNum, executeNum);
                           if (coverageItem == null) {
                              coverageImport.getLog().error(
                                 String.format(
                                    "Either Method [%s] or Line [%s] do not exist for Coverage Unit [%s] found in test unit vcast/results/.dat file [%s]",
                                    methodNum, executeNum, coverageUnit, resultPath));
                           } else {
                              coverageItem.setCoverageMethod(CoverageOptionManager.Test_Unit);
                              try {
                                 coverageItem.addTestUnitName(resultPath);
                              } catch (OseeCoreException ex) {
                                 coverageImport.getLog().error(
                                    String.format("Can't store test unit [%s] for coverageUnit [%s]; exception [%s]",
                                       resultPath, coverageUnit, ex.getLocalizedMessage()));
                              }
                           }
                        }
                     }
                  }
               }
            }
         } finally {
            Lib.close(br);
         }
      }
   }

   private String generateNamespace(String prefix, String filename) {
      StringBuffer sb = new StringBuffer();
      sb.append(prefix);
      if (!prefix.endsWith(".")) {
         sb.append(".");
      }
      String namespaceFilename = null;
      if (filename.endsWith(".c")) {
         namespaceFilename = "c_files." + filename;
         captureNamespace(namespaceFilename, 2, sb);
      } else {
         namespaceFilename = filename;
         captureNamespace(namespaceFilename, 3, sb);
      }
      return sb.toString().replaceFirst("\\.$", "");
   }

   private void captureNamespace(String filename, int namesOverLength, StringBuffer sb) {
      String[] names = filename.split("\\.");
      if (names.length > namesOverLength) {
         for (int x = 0; x < names.length - namesOverLength; x++) {
            sb.append(names[x]);
            sb.append(".");
         }
      }
   }

   private Collection<VCastInstrumentedFile> getInstrumentedFiles(VCastDataStore dataStore) {
      Collection<VCastInstrumentedFile> instrumentedFiles = new ArrayList<VCastInstrumentedFile>();
      try {
         //Note: the LIS_file field of the instrumentedFiles may have a fictious absolute path - but the path is ignored and only the file name is used.
         instrumentedFiles = dataStore.getAllInstrumentedFiles();
      } catch (OseeCoreException ex) {
         coverageImport.getLog().error("SQL error while reading instrumented_files " + ex.getMessage());
      }
      if (instrumentedFiles.isEmpty()) {
         coverageImport.getLog().warning("Warning: SQL lite table 'instrumented_files' is empty.");
      }
      return instrumentedFiles;
   }

   private Collection<VCastResult> getResultFiles(VCastDataStore dataStore) {
      Collection<VCastResult> results = null;
      try {
         results = dataStore.getAllResults();
      } catch (OseeCoreException ex) {
         coverageImport.getLog().error("SQL error while reading results " + ex.getMessage());
      }
      if (results.isEmpty()) {
         coverageImport.getLog().warning("Warning: SQL lite table 'results' is empty.");
      }
      return results;
   }

}

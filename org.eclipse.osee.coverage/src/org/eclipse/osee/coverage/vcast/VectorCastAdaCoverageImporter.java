/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.vcast;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.SimpleCoverageUnitFileContentsProvider;
import org.eclipse.osee.coverage.vcast.VcpResultsFile.ResultsValue;
import org.eclipse.osee.coverage.vcast.VcpSourceFile.SourceValue;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class VectorCastAdaCoverageImporter implements ICoverageImporter {

   private CoverageImport coverageImport;
   private final IVectorCastCoverageImportProvider vectorCastCoverageImportProvider;
   Pattern sourceLinePattern = Pattern.compile("^[0-9]+ [0-9]+(.*?)$");

   public VectorCastAdaCoverageImporter(IVectorCastCoverageImportProvider vectorCastCoverageImportProvider) {
      this.vectorCastCoverageImportProvider = vectorCastCoverageImportProvider;
   }

   @Override
   public CoverageImport run(IProgressMonitor progressMonitor) {
      coverageImport = new CoverageImport("VectorCast Import");
      coverageImport.setCoverageUnitFileContentsProvider(new SimpleCoverageUnitFileContentsProvider());

      if (!Strings.isValid(vectorCastCoverageImportProvider.getVCastDirectory())) {
         coverageImport.getLog().logError("VectorCast directory must be specified");
         return coverageImport;
      }

      File file = new File(vectorCastCoverageImportProvider.getVCastDirectory());
      if (!file.exists()) {
         coverageImport.getLog().logError(
               String.format("VectorCast directory doesn't exist [%s]",
                     vectorCastCoverageImportProvider.getVCastDirectory()));
         return coverageImport;
      }

      coverageImport.setImportDirectory(vectorCastCoverageImportProvider.getVCastDirectory());
      // Add config files to import record
      try {
         coverageImport.addImportRecordFile(new File(
               vectorCastCoverageImportProvider.getVCastDirectory() + "\\CCAST_.CFG"));
      } catch (Exception ex) {
         coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
      }
      try {
         coverageImport.addImportRecordFile(new File(
               vectorCastCoverageImportProvider.getVCastDirectory() + "\\build_info.xml"));
      } catch (Exception ex) {
         coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
      }

      VCastVcp vCastVcp = null;
      try {
         vCastVcp = new VCastVcp(vectorCastCoverageImportProvider.getVCastDirectory());
      } catch (Exception ex) {
         coverageImport.getLog().logError("Exception reading vcast.vcp file: " + ex.getLocalizedMessage());
         return coverageImport;
      }
      try {
         coverageImport.addImportRecordFile(vCastVcp.getFile());
      } catch (Exception ex) {
         coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
      }
      coverageImport.setLocation(vectorCastCoverageImportProvider.getVCastDirectory());

      // Create file and subprogram Coverage Units and execution line Coverage Items
      Map<String, CoverageUnit> fileNumToCoverageUnit = new HashMap<String, CoverageUnit>();
      Map<String, CoverageUnit> coverageNameToCoverageUnit = new HashMap<String, CoverageUnit>();
      Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram =
            new HashMap<CoverageUnit, CoverageDataSubProgram>();
      List<VcpSourceFile> vcpSourceFiles = vCastVcp.sourceFiles;
      if (progressMonitor != null) {
         progressMonitor.beginTask("Importing Source File Data", vcpSourceFiles.size());
      }
      int x = 1;
      for (VcpSourceFile vcpSourceFile : vCastVcp.sourceFiles) {
         String str =
               String.format("Processing VcpSourceFile %d/%d [%s]...", x++, vcpSourceFiles.size(), vcpSourceFile);
         //         System.out.println(str);
         if (progressMonitor != null) {
            progressMonitor.worked(1);
            progressMonitor.subTask(str);
         }
         try {
            CoverageDataFile coverageDataFile = vcpSourceFile.getCoverageDataFile();
            try {
               coverageImport.addImportRecordFile(coverageDataFile.getFile());
            } catch (Exception ex) {
               coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
            }
            for (CoverageDataUnit coverageDataUnit : coverageDataFile.getCoverageDataUnits()) {
               CoverageUnit fileCoverageUnit =
                     coverageImport.createCoverageUnit(null, vcpSourceFile.getValue(SourceValue.SOURCE_FILENAME), "");
               String fileNamespace = vectorCastCoverageImportProvider.getFileNamespace(coverageDataUnit.getName());
               fileCoverageUnit.setNamespace(fileNamespace);
               CoverageUnit parent = coverageImport.getOrCreateParent(fileCoverageUnit.getNamespace());
               if (parent != null) {
                  parent.addCoverageUnit(fileCoverageUnit);
               } else {
                  coverageImport.addCoverageUnit(fileCoverageUnit);
               }
               VcpSourceLisFile vcpSourceLisFile = vcpSourceFile.getVcpSourceLisFile();
               try {
                  coverageImport.addImportRecordFile(vcpSourceLisFile.getFile());
               } catch (Exception ex) {
                  coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
               }

               fileCoverageUnit.setFileContents(vcpSourceLisFile.getText());
               int methodNum = 0;
               for (CoverageDataSubProgram coverageDataSubProgram : coverageDataUnit.getSubPrograms()) {
                  methodNum++;
                  CoverageUnit methodCoverageUnit =
                        coverageImport.createCoverageUnit(fileCoverageUnit, coverageDataSubProgram.getName(), "");
                  // Store this mapping so can check covered/totals later
                  methodCoverageUnitToCoverageDataSubProgram.put(methodCoverageUnit, coverageDataSubProgram);
                  fileCoverageUnit.addCoverageUnit(methodCoverageUnit);
                  methodCoverageUnit.setOrderNumber(String.valueOf(methodNum));
                  for (LineNumToBranches lineNumToBranches : coverageDataSubProgram.getLineNumToBranches()) {
                     CoverageItem coverageItem =
                           new CoverageItem(methodCoverageUnit, CoverageOptionManager.Not_Covered,
                                 String.valueOf(lineNumToBranches.getLineNum()));
                     Pair<String, Boolean> lineData =
                           vcpSourceLisFile.getExecutionLine(String.valueOf(methodNum),
                                 String.valueOf(lineNumToBranches.getLineNum()));
                     String sourceLine = lineData.getFirst();
                     // Need to get rid of line method num and line num before storing
                     Matcher m = sourceLinePattern.matcher(sourceLine);
                     if (m.find()) {
                        coverageItem.setName(m.group(1));
                     } else {
                        coverageImport.getLog().logError(
                              String.format("Coverage line doesn't match \"n n <line>\" [%s].  " + sourceLine));
                        continue;
                     }
                     if (vectorCastCoverageImportProvider.isResolveExceptionHandling() && lineData.getSecond()) {
                        coverageItem.setCoverageMethod(CoverageOptionManager.Exception_Handling);
                     }
                     methodCoverageUnit.addCoverageItem(coverageItem);
                  }
               }
               fileNumToCoverageUnit.put(String.valueOf(coverageDataUnit.getIndex()), fileCoverageUnit);
               coverageNameToCoverageUnit.put(fileCoverageUnit.getName(), fileCoverageUnit);
            }
         } catch (Exception ex) {
            coverageImport.getLog().logError(
                  String.format("Error processing coverage for [%s].  " + ex.getLocalizedMessage(), vcpSourceFile));
            continue;
         }
      }

      // Process all results files and map to coverage units
      List<VcpResultsFile> vcpResultsFiles = vCastVcp.resultsFiles;
      if (progressMonitor != null) {
         progressMonitor.beginTask("Importing Test Unit Data", vcpResultsFiles.size());
      }
      x = 1;
      for (VcpResultsFile vcpResultsFile : vcpResultsFiles) {
         String str =
               String.format("Processing VcpResultsFile %d/%d [%s]...", x++, vcpResultsFiles.size(), vcpResultsFile);
         //         System.out.println(str);
         if (progressMonitor != null) {
            progressMonitor.worked(1);
            progressMonitor.subTask(str);
         }
         String testUnitName = vcpResultsFile.getValue(ResultsValue.FILENAME);
         try {
            coverageImport.addImportRecordFile(vcpResultsFile.getVcpResultsDatFile().getFile());
         } catch (Exception ex) {
            coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
         }

         try {
            for (String fileNum : vcpResultsFile.getVcpResultsDatFile().getFileNumbers()) {
               CoverageUnit coverageUnit = fileNumToCoverageUnit.get(fileNum);
               if (coverageUnit == null) {
                  coverageImport.getLog().logError(
                        String.format("coverageUnit doesn't exist for unit_number [%s]", fileNum));
                  continue;
               }
               for (Pair<String, HashSet<String>> methodExecutionPair : vcpResultsFile.getVcpResultsDatFile().getMethodExecutionPairs(
                     fileNum)) {
                  String methodNum = methodExecutionPair.getFirst();
                  Set<String> executeNums = methodExecutionPair.getSecond();
                  for (String executeNum : executeNums) {
                     // Find or create new coverage item for method num /execution line
                     CoverageItem coverageItem = coverageUnit.getCoverageItem(methodNum, executeNum);
                     if (coverageItem == null) {
                        coverageImport.getLog().logError(
                              String.format("Can't retrieve method [%s] from coverageUnit [%s] for test unit [%s]",
                                    methodNum, coverageUnit, testUnitName));
                     } else {
                        coverageItem.setCoverageMethod(CoverageOptionManager.Test_Unit);
                        try {
                           coverageItem.addTestUnitName(testUnitName);
                        } catch (OseeCoreException ex) {
                           coverageImport.getLog().logError(
                                 String.format("Can't store test unit [%s] for coverageUnit [%s]; exception [%s]",
                                       testUnitName, coverageUnit, ex.getLocalizedMessage()));

                        }
                     }
                  }
               }
            }
         } catch (Exception ex) {
            coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
         }
      }

      // Validate VectorCast covered/total from <unit>.xml files with imported results files above
      for (Entry<CoverageUnit, CoverageDataSubProgram> entry : methodCoverageUnitToCoverageDataSubProgram.entrySet()) {
         CoverageUnit methodCoverageUnit = entry.getKey();
         CoverageDataSubProgram coverageDataSubProgram = entry.getValue();
         if (methodCoverageUnit.getCoverageItems(false).size() != coverageDataSubProgram.getTotal()) {
            coverageImport.getLog().logError(
                  String.format(
                        "Imported number of lines [%s] doesn't match VectorCast number of lines [%s] for coverage unit [%s]",
                        methodCoverageUnit.getCoverageItems(false).size(), coverageDataSubProgram.getTotal(),
                        methodCoverageUnit));
         }
         if (methodCoverageUnit.getCoverageItemsCovered(false, CoverageOptionManager.Test_Unit).size() != coverageDataSubProgram.getCovered()) {
            coverageImport.getLog().logError(
                  String.format(
                        "Imported covered items [%s] doesn't match VectorCast covered items [%s] for coverage unit [%s]",
                        methodCoverageUnit.getCoverageItems(false).size(), coverageDataSubProgram.getCovered(),
                        methodCoverageUnit));
         }
      }

      try {
         coverageImport.getLog().log("\nPerforming Aggregate <-> Import Verification");
         // Retrieve and process Aggregate file compared with import results
         VCastAggregateReport report = new VCastAggregateReport(vectorCastCoverageImportProvider.getVCastDirectory());
         for (AggregateCoverageUnitResult result : report.getResults()) {
            //            System.out.println(result);
            CoverageUnit coverageUnit = coverageNameToCoverageUnit.get(result.getName());
            if (coverageUnit == null) {
               coverageImport.getLog().logError(
                     String.format("Aggregate Check: Can't locate Coverage Unit for Aggregate unit [%s]",
                           result.getName()));
            } else {
               int importCuItems = coverageUnit.getCoverageItems(true).size();
               int importCuCovered =
                     coverageUnit.getCoverageItemsCovered(true, CoverageOptionManagerDefault.Test_Unit).size();
               if (result.getNumLines() != importCuItems || result.getNumCovered() != importCuCovered) {
                  coverageImport.getLog().logError(
                        String.format(
                              "Aggregate Check: Unit [%s] Import [%d] of [%d] doesn't match Aggregate [%d] of [%d]",
                              result.getName(), importCuCovered, importCuItems, result.getNumCovered(),
                              result.getNumLines()));
               }
            }
         }
         coverageImport.getLog().log("Completed Aggregate <-> Import Verification");

      } catch (Exception ex) {
         coverageImport.getLog().logError("\nError Processing Aggregate File: " + ex.getLocalizedMessage());
      }

      return coverageImport;
   }

   @Override
   public String getName() {
      return "VectorCast Import";
   }

}

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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.SimpleCoverageUnitFileContentsProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class VectorCastAdaCoverageImporter implements ICoverageImporter {

   private CoverageImport coverageImport;
   private final IVectorCastCoverageImportProvider vectorCastCoverageImportProvider;

   public VectorCastAdaCoverageImporter(IVectorCastCoverageImportProvider vectorCastCoverageImportProvider) {
      this.vectorCastCoverageImportProvider = vectorCastCoverageImportProvider;
   }

   @Override
   public CoverageImport run(IProgressMonitor progressMonitor) throws OseeCoreException {
      coverageImport = new CoverageImport("VectorCast Import");
      coverageImport.setCoverageUnitFileContentsProvider(new SimpleCoverageUnitFileContentsProvider());

      if (!validateImportParameters()) {
         return null;
      }
      importRecord__AddConfigFiles();

      VCastVcp vCastVcp = createVCastVcp();
      if (vCastVcp == null) {
         return coverageImport;
      }

      importRecord_addVCastVcpFile(vCastVcp);
      coverageImport.setLocation(vectorCastCoverageImportProvider.getVCastDirectory());

      // Create file and subprogram Coverage Units and execution line Coverage Items
      Map<String, CoverageUnit> fileNumToCoverageUnit = new ConcurrentHashMap<String, CoverageUnit>();
      Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram =
         new ConcurrentHashMap<CoverageUnit, CoverageDataSubProgram>();

      processVcpSourceFiles(progressMonitor, vCastVcp, fileNumToCoverageUnit,
         methodCoverageUnitToCoverageDataSubProgram);
      // garbage collect
      System.gc();

      processVcpResultsFiles(progressMonitor, vCastVcp, fileNumToCoverageUnit);
      fileNumToCoverageUnit.clear();
      fileNumToCoverageUnit = null;
      // garbage collect
      System.gc();

      verifyCoveredTotalFromXmlWithVCastVcpAndResultsDir(progressMonitor, methodCoverageUnitToCoverageDataSubProgram);
      methodCoverageUnitToCoverageDataSubProgram.clear();
      methodCoverageUnitToCoverageDataSubProgram = null;
      // garbage collect
      System.gc();

      VCastAggregateReportValidator report = new VCastAggregateReportValidator();
      report.run(progressMonitor, this);
      // garbage collect
      System.gc();

      verifyNumberOfVcpResultsFilesAndDatFiles(progressMonitor, vCastVcp);

      vCastVcp = null;
      // garbage collect
      System.gc();

      return coverageImport;
   }

   public void verifyCoveredTotalFromXmlWithVCastVcpAndResultsDir(IProgressMonitor progressMonitor, Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram) {
      if (progressMonitor != null) {
         progressMonitor.beginTask("Verifing Covered Total From .xml with VCastVcp and Results Files", 1);
      }
      // Verifying VectorCast covered/total items from vcast/<unit>.xml with data imported from results read from vcast.vcp and results dir
      coverageImport.getLog().log(
         "\nVerifying VectorCast covered/total items from vcast/<unit>.xml with data imported from results read from vcast.vcp and results dir");
      boolean error = false;
      for (Entry<CoverageUnit, CoverageDataSubProgram> entry : methodCoverageUnitToCoverageDataSubProgram.entrySet()) {
         CoverageUnit methodCoverageUnit = entry.getKey();
         CoverageDataSubProgram coverageDataSubProgram = entry.getValue();
         String usevectorcast53 = System.getProperty("usevectorcast53", null);
         if (Strings.isValid(usevectorcast53)) {
            int totalCoverageItems = methodCoverageUnit.getCoverageItems(false).size();
            int coveredCoverageItems = methodCoverageUnit.getCoverageItemsCount(false, CoverageOptionManager.Test_Unit);
            if (totalCoverageItems != coverageDataSubProgram.getTotal() || coveredCoverageItems != coverageDataSubProgram.getCovered()) {
               coverageImport.getLog().logError(
                  String.format(
                     "Imported covered/total items [%d/%d] doesn't match VectorCast [%d/%d] reported in .xml file for coverage unit [%s]",
                     coveredCoverageItems, totalCoverageItems, coverageDataSubProgram.getCovered(),
                     coverageDataSubProgram.getTotal(), methodCoverageUnit));
               error = true;
            }
         }
      }
      if (!error) {
         coverageImport.getLog().log("Ok");
      }
      if (progressMonitor != null) {
         progressMonitor.worked(1);
      }
   }

   public void verifyNumberOfVcpResultsFilesAndDatFiles(IProgressMonitor progressMonitor, VCastVcp vCastVcp) {
      if (progressMonitor != null) {
         progressMonitor.beginTask("Verifing Number of vcast.vcp results files and .dat files", 1);
      }
      // Verifying number results files reported in vcast.vcp with vcast/results/*.dat files
      coverageImport.getLog().log(
         "\nVerifying number results files reported in vcast.vcp with vcast/results/*.dat files");
      int numVcastVcpDatFiles = vCastVcp.getResultsFiles().size();
      List<String> filenames =
         Lib.readListFromDir(
            new File(
               vectorCastCoverageImportProvider.getVCastDirectory() + File.separator + "vcast" + File.separator + "results" + File.separator),
            new MatchFilter(".*\\.DAT"), false);
      if (numVcastVcpDatFiles != filenames.size()) {
         coverageImport.getLog().logError(
            String.format(
               "Number of results files in Vcast.vcp [%d] doesn't match number of vcast/results/*.dat files [%d]",
               numVcastVcpDatFiles, filenames.size()));
      } else {
         coverageImport.getLog().log("Ok");
      }
      if (progressMonitor != null) {
         progressMonitor.worked(1);
      }
   }

   public void processVcpResultsFiles(IProgressMonitor progressMonitor, VCastVcp vCastVcp, Map<String, CoverageUnit> fileNumToCoverageUnit) throws OseeCoreException {
      // Process all results files and map to coverage units
      if (progressMonitor != null) {
         progressMonitor.beginTask("Importing Test Unit Data", vCastVcp.resultsFiles.size());
      }

      ImportWorkerFactory factory =
         new VcpResultFilesWorkerFactory(vCastVcp.resultsFiles, fileNumToCoverageUnit, progressMonitor);

      workerHelper(factory);
   }

   private List<Object> workerHelper(ImportWorkerFactory workerFactory) throws OseeCoreException {
      //int numProcessors = Runtime.getRuntime().availableProcessors() * 2;
      int numProcessors = 1;
      int partitionSize = workerFactory.getListSize() / numProcessors;
      int remainder = workerFactory.getListSize() % numProcessors;
      AtomicInteger numberProcessed = new AtomicInteger(1);
      ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
      int startIndex = 0;
      int endIndex = 0;
      List<Object> toReturn = new LinkedList<Object>();

      try {
         Collection<Callable<Object>> workers = new LinkedList<Callable<Object>>();
         for (int i = 0; i < numProcessors; i++) {
            startIndex = endIndex;
            endIndex = startIndex + partitionSize;
            if (i == 0) {
               endIndex += remainder;
            }
            workers.add(workerFactory.createWorker(startIndex, endIndex, numberProcessed));
         }
         try {
            for (Future<Object> future : executor.invokeAll(workers)) {
               toReturn.add(future.get());
            }

         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      } finally {
         executor.shutdown();
      }
      return toReturn;
   }

   public void processVcpSourceFiles(IProgressMonitor progressMonitor, VCastVcp vCastVcp, Map<String, CoverageUnit> fileNumToCoverageUnit, Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram) throws OseeCoreException {
      if (progressMonitor != null) {
         progressMonitor.beginTask("Importing Source File Data", vCastVcp.sourceFiles.size());
      }

      ImportWorkerFactory factory =
         new VcpSourceFilesWorkerFactory(progressMonitor, vCastVcp.sourceFiles,
            methodCoverageUnitToCoverageDataSubProgram, fileNumToCoverageUnit);

      workerHelper(factory);
   }

   public VCastVcp createVCastVcp() {
      VCastVcp vCastVcp = null;
      try {
         vCastVcp = new VCastVcp(vectorCastCoverageImportProvider.getVCastDirectory());
      } catch (Exception ex) {
         coverageImport.getLog().logError("Exception reading vcast.vcp file: " + ex.getLocalizedMessage());
         return null;
      }
      return vCastVcp;
   }

   public void importRecord_addVCastVcpFile(VCastVcp vCastVcp) {
      try {
         coverageImport.addImportRecordFile(vCastVcp.getFile());
      } catch (Exception ex) {
         coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
      }
   }

   public void importRecord__AddConfigFiles() {
      // Add config files to import record
      try {
         coverageImport.addImportRecordFile(new File(
            vectorCastCoverageImportProvider.getVCastDirectory() + File.separator + "CCAST_.CFG"));
      } catch (Exception ex) {
         coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
      }
      try {
         coverageImport.addImportRecordFile(new File(
            vectorCastCoverageImportProvider.getVCastDirectory() + File.separator + "vcast" + File.separator + "build_info.xml"));
      } catch (Exception ex) {
         coverageImport.getLog().logError("Error Adding Import Record File: " + ex.getLocalizedMessage());
      }
   }

   public boolean validateImportParameters() {
      if (!Strings.isValid(vectorCastCoverageImportProvider.getVCastDirectory())) {
         coverageImport.getLog().logError("VectorCast directory must be specified");
         return false;
      }

      File file = new File(vectorCastCoverageImportProvider.getVCastDirectory());
      if (!file.exists()) {
         coverageImport.getLog().logError(
            String.format("VectorCast directory doesn't exist [%s]",
               vectorCastCoverageImportProvider.getVCastDirectory()));
         return false;
      }

      coverageImport.setImportDirectory(vectorCastCoverageImportProvider.getVCastDirectory());
      return true;
   }

   /**
    * VectorCast does not put breakout information for coverage units that have no coverage. Check for this case so we
    * don't show lots of errors.
    */
   protected boolean isVectorCastIgnoreCase(String notes, Integer importCuCovered, Integer aggregateNumCovered) {
      return notes.equals("No Coverage Data Exists") && importCuCovered == 0 && aggregateNumCovered == null;
   }

   @Override
   public String getName() {
      return "VectorCast Import";
   }

   public CoverageImport getCoverageImport() {
      return coverageImport;
   }

   public String getVcastDirectory() {
      return vectorCastCoverageImportProvider.getVCastDirectory();
   }

   private class VcpSourceFileWorker implements Callable<Object> {

      private final IProgressMonitor progressMonitor;
      private final List<VcpSourceFile> filesToProcess;
      private final int totalSize;
      private final AtomicInteger numberProcessed;
      private final Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram;
      private final Map<String, CoverageUnit> fileNumToCoverageUnit;
      private final Pattern sourceLinePattern = Pattern.compile("^[0-9]+ [0-9]+(.*?)$");

      public VcpSourceFileWorker(IProgressMonitor progressMonitor, List<VcpSourceFile> filesToProcess, int totalSize, AtomicInteger numberProcessed, Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram, Map<String, CoverageUnit> fileNumToCoverageUnit) {
         this.progressMonitor = progressMonitor;
         this.filesToProcess = filesToProcess;
         this.totalSize = totalSize;
         this.numberProcessed = numberProcessed;
         this.methodCoverageUnitToCoverageDataSubProgram = methodCoverageUnitToCoverageDataSubProgram;
         this.fileNumToCoverageUnit = fileNumToCoverageUnit;
      }

      @Override
      public Object call() throws Exception {
         for (VcpSourceFile vcpSourceFile : filesToProcess) {
            // System.out.println(str);
            if (progressMonitor != null) {
               progressMonitor.worked(1);
               StringBuilder str = new StringBuilder();
               str.append("Processing VcpSourceFile ");
               str.append(numberProcessed.getAndIncrement());
               str.append("/");
               str.append(totalSize);
               progressMonitor.subTask(str.toString());
            }
            try {
               CoverageDataFile coverageDataFile = null;
               try {
                  coverageDataFile = vcpSourceFile.getCoverageDataFile(coverageImport);
               } catch (Exception ex) {
                  String errorStr =
                     String.format(
                        "Can't process vcast/<code file>.xml file for source file [%s] exception [%s] (see Error Log)",
                        vcpSourceFile.getFilename(), ex.getLocalizedMessage());
                  coverageImport.getLog().logError(AHTML.textToHtml(errorStr));
                  OseeLog.log(Activator.class, Level.SEVERE, errorStr, ex);
                  continue;
               }
               try {
                  coverageImport.addImportRecordFile(coverageDataFile.getFile());
               } catch (Exception ex) {
                  coverageImport.getLog().logError(
                     "Error Adding Import Record File (see Error Log): " + ex.getLocalizedMessage());
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
               for (CoverageDataUnit coverageDataUnit : coverageDataFile.getCoverageDataUnits()) {
                  // Create CoverageUnit object to represent single <code file>.xml
                  CoverageUnit fileCoverageUnit =
                     coverageImport.createCoverageUnit(null, vcpSourceFile.getFilename(), "");
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
                     coverageImport.getLog().logError(
                        "Error Adding Import Record File (see Error Log): " + ex.getLocalizedMessage());
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }

                  fileCoverageUnit.setFileContentsLoader(vcpSourceLisFile);
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
                              String.format("Coverage line doesn't match \"n n <line>\" [%s].  ", sourceLine));
                           continue;
                        }
                        if (vectorCastCoverageImportProvider.isResolveExceptionHandling() && lineData.getSecond()) {
                           coverageItem.setCoverageMethod(CoverageOptionManager.Exception_Handling);
                        }
                        methodCoverageUnit.addCoverageItem(coverageItem);
                     }
                  }
                  fileNumToCoverageUnit.put(String.valueOf(coverageDataUnit.getIndex()), fileCoverageUnit);
               }
            } catch (Exception ex) {
               coverageImport.getLog().logError(
                  String.format("Error processing coverage for [%s].  " + ex.getLocalizedMessage(), vcpSourceFile));
               continue;
            }
            vcpSourceFile.cleanup();
         }
         return null;
      }

   }

   private class VcpResultFilesWorkerFactory implements ImportWorkerFactory {
      private final List<VcpResultsFile> vcpResultsFiles;
      private final IProgressMonitor progressMonitor;
      private final Map<String, CoverageUnit> fileNumToCoverageUnit;

      public VcpResultFilesWorkerFactory(List<VcpResultsFile> vcpResultsFiles, Map<String, CoverageUnit> fileNumToCoverageUnit, IProgressMonitor progressMonitor) {
         this.vcpResultsFiles = vcpResultsFiles;
         this.progressMonitor = progressMonitor;
         this.fileNumToCoverageUnit = fileNumToCoverageUnit;
      }

      @Override
      public int getListSize() {
         return vcpResultsFiles.size();
      }

      @Override
      public Callable<Object> createWorker(int startIndex, int endIndex, AtomicInteger numberProcessed) {
         return new VcpResultsFileWorker(vcpResultsFiles.subList(startIndex, endIndex), fileNumToCoverageUnit,
            progressMonitor, numberProcessed, getListSize());
      }
   }

   private class VcpResultsFileWorker implements Callable<Object> {

      private final List<VcpResultsFile> vcpResultsFiles;
      private final IProgressMonitor progressMonitor;
      private final Map<String, CoverageUnit> fileNumToCoverageUnit;
      private final AtomicInteger numberProcessed;
      private final int totalSize;

      public VcpResultsFileWorker(List<VcpResultsFile> vcpResultsFiles, Map<String, CoverageUnit> fileNumToCoverageUnit, IProgressMonitor progressMonitor, AtomicInteger numberProcessed, int totalSize) {
         this.vcpResultsFiles = vcpResultsFiles;
         this.progressMonitor = progressMonitor;
         this.fileNumToCoverageUnit = fileNumToCoverageUnit;
         this.numberProcessed = numberProcessed;
         this.totalSize = totalSize;
      }

      @Override
      public Object call() throws Exception {
         for (VcpResultsFile vcpResultsFile : vcpResultsFiles) {
            //         System.out.println(str);
            if (progressMonitor != null) {
               StringBuilder str = new StringBuilder();
               str.append("Processing VcpResultsFile ");
               str.append(numberProcessed.getAndIncrement());
               str.append("/");
               str.append(totalSize);
               progressMonitor.worked(1);
               progressMonitor.subTask(str.toString());
            }

            vcpResultsFile.processResultsFiles(coverageImport, fileNumToCoverageUnit);
         }
         return null;
      }

   }

   private class VcpSourceFilesWorkerFactory implements ImportWorkerFactory {

      private final IProgressMonitor progressMonitor;
      private final List<VcpSourceFile> filesToProcess;
      private final Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram;
      private final Map<String, CoverageUnit> fileNumToCoverageUnit;

      public VcpSourceFilesWorkerFactory(IProgressMonitor progressMonitor, List<VcpSourceFile> filesToProcess, Map<CoverageUnit, CoverageDataSubProgram> methodCoverageUnitToCoverageDataSubProgram, Map<String, CoverageUnit> fileNumToCoverageUnit) {
         this.progressMonitor = progressMonitor;
         this.filesToProcess = filesToProcess;
         this.methodCoverageUnitToCoverageDataSubProgram = methodCoverageUnitToCoverageDataSubProgram;
         this.fileNumToCoverageUnit = fileNumToCoverageUnit;
      }

      @Override
      public int getListSize() {
         return filesToProcess.size();
      }

      @Override
      public Callable<Object> createWorker(int startIndex, int endIndex, AtomicInteger numberProcessed) {
         return new VcpSourceFileWorker(progressMonitor, filesToProcess.subList(startIndex, endIndex),
            filesToProcess.size(), numberProcessed, methodCoverageUnitToCoverageDataSubProgram, fileNumToCoverageUnit);
      }
   }

   private interface ImportWorkerFactory {

      public int getListSize();

      public Callable<Object> createWorker(int startIndex, int endIndex, AtomicInteger numberProcessed);

   }
}

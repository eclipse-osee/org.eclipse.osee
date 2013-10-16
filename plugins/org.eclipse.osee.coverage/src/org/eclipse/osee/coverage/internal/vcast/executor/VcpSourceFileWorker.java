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
package org.eclipse.osee.coverage.internal.vcast.executor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.internal.vcast.model.CoverageDataSubProgram;
import org.eclipse.osee.coverage.internal.vcast.model.CoverageDataUnit;
import org.eclipse.osee.coverage.internal.vcast.model.LineNumToBranches;
import org.eclipse.osee.coverage.internal.vcast.operations.VcpSourceFile;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.util.LineData;
import org.eclipse.osee.coverage.vcast.CoverageImportData;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class VcpSourceFileWorker extends AbstractVcpFileWorker<VcpSourceFile> {

   private static final Pattern SOURCE_LINE_PATTERN = Pattern.compile("^[0-9]+ [0-9]+(.*?)$");

   private final CoverageImport coverageImport;
   private final CoverageImportData input;
   private final Map<VcpSourceFile, VcpSourceLisFile> sourceToFileList;
   private final Map<VcpSourceFile, CoverageDataFileParser> sourceToDataParser;

   private final Matcher sourceLineMatcher;

   public VcpSourceFileWorker(XResultData logger, IProgressMonitor monitor, AtomicInteger numberProcessed, int totalSize, List<VcpSourceFile> toProcess, List<File> processed, Map<String, CoverageUnit> fileNumToCoverageUnit, CoverageImport coverageImport, CoverageImportData input, Map<VcpSourceFile, VcpSourceLisFile> sourceToFileList, Map<VcpSourceFile, CoverageDataFileParser> sourceToDataParser) {
      super(logger, monitor, numberProcessed, totalSize, toProcess, processed, fileNumToCoverageUnit);
      this.coverageImport = coverageImport;
      this.input = input;
      this.sourceToFileList = sourceToFileList;
      this.sourceToDataParser = sourceToDataParser;
      sourceLineMatcher = SOURCE_LINE_PATTERN.matcher("");
   }

   @Override
   protected void process(VcpSourceFile vcpSourceFile) throws Exception {
      try {
         // coverageDataFile is the *.xml file - path: vcast/*.xml
         CoverageDataFileParser coverageDataFile = null;
         try {
            coverageDataFile = getCoverageDataFile(vcpSourceFile, coverageImport);
         } catch (Exception ex) {
            String errorStr =
               String.format(
                  "Can't process vcast/<code file>.xml file for source file [%s] exception [%s] (see Error Log)",
                  vcpSourceFile.getFilename(), ex.getLocalizedMessage());
            getLogger().logError(AHTML.textToHtml(errorStr));
            OseeLog.log(Activator.class, Level.SEVERE, errorStr, ex);
         }

         if (coverageDataFile != null) {
            try {
               getProcessed().add(coverageDataFile.getFile());
            } catch (Exception ex) {
               getLogger().logError("Error Adding Import Record File (see Error Log): " + ex.getLocalizedMessage());
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }

            //coverageDataUnit is the <unit>...</unit> data in the *.xml file
            //    The <unit> file represents a single source file and includes the methods and their info too
            //    NOTE: This for-loop is unneeded -  there is always one and only one <unit> in an *.xml file
            List<CoverageDataUnit> coverageDataUnits = coverageDataFile.getCoverageDataUnits();
            for (CoverageDataUnit coverageDataUnit : coverageDataUnits) {
               processCoverageDataUnit(vcpSourceFile, coverageDataUnit);
            }
            vcpSourceFile.cleanup();
         }
      } catch (Exception ex) {
         getLogger().logError(
            String.format("Error processing coverage for [%s].  " + ex.getLocalizedMessage(), vcpSourceFile));
      }
   }

   private void processCoverageDataUnit(VcpSourceFile vcpSourceFile, CoverageDataUnit coverageDataUnit) throws OseeCoreException {
      //fileCoverageUnit is the *.ada source code file
      CoverageUnit fileCoverageUnit = coverageImport.createCoverageUnit(null, vcpSourceFile.getFilename(), "");
      String fileNamespace = input.getFileNamespace(coverageDataUnit.getName());
      fileCoverageUnit.setNamespace(fileNamespace);
      CoverageUnit parent = coverageImport.getOrCreateParent(fileCoverageUnit.getNamespace());
      if (parent != null) {
         parent.addCoverageUnit(fileCoverageUnit);
      } else {
         coverageImport.addCoverageUnit(fileCoverageUnit);
      }

      VcpSourceLisFile vcpSourceLisFile = getVcpSourceLisFile(vcpSourceFile);
      try {
         getProcessed().add(vcpSourceLisFile.getFile());
      } catch (Exception ex) {
         getLogger().logError("Error Adding Import Record File (see Error Log): " + ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      fileCoverageUnit.setFileContentsLoader(vcpSourceLisFile);

      //coverageDataSubProgram is the <subprogram>...</subprogram> item in the <unit> in *.xml file
      //    Corresponds to the method
      int methodNum = 0;
      for (CoverageDataSubProgram coverageDataSubProgram : coverageDataUnit.getSubPrograms()) {
         methodNum++;
         CoverageUnit methodCoverageUnit =
            coverageImport.createCoverageUnit(fileCoverageUnit, coverageDataSubProgram.getName(), "");
         fileCoverageUnit.addCoverageUnit(methodCoverageUnit);
         methodCoverageUnit.setOrderNumber(String.valueOf(methodNum));
         for (LineNumToBranches lineNumToBranches : coverageDataSubProgram.getLineNumToBranches()) {
            CoverageItem coverageItem =
               new CoverageItem(methodCoverageUnit, CoverageOptionManager.Not_Covered,
                  String.valueOf(lineNumToBranches.getLineNum()));
            LineData lineData =
               vcpSourceLisFile.getExecutionLine(String.valueOf(methodNum),
                  String.valueOf(lineNumToBranches.getLineNum()));
            String sourceLine = lineData.getLineText();
            // Need to get rid of line method num and line num before storing
            sourceLineMatcher.reset(sourceLine);
            if (!sourceLineMatcher.find()) {
               getLogger().logError(String.format("Coverage line doesn't match \"n n <line>\" [%s].  ", sourceLine));
            } else {
               coverageItem.setLineNumber(lineData.getLineNumber());
               coverageItem.setName(sourceLineMatcher.group(1));
               if (input.isResolveExceptionHandling() && lineData.getIsException()) {
                  coverageItem.setCoverageMethod(CoverageOptionManager.Exception_Handling);
               }
               methodCoverageUnit.addCoverageItem(coverageItem);
            }
         }
      }
      getFileNumToCoverageUnit().put(String.valueOf(coverageDataUnit.getIndex()), fileCoverageUnit);
   }

   public VcpSourceLisFile getVcpSourceLisFile(VcpSourceFile vcpSourceFile) {
      VcpSourceLisFile vcpSourceLisFile = sourceToFileList.get(vcpSourceFile);
      if (vcpSourceLisFile == null) {
         vcpSourceLisFile = new VcpSourceLisFile(vcpSourceFile);
      }
      return vcpSourceLisFile;
   }

   public CoverageDataFileParser getCoverageDataFile(VcpSourceFile vcpSourceFile, CoverageImport coverageImport) throws OseeCoreException {
      CoverageDataFileParser coverageDataFileParser = sourceToDataParser.get(vcpSourceFile);
      if (coverageDataFileParser == null) {
         coverageDataFileParser =
            new CoverageDataFileParser(
               coverageImport,
               vcpSourceFile.getvCastVcp().getVCastDirectory() + File.separator + "vcast" + File.separator + vcpSourceFile.getFilename().replaceAll(
                  "\\.(ada|adb|c)$", "\\.xml"));
      }
      return coverageDataFileParser;
   }

}